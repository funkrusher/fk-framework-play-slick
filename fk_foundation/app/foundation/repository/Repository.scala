package foundation.repository

import foundation.dtos.QueryParamDTO
import foundation.dtos.QueryParamFilterDTO
import foundation.dtos.QueryParamSorterDTO
import play.api.Logger
import play.api.db.slick.HasDatabaseConfigProvider
import slick.jdbc.JdbcProfile
import slick.lifted.AbstractTable
import slick.lifted.CanBeQueryCondition
import slick.lifted.ColumnOrdered
import slick.ast.Ordering
import slick.sql.SqlAction

import java.lang.reflect.Method
import scala.collection.mutable
import scala.reflect.runtime.universe._
import scala.collection.mutable.{ Map => MutableMap }

abstract class Repository extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val logger: Logger = Logger(this.getClass())

  val NEVER_TRUE  = LiteralColumn(1) === LiteralColumn(0)
  val ALWAYS_TRUE = LiteralColumn(1) === LiteralColumn(1)

  case class Sorter[T: TypeTag](tableQuery: TableQuery[_], columnName: String) {

    val table = tableQuery.baseTableRow.asInstanceOf[Table[_]]

    def reflector: Method = prepareReflector(table, columnName)
  }

  case class Filter[T: TypeTag](tableQuery: TableQuery[_], columnName: String, filterComparator: String) {
    val table = tableQuery.baseTableRow.asInstanceOf[Table[_]]

    def filterType = typeOf[T]

    def reflector: Method = prepareReflector(table, columnName)
  }

  def createConditionList(
      filters: Seq[(Filter[_], QueryParamFilterDTO)],
      table: AnyRef,
  ): List[Rep[Option[Boolean]]] = {
    filters
      .map(orFilter => {
        val filter       = orFilter._2
        val columnFilter = orFilter._1
        val obj          = columnFilter.reflector.invoke(table)
        RepFilterResolver.resolve(obj, filter.filterValue, orFilter._1.filterType, orFilter._1.filterComparator)
      })
      .toList
  }

  def createConditions(
      activeFilters: Option[Seq[(Filter[_], QueryParamFilterDTO)]],
      table: AnyRef,
  ): Rep[Option[Boolean]] = {
    if (activeFilters.isDefined && activeFilters.get.size > 0) {
      val list                               = createConditionList(activeFilters.get, table)
      val andCondition: Rep[Option[Boolean]] = list.reduceLeftOption((x, y) => x && y).getOrElse(NEVER_TRUE.?)
      andCondition
    } else {
      ALWAYS_TRUE.?
    }
  }

  def createSorting(
      activeSorter: Option[(Sorter[_], QueryParamSorterDTO)],
      table: AnyRef,
  ): Either[java.lang.Throwable, ColumnOrdered[Any]] = {
    if (activeSorter.isDefined) {
      val sorter       = activeSorter.get._2
      val columnSorter = activeSorter.get._1
      val obj          = columnSorter.reflector.invoke(table)
      Right(RepSortByResolver.resolve(obj, sorter.sortOrder))
    } else {
      Left(new Exception("active sorter could not be found in query-params."))
    }
  }

  def resolveActiveFilters(
      tableQueryFilters: Seq[QueryParamFilterDTO],
      columnFilters: Seq[Filter[_]],
  ): Seq[(Filter[_], QueryParamFilterDTO)] = {
    val result = tableQueryFilters
      .map(tableQueryFilter => {
        val maybeFound = columnFilters.find(x =>
          x.columnName == tableQueryFilter.filterName && x.filterComparator == tableQueryFilter.filterComparator && x.table.tableName == tableQueryFilter.tableName
        )
        maybeFound match {
          case Some(found: Filter[_]) => Some((found, tableQueryFilter))
          case _                      => None
        }
      })
      .collect({ case Some(it) => it })
    result
  }

  def resolveActiveSorter(
      tableQuerySorter: QueryParamSorterDTO,
      columnSorters: Seq[Sorter[_]],
  ) = {
    val maybeFound = columnSorters.find(x =>
      x.columnName == tableQuerySorter.sortName && x.table.tableName == tableQuerySorter.tableName
    )
    maybeFound match {
      case Some(found: Sorter[_]) => Some((found, tableQuerySorter))
      case _                      => None
    }
  }

  implicit class FilterQuery[A <: AbstractTable[_]](q: TableQuery[A]) {
    def applyFilters(qParam: QueryParamDTO, filters: Seq[Filter[_]]): Query[A, Any, Seq] = {
      val table = q.baseTableRow.asInstanceOf[Table[_]]

      // find filters
      val resolvedActiveFilters =
        qParam.filters.map(qFilters => resolveActiveFilters(qFilters, filters)).getOrElse(Seq())

      var resultQuery: Query[A, Any, Seq] = q.filter(x => ALWAYS_TRUE).asInstanceOf[Query[A, Any, Seq]]

      if (resolvedActiveFilters.size > 0) {
        resultQuery = resultQuery
          .filter(filterTable => {
            createConditions(Some(resolvedActiveFilters), filterTable)
          })
          .asInstanceOf[Query[A, Any, Seq]]
      }
      resultQuery
    }
  }

  implicit class SortQuery[E <: Product, U <: Product](q: Query[E, U, Seq]) {
    def applySorter(qParam: QueryParamDTO, sorters: Seq[Sorter[_]]): Query[E, U, Seq] = {
      qParam.sorter match {
        case Some(sorter) =>
          q.sortBy(container => {
            val list: List[Table[_]] = container.productIterator.map(_.asInstanceOf[Table[_]]).toList

            val resolvedActiveSorter = resolveActiveSorter(sorter, sorters)

            val maybeTable = list.find(x => {
              x.tableName == sorter.tableName
            })
            maybeTable match {
              case Some(table) =>
                createSorting(resolvedActiveSorter, table) match {
                  case Left(ex)      => null
                  case Right(sorter) => sorter
                }
              case _ =>
                null
            }
          })
        case _ => q
      }
    }
  }

  implicit class PaginateQuery[A](q: Query[Rep[A], A, Seq]) {
    def paginate(qParam: QueryParamDTO): Query[Rep[A], A, Seq] = {
      var query = q
      query = if (qParam.drop.isDefined) query.drop(qParam.drop.get) else query
      query = if (qParam.take.isDefined) query.take(qParam.take.get) else query
      query
    }
  }

  implicit class FixForGroupingWithKeepOrdering[A](q: Query[Rep[A], A, Seq]) {
    def resultWithFixOrderedAndGrouped(): SqlAction[Seq[A], Streaming[A], Effect.Read] = {
      val overridenStatements = q.result.statements.map(statement => {
        statement.replace("order by", "group by `id` order by")
      })
      q.result.overrideStatements(overridenStatements)
    }
  }

  object RepSortByResolver {
    def resolve(obj: AnyRef, sortOrder: String): ColumnOrdered[Any] = {
      val ordering = sortOrder match {
        case "asc"  => new Ordering().asc
        case "desc" => new Ordering().desc
        case _ =>
          logger.error(s"""Unexpected ordering $sortOrder for given object! Falling back to ascending order.""")
          new Ordering().asc
      }
      val rep = obj.asInstanceOf[Rep[Any]]
      val res = ColumnOrdered(rep, ordering);
      res
    }
  }

  object RepFilterResolver {
    private def asString(rep: Rep[String], value: String, filterComparator: String): Rep[Option[Boolean]] = {
      asOptionString(Rep.Some(rep), value, filterComparator)
    }

    private def asOptionString(
        rep: Rep[Option[String]],
        value: String,
        filterComparator: String,
    ): Rep[Option[Boolean]] =
      filterComparator match {
        case "null"      => rep.isEmpty.?
        case "not_null"  => rep.isDefined.?
        case "in"        => rep.inSet(value.split(",").toList)
        case "like"      => rep.like(value)
        case "equal"     => rep === value
        case "not_equal" => rep =!= value
        case _ =>
          logger.error(s"""Unexpected condition-case for string object!""")
          NEVER_TRUE.?
      }

    private def asInt(rep: Rep[Int], value: String, filterComparator: String): Rep[Option[Boolean]] = {
      asOptionInt(Rep.Some(rep), value, filterComparator)
    }

    private def asOptionInt(rep: Rep[Option[Int]], value: String, filterComparator: String): Rep[Option[Boolean]] =
      filterComparator match {
        case "null"          => rep.isEmpty.?
        case "not_null"      => rep.isDefined.?
        case "in"            => rep.inSet(value.split(",").map(_.toInt).toList)
        case "equal"         => rep === value.toInt
        case "not_equal"     => rep =!= value.toInt
        case "greater"       => rep > value.toInt
        case "lesser"        => rep < value.toInt
        case "greater_equal" => rep >= value.toInt
        case "lesser_equal"  => rep <= value.toInt
        case _ =>
          logger.error(s"""Unexpected condition-case for int object!""")
          NEVER_TRUE.?
      }

    def resolve(obj: AnyRef, value: String, t: Type, filterComparator: String): Rep[Option[Boolean]] =
      t match {
        case t if t =:= typeOf[Option[String]] =>
          asOptionString(obj.asInstanceOf[Rep[Option[String]]], value, filterComparator)
        case t if t =:= typeOf[String]      => asString(obj.asInstanceOf[Rep[String]], value, filterComparator)
        case t if t =:= typeOf[Option[Int]] => asOptionInt(obj.asInstanceOf[Option[Int]], value, filterComparator)
        case t if t =:= typeOf[Int]         => asInt(obj.asInstanceOf[Rep[Int]], value, filterComparator)
        case _ =>
          logger.error(s"""Unexpected type for given object!""")
          NEVER_TRUE.?
      }
  }

  /**
   * Prepare the reflector that can be used to resolve a value in the given table for the given columnName
   *
   * @param table      table
   * @param columnName columnName
   * @return reflector or exception
   */
  def prepareReflector(table: AbstractTable[_], columnName: String): Method = {
    try {
      table.getClass.getMethod(columnName)
    } catch {
      case ex: Exception =>
        val message = s"""Column $columnName in table ${table.tableName} not found!"""
        logger.error(message, ex)
        throw new RuntimeException(message)
    }
  }

}
