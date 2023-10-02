package daos

import core.dao.ReflectionHelper
import slick.lifted.AbstractTable
import slick.lifted.ColumnOrdered
import util.QueryParamFilterModel
import util.QueryParamModel
import util.QueryParamSorterModel

import java.lang.reflect.Method
import scala.reflect.runtime.universe._

abstract class ViewDAO extends DAO {

  import profile.api._

  case class Sorter[T: TypeTag](tableQuery: TableQuery[_], columnName: String) {

    val table = tableQuery.baseTableRow.asInstanceOf[Table[_]]

    def reflector: Method = ReflectionHelper.prepareReflector(table, columnName)
  }

  case class Filter[T: TypeTag](tableQuery: TableQuery[_], columnName: String, filterComparator: String) {
    val table = tableQuery.baseTableRow.asInstanceOf[Table[_]]

    def filterType = typeOf[T]

    def reflector: Method = ReflectionHelper.prepareReflector(table, columnName)
  }

  def createConditionList(
      filters: Seq[(Filter[_], QueryParamFilterModel)],
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
      activeFilters: Option[Seq[(Filter[_], QueryParamFilterModel)]],
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
      activeSorter: Option[(Sorter[_], QueryParamSorterModel)],
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
      tableQueryFilters: Seq[QueryParamFilterModel],
      columnFilters: Seq[Filter[_]],
  ): Seq[(Filter[_], QueryParamFilterModel)] = {
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
      tableQuerySorter: QueryParamSorterModel,
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
    def applyFilters(qParam: QueryParamModel, filters: Seq[Filter[_]]): Query[A, Any, Seq] = {
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
    def applySorter(qParam: QueryParamModel, sorters: Seq[Sorter[_]]): Query[E, U, Seq] = {
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
                  case Left(ex) =>
                    logger.error("whassssssss")
                    null
                  case Right(sorter) =>
                    logger.error("whassssssss22222")
                    sorter
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
    def paginate(qParam: QueryParamModel): DBIO[Seq[A]] = {
      var query = q
      query = if (qParam.drop.isDefined) query.drop(qParam.drop.get) else query
      query = if (qParam.take.isDefined) query.take(qParam.take.get) else query

      val result = query.result

      val statements = result.statements

      val dumpInfo = result.getDumpInfo

      val overridenStatements = statements.map(statement => {
        statement.replace("order by", "group by `id` order by")
      })
      val overriden = result.overrideStatements(overridenStatements)

      overriden
    }
  }
}
