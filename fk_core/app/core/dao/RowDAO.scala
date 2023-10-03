package core.dao

import slick.lifted.AbstractTable

abstract class RowDAO[V <: AbstractTable[Row], Row <: V#TableElementType, I <: Any] extends DAO {

  import profile.api._

  def builder: RowQueryBuilder

  trait RowQueryBuilder {
    type Q = TableQuery[V]

    type R = Row

    type QResult = Query[Table[Row], Table[Row]#TableElementType, Seq]

    type IdQResult = Query[Rep[I], I, Seq]

    def query: Q

    // ---------------------------------------
    // Abstract Functions (to be implemented!)
    // ---------------------------------------

    def idQuery(query: Q, id: Option[I]): QResult

    def idSetQuery(query: Q, ids: Seq[I]): QResult

    def idMapQuery(query: Q): IdQResult

    def idFromRow(row: R): Option[I]

    def idToRow(id: Option[I], row: R): R

  }

  // ------------------------------------
  // Functions that are already available
  // ------------------------------------

  def select(id: I): DBIO[Option[Row]] = builder.idQuery(builder.query, Some(id)).result.headOption

  def selectAll(ids: Seq[I]): DBIO[Seq[Row]] = builder.idSetQuery(builder.query, ids).result

  def insert(row: Row): DBIO[Int] = builder.query += row

  def insertAll(rows: Seq[Row]): DBIO[Option[Int]] = builder.query ++= rows

  def insertAndReturn(row: Row) =
    builder.query.returning(builder.idMapQuery(builder.query)).into((_, id) => builder.idToRow(Some(id), row)) += row

  def delete(id: I): DBIO[Int] = builder.idQuery(builder.query, Some(id)).delete

  def update(row: Row): DBIO[Int] = builder.idQuery(builder.query, builder.idFromRow(row)).update(row)

  def toRowMap[A, B](seq: Seq[(A, B)]): Map[A, Seq[B]] = {
    // create a grouped map, which is grouped by an id and contains a seq of items for each id
    seq.groupBy(_._1).map(x => (x._1, x._2.map(_._2)))
  }

}
