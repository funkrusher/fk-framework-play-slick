package library.managers

import foundation.error.MappingError
import foundation.manager.Manager
import library.dtos.BookToBookStoreDTO
import foundation.persistence.{DbRun, DbRunner}
import library.daos.BookToBookStoreDAO
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

@Singleton
class BookToBookStoreManager @Inject() (
    dbRun: DbRun,
    bookToBookStoreDAO: BookToBookStoreDAO,
)(implicit ec: ExecutionContext, dbConfigProvider: DatabaseConfigProvider)
    extends Manager {

  // We want the JdbcProfile for this provider
  // it must be defined as protected because we return DBIO as result.
  protected val dbConfig = dbConfigProvider.get[JdbcProfile]

  def update(bookToBookStore: BookToBookStoreDTO): Future[Either[MappingError, BookToBookStoreDTO]] = {
    dbRun.run(bookToBookStoreDAO.update(bookToBookStore.toRow())).map(x => Right(bookToBookStore))
  }

}
