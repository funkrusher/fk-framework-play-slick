package library.daos

import core.dao.SingleKeyDAO
import core.tables.Tables._
import play.api.db.slick.DatabaseConfigProvider

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class BookStoreDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
    extends SingleKeyDAO[BookStore, BookStoreRow, String] {

  import profile.api._

  override def config: SingleKeyQueryConfig =
    new SingleKeyQueryConfig(BookStore) {

      override def filterId(q: RowQuery, id: RowId): FilteredRowQuery = q.filter(_.name === id)

      override def filterIds(q: RowQuery, ids: Seq[RowId]): FilteredRowQuery = q.filter(_.name.inSet(ids))

      override def mapId(q: RowQuery): RowIdQuery = q.map(_.name)

      override def putIdToRow(id: RowId, row: Row): Row = row.copy(name = id)

      override def getIdFromRow(row: Row): RowId = row.name
    }

  def fetchBookStoresByBookId(authorIds: Seq[Int]): DBIO[Map[Int, Seq[BookStoreRow]]] = {
    val action = for {
      bookId    <- Book.filter(_.author_id.inSet(authorIds)).map(_.id)
      relation  <- BookToBookStore.filter(_.book_id === bookId)
      bookStore <- BookStore.filter(_.name === relation.name)
    } yield (relation.book_id, bookStore)
    action.result.map(toRowMap)
  }
}
