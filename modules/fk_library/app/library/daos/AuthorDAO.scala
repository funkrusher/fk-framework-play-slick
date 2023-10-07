package library.daos

import core.dao.SingleKeyDAO
import core.tables.Tables._
import play.api.db.slick.DatabaseConfigProvider

import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.ExecutionContext

@Singleton
class AuthorDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
    extends SingleKeyDAO[Author, AuthorRow, Int] {

  import profile.api._

  override def config: SingleKeyQueryConfig =
    new SingleKeyQueryConfig(Author) {

      override def filterId(q: RowQuery, id: RowId): FilteredRowQuery = q.filter(_.id === id)

      override def filterIds(q: RowQuery, ids: Seq[RowId]): FilteredRowQuery = q.filter(_.id.inSet(ids))

      override def mapId(q: RowQuery): RowIdQuery = q.map(_.id)

      override def putIdToRow(id: RowId, row: Row): Row = row.copy(id = Some(id))

      override def getIdFromRow(row: Row): RowId = row.id.get
    }
}
