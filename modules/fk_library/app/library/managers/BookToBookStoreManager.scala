package library.managers

import library.daos.row.BookToBookStoreDAO
import library.dtos.BookToBookStoreDTO
import core.manager.Manager
import core.manager.errors.ManagerError
import core.persistence.DbRunner
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

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
