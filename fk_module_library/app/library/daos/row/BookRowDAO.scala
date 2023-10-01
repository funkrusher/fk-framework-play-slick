package library.daos.row

import daos.RowDAO
import play.api.db.slick.DatabaseConfigProvider
import tables.Tables._

import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.ExecutionContext

@Singleton
class BookRowDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends RowDAO[Book, BookRow, Int] {

  import profile.api._

  override def tableQuery = tables.Tables.Book

  override def id = Id[Int](tableQuery, "id")

  def fetchBooksByAuthorId(authorIds: Seq[Int]): DBIO[Map[Int, Seq[BookRow]]] = {
    val action = for {
      book <- Book.filter(_.author_id.inSet(authorIds))
    } yield (book.author_id, book)
    action.result.map(toRowMap)
  }
}
