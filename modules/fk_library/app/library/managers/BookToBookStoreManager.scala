package library.managers

import library.daos.row.AuthorRowDAO
import library.daos.row.BookRowDAO
import library.daos.row.BookStoreRowDAO
import library.daos.row.BookToBookStoreDAO
import library.daos.view.AuthorViewDAO
import library.dtos.AuthorDTO
import library.dtos.AuthorPaginateDTO
import library.dtos.BookDTO
import library.dtos.BookStoreDTO
import library.dtos.BookToBookStoreDTO
import manager.Manager
import manager.errors.ManagerError
import persistence.DbRunner
import persistence.errors.DbError
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile
import util.QueryParamModel

import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

@Singleton
class BookToBookStoreManager @Inject() (
    bookToBookStoreDAO: BookToBookStoreDAO
)(implicit ec: ExecutionContext, dbConfigProvider: DatabaseConfigProvider)
    extends Manager {

  // We want the JdbcProfile for this provider
  // it must be defined as protected because we return DBIO as result.
  protected val dbConfig = dbConfigProvider.get[JdbcProfile]

  def update(bookToBookStore: BookToBookStoreDTO): Future[Either[ManagerError, BookToBookStoreDTO]] = {
    DbRunner.run(bookToBookStoreDAO.update(bookToBookStore.toRow())).map {
      case Left(error)  => Left(toManagerError(error))
      case Right(count) => Right(bookToBookStore)
    }
  }

}
