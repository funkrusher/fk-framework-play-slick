package daos

import slick.lifted.AbstractTable

import java.lang.reflect.Method
import scala.reflect.runtime.universe._

abstract class RowDAO[V <: AbstractTable[T], T <: V#TableElementType, X: TypeTag] extends DAO {

  import profile.api._

  def id: Id[X]

  def tableQuery: TableQuery[V]


  case class Id[U: TypeTag](tableQuery: TableQuery[_], columnName: String) {
    val table = tableQuery.baseTableRow.asInstanceOf[Table[_]]

    def idType = typeOf[U]

    def reflector: Method = prepareReflector(table, columnName)
  }

  def resolveIdValue(obj: Any): X = id.reflector.invoke(obj).asInstanceOf[X]


  def resolveIdFilter(searchId: X, filterSource: Any): Rep[Option[Boolean]] = {
    val obj = id.reflector.invoke(filterSource)
    RepFilterResolver.resolve(obj, searchId.toString, typeOf[X], "equal")
  }

  def resolveIdInSetFilter(searchIds: Seq[X], filterSource: Any): Rep[Option[Boolean]] = {
    val obj = id.reflector.invoke(filterSource)
    RepFilterResolver.resolve(obj, searchIds.mkString(","), typeOf[X], "in")
  }


  def select(id: X): DBIO[Option[V#TableElementType]] = {
    val query = tableQuery.filter(filterSource => {
      resolveIdFilter(id, filterSource)
    })
    val result: DBIO[Option[V#TableElementType]] = query.result.headOption
    result
  }


  def selectAll(ids: Seq[X]): DBIO[Seq[V#TableElementType]] = {
    val query = tableQuery.filter(filterSource => {
      resolveIdInSetFilter(ids, filterSource)
    })
    val result: DBIO[Seq[V#TableElementType]] = query.result
    result
  }

  def insert(row: T): DBIO[Int] = tableQuery += row

  def insertReturnId(row: T): DBIO[X] = {
    val tq = tableQuery
    val tx = id.idType match {
      case t if t =:= typeOf[Option[String]] => tq.map(id.reflector.invoke(_).asInstanceOf[Rep[Option[String]]])
      case t if t =:= typeOf[String] => tq.map(id.reflector.invoke(_).asInstanceOf[Rep[String]])
      case t if t =:= typeOf[Option[Int]] => tq.map(id.reflector.invoke(_).asInstanceOf[Rep[Option[Int]]])
      case t if t =:= typeOf[Int] => tq.map(id.reflector.invoke(_).asInstanceOf[Rep[Int]])
      case t if t =:= typeOf[Option[Long]] => tq.map(id.reflector.invoke(_).asInstanceOf[Rep[Option[Long]]])
      case t if t =:= typeOf[Long] => tq.map(id.reflector.invoke(_).asInstanceOf[Rep[Long]])
      case _ => tq
    }
    ((tq returning tx) into ((_, idValue) => idValue.asInstanceOf[X]) += row)
  }


  def insertAll(rows: Seq[T]): DBIO[Option[Int]] = tableQuery ++= rows

  def delete(id: X): DBIO[Int] = {
    val query = tableQuery.filter(filterSource => {
      resolveIdFilter(id, filterSource)
    }).asInstanceOf[Query[Table[T], Table[T]#TableElementType, Seq]]
    query.delete
  }

  def update(row: T): DBIO[Int] = {
    val query = tableQuery.filter(filterSource => {
      val value = resolveIdValue(row)
      resolveIdFilter(value, filterSource)
    })
    query.update(row)
  }


  def toRowMap[A, B](seq: Seq[(A, B)]): Map[A, Seq[B]] = {
    // create a grouped map, which is grouped by an id and contains a seq of items for each id
    seq.groupBy(_._1).map(x => (x._1, x._2.map(_._2)))
  }
}
