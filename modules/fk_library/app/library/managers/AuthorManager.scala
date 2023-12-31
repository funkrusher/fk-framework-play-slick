package library.managers

import foundation.dtos.QueryParamDTO
import library.dtos.AuthorDTO
import library.dtos.AuthorPaginateDTO
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import foundation.error.MappingError
import foundation.manager.Manager
import foundation.persistence.DbRunner
import library.daos.AuthorDAO
import library.daos.BookDAO
import library.daos.BookStoreDAO
import library.repositories.AuthorRepository

@Singleton
class AuthorManager @Inject() (
    dbRunner: DbRunner,
    authorRepository: AuthorRepository,
    authorRowDAO: AuthorDAO,
    bookRowDAO: BookDAO,
)(implicit ec: ExecutionContext, dbConfigProvider: DatabaseConfigProvider)
    extends Manager {

  // We want the JdbcProfile for this provider
  // it must be defined as protected because we return DBIO as result.
  protected val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  def fetch(authorIds: Seq[Long]): Future[Either[MappingError, Seq[AuthorDTO]]] = {
    dbRunner.run(authorRepository.fetch(authorIds))
  }

  def paginate(qParam: QueryParamDTO): Future[Either[MappingError, AuthorPaginateDTO]] = {
    dbRunner.run(authorRepository.paginate(qParam))
  }

  def delete(authorId: Long): Future[Either[MappingError, Int]] = {
    val action = (for {
      _            <- bookRowDAO.deleteByAuthor(authorId)
      authorDelete <- authorRowDAO.delete(authorId)
    } yield authorDelete).transactionally
    dbRunner.run(action).map(Right(_))
  }

  def insert(author: AuthorDTO): Future[Either[MappingError, AuthorDTO]] = {
    dbRunner
      .run(authorRowDAO.insertAndReturn(author.toRow()))
      .map(x => Right(AuthorDTO.fromRow(x)))
  }

}
