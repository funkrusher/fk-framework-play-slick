package library.daos.row

import daos.RowDAO
import play.api.db.slick.DatabaseConfigProvider
import tables.Tables._

import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.ExecutionContext

@Singleton
class AuthorRowDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext) extends RowDAO[Author, AuthorRow, Int] {

  import profile.api._

  override def tableQuery = tables.Tables.Author

  override def id = Id[Int](tableQuery, "id")

  def insertAndReturn(row: AuthorRow) = {
    ((Author returning Author.map(_.id) into ((u, insertId) => row.copy(id = insertId))) += row)
  }
}
