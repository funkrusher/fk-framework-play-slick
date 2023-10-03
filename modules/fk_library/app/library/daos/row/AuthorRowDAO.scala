package library.daos.row

import dao.

import play.api.db.slick.DatabaseConfigProvider
import tables.Tables
import tables.Tables._

import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.ExecutionContext

@Singleton
class AuthorRowDAO @Inject() (protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
    extends RowDAO[Author, AuthorRow, Int] {

  import profile.api._

}
