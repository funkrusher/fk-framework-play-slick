package managers

import daos.row.{AuthorRowDAO, BookRowDAO, BookStoreRowDAO}
import daos.view.AuthorViewDAO
import dtos.{AuthorDTO, AuthorPaginateDTO, BookDTO, BookStoreDTO}
import manager.Manager
import manager.errors.ManagerError
import persistence.DbRunner
import persistence.errors.DbError
import play.api.db.slick.DatabaseConfigProvider
import slick.dbio.{DBIOAction, Effect, NoStream}
import slick.jdbc.JdbcProfile
import slick.lifted.{FlatShapeLevel, Rep, Shape}
import tables.Tables

import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import util.QueryParamModel

@Singleton
class AuthorManager @Inject()(
                               authorRowDAO: AuthorRowDAO,
                               bookRowDAO: BookRowDAO,
                               authorViewDAO: AuthorViewDAO,
                               bookStoreRowDAO: BookStoreRowDAO,
                             )(implicit ec: ExecutionContext, dbConfigProvider: DatabaseConfigProvider) extends Manager {

  // We want the JdbcProfile for this provider
  // it must be defined as protected because we return DBIO as result.
  protected val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._

  def fetch(authorIds: Seq[Int]): Future[Either[ManagerError, Seq[AuthorDTO]]] = {
    val action = for {
      authors <- authorRowDAO.selectAll(authorIds)
      booksMap <- bookRowDAO.fetchBooksByAuthorId(authorIds)
      bookStoresMap <- bookStoreRowDAO.fetchBookStoresByBookId(authorIds)
    } yield (authors, booksMap, bookStoresMap)

    DbRunner.run(action).map {
      case Left(error) => Left(toManagerError(error))
      case Right((authors, booksMap, bookStoresMap)) =>
        val authorsSorted = authors.sortBy(item => authorIds.indexOf(item.id))
        Right(authorsSorted.map { author =>
          // gather all related data for this author, with help of the grouped maps.
          val books = booksMap.getOrElse(author.id, Seq.empty)
          val bookApis = books.map(book => {
            val bookStores = bookStoresMap.getOrElse(book.id, Seq.empty)
            BookDTO.fromRow(book).copy(bookStores = Some(bookStores.map(BookStoreDTO.fromRow)))
          })
          AuthorDTO.fromRow(author).copy(books = Some(bookApis))
        })
    }
  }

  def paginate(qParam: QueryParamModel): Future[Either[ManagerError, AuthorPaginateDTO]] = {
    val action = for {
      ids <- authorViewDAO.paginate(qParam)
      count <- authorViewDAO.paginateCount(qParam)
    } yield (ids, count)

    DbRunner.run(action).flatMap {
      case Left(error) => Future.successful(Left(toManagerError(error)))
      case Right((ids, count)) => fetch(ids).map {
        case Left(error) => Left(error)
        case Right(authors) => Right(AuthorPaginateDTO(authors, count))
      }
    }
  }


  def paginateCount(qParam: QueryParamModel) =
    DbRunner.run(authorViewDAO.paginateCount(qParam))

  def delete(authorId: Int): Future[Either[DbError, Int]] =
    DbRunner.run(authorRowDAO.delete(authorId))

  def insert(author: AuthorDTO): Future[Either[ManagerError, AuthorDTO]] = {
    val action = for {
      id <- authorRowDAO.insertReturnId(author.toRow())
      author <- authorRowDAO.select(id)
    } yield (author)
    DbRunner.run(action).map {
      case Left(error) => Left(toManagerError(error))
      case Right(maybeResult) => maybeResult match {
        case Some(result) => Right(AuthorDTO.fromRow(result))
        case _ => Left(ManagerError("not found!"))
      }
    }
  }

}
