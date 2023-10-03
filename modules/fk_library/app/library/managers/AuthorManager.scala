package library.managers

import library.daos.row.AuthorRowDAO
import library.daos.row.BookRowDAO
import library.daos.row.BookStoreRowDAO
import library.daos.view.AuthorViewDAO
import library.dtos.AuthorDTO
import library.dtos.AuthorPaginateDTO
import library.dtos.BookDTO
import library.dtos.BookStoreDTO
import core.manager.Manager
import core.manager.errors.ManagerError
import core.persistence.DbRunner
import core.persistence.errors.DbError
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import core.util.QueryParamModel

@Singleton
class AuthorManager @Inject() (
    authorRowDAO: AuthorRowDAO,
    bookRowDAO: BookRowDAO,
    authorViewDAO: AuthorViewDAO,
    bookStoreRowDAO: BookStoreRowDAO,
)(implicit ec: ExecutionContext, dbConfigProvider: DatabaseConfigProvider)
    extends Manager {

  // We want the JdbcProfile for this provider
  // it must be defined as protected because we return DBIO as result.
  protected val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  def fetch(authorIds: Seq[Int]): Future[Either[ManagerError, Seq[AuthorDTO]]] = {
    val action = for {
      authors       <- authorRowDAO.selectAll(authorIds)
      booksMap      <- bookRowDAO.fetchBooksByAuthorId(authorIds)
      bookStoresMap <- bookStoreRowDAO.fetchBookStoresByBookId(authorIds)
    } yield (authors, booksMap, bookStoresMap)

    DbRunner.run(action).map {
      case Left(error) => Left(toManagerError(error))
      case Right((authors, booksMap, bookStoresMap)) =>
        val authorsSorted = authors.sortBy(item => authorIds.indexOf(item.id))
        Right(authorsSorted.map { author =>
          // gather all related data for this author, with help of the grouped maps.
          val books = booksMap.getOrElse(author.id.get, Seq.empty)
          val bookApis = books.map(book => {
            val bookStores = bookStoresMap.getOrElse(book.id.get, Seq.empty)
            BookDTO.fromRow(book).copy(bookStores = Some(bookStores.map(BookStoreDTO.fromRow)))
          })
          AuthorDTO.fromRow(author).copy(books = Some(bookApis))
        })
    }
  }

  def paginate(qParam: QueryParamModel): Future[Either[ManagerError, AuthorPaginateDTO]] = {
    val action = for {
      ids   <- authorViewDAO.paginate(qParam)
      count <- authorViewDAO.paginateCount(qParam)
    } yield (ids, count)

    DbRunner.run(action).flatMap {
      case Left(error) => Future.successful(Left(toManagerError(error)))
      case Right((ids, count)) =>
        fetch(ids).map {
          case Left(error)    => Left(error)
          case Right(authors) => Right(AuthorPaginateDTO(authors, count))
        }
    }
  }

  def paginateCount(qParam: QueryParamModel) =
    DbRunner.run(authorViewDAO.paginateCount(qParam))

  def delete(authorId: Int): Future[Either[ManagerError, Int]] = {
    val action = (for {
      bookDelete   <- bookRowDAO.deleteByAuthor(authorId)
      authorDelete <- authorRowDAO.delete(authorId)
    } yield (bookDelete, authorDelete)).transactionally

    DbRunner.run(action).map {
      case Left(error)   => Left(toManagerError(error))
      case Right(result) => Right(result._2)
    }
  }

  def insert(author: AuthorDTO): Future[Either[ManagerError, AuthorDTO]] = {
    DbRunner.run(authorRowDAO.insertAndReturn(author.toRow())).map {
      case Left(error)   => Left(toManagerError(error))
      case Right(result) => Right(AuthorDTO.fromRow(result))
    }
  }

}
