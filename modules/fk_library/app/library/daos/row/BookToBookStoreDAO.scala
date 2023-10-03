package library.daos.row

import core.dao.MultiKeyRowDAO
import play.api.db.slick.DatabaseConfigProvider
import core.tables.Tables._

import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.ExecutionContext

@Singleton
class BookToBookStoreDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit
    ec: ExecutionContext
) extends MultiKeyRowDAO[BookToBookStore, BookToBookStoreRow, (String, Int)] {

  import profile.api._

  override def config: MultiKeyQueryConfig =
    new MultiKeyQueryConfig(BookToBookStore) {

      override def filterId(q: RowQuery, id: RowId): FilteredRowQuery =
        q.filter(x => x.name === id._1 && x.book_id === id._2)

      override def getIdFromRow(row: Row): RowId = (row.name, row.book_id)
    }
}
