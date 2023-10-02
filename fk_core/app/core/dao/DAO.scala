package daos

import play.api.Logger
import play.api.db.slick.HasDatabaseConfigProvider
import slick.ast.Ordering
import slick.jdbc.JdbcProfile
import slick.lifted.AbstractTable
import slick.lifted.ColumnOrdered

import java.lang.reflect.Method
import scala.reflect.runtime.universe._

abstract class DAO extends HasDatabaseConfigProvider[JdbcProfile] {

  import profile.api._

  val logger: Logger = Logger(this.getClass())

  val NEVER_TRUE  = LiteralColumn(1) === LiteralColumn(0)
  val ALWAYS_TRUE = LiteralColumn(1) === LiteralColumn(1)

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

}
