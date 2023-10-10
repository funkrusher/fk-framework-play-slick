package library.managers

import core.error.MappingError
import library.dtos.AuthorDTO
import library.dtos.AuthorPaginateDTO
import core.manager.Manager
import core.persistence.DbRunner
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import core.util.QueryParamModel
import library.daos.AuthorDAO
import library.daos.BookDAO
import library.daos.BookStoreDAO
import library.repositories.AuthorRepository

@Singleton
class AuthorManager @Inject() (
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
    DbRunner.run(authorRepository.fetch(authorIds))
  }

  def paginate(qParam: QueryParamModel): Future[Either[MappingError, AuthorPaginateDTO]] = {
    DbRunner.run(authorRepository.paginate(qParam))
  }

  def delete(authorId: Long): Future[Either[MappingError, Int]] = {
    val action = (for {
      _            <- bookRowDAO.deleteByAuthor(authorId)
      authorDelete <- authorRowDAO.delete(authorId)
    } yield authorDelete).transactionally
    DbRunner.run(action).map(Right(_))
  }

  def insert(author: AuthorDTO): Future[Either[MappingError, AuthorDTO]] = {
    DbRunner
      .run(authorRowDAO.insertAndReturn(author.toRow()))
      .map(x => Right(AuthorDTO.fromRow(x)))
  }

}
