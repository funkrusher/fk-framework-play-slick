package core.dao

import slick.lifted.AbstractTable

abstract class SingleKeyDAO[V <: AbstractTable[R], R <: V#TableElementType, I <: Any] extends DAO[V, R, I] {

  import profile.api._

  // ---------------------------------------
  // Abstract Functions (to be implemented!)
  // ---------------------------------------
  override def config: SingleKeyQueryConfig

  abstract class SingleKeyQueryConfig(q: TableQuery[V]) extends QueryConfig(q) {
    def filterIds(q: RowQuery, ids: Seq[RowId]): FilteredRowQuery

    def mapId(q: RowQuery): RowIdQuery

    def putIdToRow(id: RowId, row: Row): Row
  }

  // ------------------------------------
  // Functions that are already available
  // ------------------------------------

  def selectAll(ids: Seq[I]): DBIO[Seq[Row]] = config.filterIds(config.query, ids).result

  def insertAndReturn(row: Row) =
    config.query.returning(config.mapId(config.query)).into((_, id) => config.putIdToRow(id, row)) += row
}
