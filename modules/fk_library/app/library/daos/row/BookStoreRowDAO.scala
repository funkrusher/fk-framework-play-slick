package library.daos.row

import daos.RowDAO
import play.api.db.slick.DatabaseConfigProvider

import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.ExecutionContext
import tables.Tables._

@Singleton
class BookStoreRowDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends RowDAO[BookStore, BookStoreRow, String] {

  import profile.api._

  override def tableQuery = tables.Tables.BookStore

  override def id = Id[String](tableQuery, "name")

  def fetchBookStoresByBookId(authorIds: Seq[Int]): DBIO[Map[Int, Seq[BookStoreRow]]] = {
    val action = for {
      bookId <- Book.filter(_.author_id.inSet(authorIds)).map(_.id)
      relation <- BookToBookStore.filter(_.book_id === bookId)
      bookStore <- BookStore.filter(_.name === relation.name)
    } yield (relation.book_id, bookStore)
    action.result.map(toRowMap)
  }
}
