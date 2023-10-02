package library.daos.row

import daos.RowDAO
import play.api.db.slick.DatabaseConfigProvider
import tables.Tables._

import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.ExecutionContext

@Singleton
class BookToBookStoreDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit
    ec: ExecutionContext
) extends RowDAO[BookToBookStore, BookToBookStoreRow, (String, Int)] {

  import profile.api._

  override def tableQuery = tables.Tables.BookToBookStore

  override def id = Id[(String, Int)](tableQuery, "name")

}
