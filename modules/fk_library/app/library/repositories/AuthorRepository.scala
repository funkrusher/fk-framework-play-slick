package library.repositories

import akka.NotUsed
import akka.stream.scaladsl.Source
import akka.util.ByteString
import core.error.MappingError
import core.error.ObjectNotFoundError
import core.repository.Repository
import play.api.db.slick.DatabaseConfigProvider
import core.tables.Tables._
import play.api.http.HttpEntity

import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import core.util.QueryParamModel
import library.daos.AuthorDAO
import library.daos.BookDAO
import library.daos.BookStoreDAO
import library.dtos.AuthorDTO
import library.dtos.AuthorPaginateDTO
import library.dtos.BookDTO
import library.dtos.BookStoreDTO
import play.api.http.HeaderNames.CONTENT_DISPOSITION
import play.api.http.Status.OK
import play.api.mvc.ResponseHeader
import play.api.mvc.Result
import slick.ast.Library.Concat
import slick.basic.DatabasePublisher
import slick.dbio.Effect
import slick.jdbc.ResultSetConcurrency
import slick.jdbc.ResultSetType
import slick.sql.SqlAction

@Singleton
class AuthorRepository @Inject() (
    authorRowDAO: AuthorDAO,
    bookRowDAO: BookDAO,
    bookStoreRowDAO: BookStoreDAO,
    protected val dbConfigProvider: DatabaseConfigProvider,
)(implicit ec: ExecutionContext)
    extends Repository {

  import profile.api._

  def fetch(authorIds: Seq[Int]): DBIO[Either[MappingError, Seq[AuthorDTO]]] = {
    val action = for {
      authors       <- authorRowDAO.selectAllSorted(authorIds)
      booksMap      <- bookRowDAO.fetchBooksByAuthorId(authorIds)
      bookStoresMap <- bookStoreRowDAO.fetchBookStoresByBookId(authorIds)
    } yield (authors, booksMap, bookStoresMap)

    action.map {
      case (authors, booksMap, bookStoresMap) =>
        Right(authors.map { author =>
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

  def fetchOne(authorId: Int): DBIO[Either[MappingError, AuthorDTO]] = {
    fetch(Seq(authorId)).map(x =>
      x.flatMap(_.headOption match {
        case Some(value) => Right(value)
        case _           => Left(ObjectNotFoundError("author not found!"))
      })
    )
  }

  def getQueryParamQuery(qParam: QueryParamModel): Query[Rep[Int], Int, Seq] = {
    val authorFilters = Seq(
      Filter[Option[String]](Author, "first_name", "like"),
      Filter[Option[String]](Author, "first_name", "in"),
      Filter[String](Author, "last_name", "like"),
      Filter[String](Author, "last_name", "in"),
      Filter[Option[Int]](Author, "year_of_birth", "equal"),
      Filter[Option[Int]](Author, "year_of_birth", "null"),
    )
    val bookFilters = Seq(
      Filter[String](Book, "title", "like"),
      Filter[String](Book, "title", "in"),
      Filter[Int](Book, "published_in", "equal"),
      Filter[Int](Book, "published_in", "null"),
      Filter[Int](Book, "published_in", "in"),
    )
    val sorters = Seq(
      Sorter[String](Author, "last_name"),
      Sorter[String](Book, "title"),
    )
    (for {
      author          <- Author.applyFilters(qParam, authorFilters)
      book            <- Book.applyFilters(qParam, bookFilters) if book.author_id === author.id
      bookToBookStore <- BookToBookStore if bookToBookStore.book_id === book.id
      bookStore       <- BookStore if bookStore.name === bookToBookStore.name
    } yield (author, book, bookToBookStore, bookStore))
      .applySorter(qParam, sorters)
      .map(_._1.id)
  }

  def paginate(qParam: QueryParamModel): DBIO[Either[MappingError, AuthorPaginateDTO]] = {
    (for {
      ids           <- getQueryParamQuery(qParam).paginate(qParam).resultWithFixOrderedAndGrouped
      count         <- getQueryParamQuery(qParam).distinct.length.result
      eitherAuthors <- fetch(ids)
    } yield (eitherAuthors, count)).map {
      case (eitherAuthors, count) =>
        eitherAuthors.map(authors => AuthorPaginateDTO(authors, count))
    }
  }

  def getIdPublisher(qParam: QueryParamModel): DatabasePublisher[Int] = {
    // we want to fetch the data from the database in chunks of items per network-response from db to us.
    db.stream(
      getQueryParamQuery(qParam).resultWithFixOrderedAndGrouped
        .withStatementParameters(
          rsType = ResultSetType.ForwardOnly,
          rsConcurrency = ResultSetConcurrency.ReadOnly,
          fetchSize = 50,
        )
        .transactionally
    )
  }

}
