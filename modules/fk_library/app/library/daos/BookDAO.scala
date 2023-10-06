package library.daos

import core.dao.SingleKeyDAO
import core.tables.Tables._
import play.api.db.slick.DatabaseConfigProvider

import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.ExecutionContext

@Singleton
class BookDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
    extends SingleKeyDAO[Book, BookRow, Int] {

  import profile.api._

  override def config: SingleKeyQueryConfig =
    new SingleKeyQueryConfig(Book) {

      override def filterId(q: RowQuery, id: RowId): FilteredRowQuery = q.filter(_.id === id)

      override def filterIds(q: RowQuery, ids: Seq[RowId]): FilteredRowQuery = q.filter(_.id.inSet(ids))

      override def mapId(q: RowQuery): RowIdQuery = q.map(_.id)

      override def putIdToRow(id: RowId, row: Row): Row = row.copy(id = Some(id))

      override def getIdFromRow(row: Row): RowId = row.id.get
    }

  def fetchBooksByAuthorId(authorIds: Seq[Int]): DBIO[Map[Int, Seq[BookRow]]] = {
    val action = for {
      book <- Book.filter(_.author_id.inSet(authorIds))
    } yield (book.author_id, book)
    action.result.map(toRowMap)
  }

  def deleteByAuthor(authorId: Int) = {
    Book.filter(_.author_id === authorId).delete
  }
}
