package library.daos

import core.dao.MultiKeyDAO
import core.tables.Tables._
import play.api.db.slick.DatabaseConfigProvider

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class BookToBookStoreDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit
    ec: ExecutionContext
) extends MultiKeyDAO[BookToBookStore, BookToBookStoreRow, (String, Int)] {

  import profile.api._

  override def config: MultiKeyQueryConfig =
    new MultiKeyQueryConfig(BookToBookStore) {

      override def filterId(q: RowQuery, id: RowId): FilteredRowQuery =
        q.filter(x => x.name === id._1 && x.book_id === id._2)

      override def getIdFromRow(row: Row): RowId = (row.name, row.book_id)
    }
}
