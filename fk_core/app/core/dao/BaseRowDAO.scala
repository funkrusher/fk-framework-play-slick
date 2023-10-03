package core.dao

import slick.lifted.AbstractTable

abstract class BaseRowDAO[V <: AbstractTable[R], R <: V#TableElementType, I <: Any] extends DAO {

  import profile.api._

  type Row = R

  type RowId = I

  type RowQuery = TableQuery[V]

  type FilteredRowQuery = Query[V, Table[Row]#TableElementType, Seq]

  type RowIdQuery = Query[Rep[RowId], RowId, Seq]

  // ---------------------------------------
  // Abstract Functions (to be implemented!)
  // ---------------------------------------
  def config: QueryConfig

  abstract class QueryConfig(q: TableQuery[V]) {

    val query = q

    def filterId(q: RowQuery, id: RowId): FilteredRowQuery

    def getIdFromRow(row: Row): RowId
  }

  // ------------------------------------
  // Functions that are already available
  // ------------------------------------

  def select(id: I): DBIO[Option[Row]] = config.filterId(config.query, id).result.headOption

  def insert(row: Row): DBIO[Int] = config.query += row

  def insertAll(rows: Seq[Row]): DBIO[Option[Int]] = config.query ++= rows

  def delete(id: I): DBIO[Int] =
    config.filterId(config.query, id).asInstanceOf[Query[Table[Row], Table[Row], Seq]].delete

  def update(row: Row): DBIO[Int] = config.filterId(config.query, config.getIdFromRow(row)).update(row)

  def toRowMap[A, B](seq: Seq[(A, B)]): Map[A, Seq[B]] = {
    // create a grouped map, which is grouped by an id and contains a seq of items for each id
    seq.groupBy(_._1).map(x => (x._1, x._2.map(_._2)))
  }

}
