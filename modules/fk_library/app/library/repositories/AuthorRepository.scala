package library.repositories

import core.error.mapping.MappingError
import core.repository.Repository
import play.api.db.slick.DatabaseConfigProvider
import core.tables.Tables._

import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.ExecutionContext
import core.util.QueryParamModel
import library.daos.AuthorDAO
import library.daos.BookDAO
import library.daos.BookStoreDAO
import library.dtos.AuthorDTO
import library.dtos.AuthorPaginateDTO
import library.dtos.BookDTO
import library.dtos.BookStoreDTO

@Singleton
class AuthorRepository @Inject() (
    authorRowDAO: AuthorDAO,
    bookRowDAO: BookDAO,
    bookStoreRowDAO: BookStoreDAO,
    protected val dbConfigProvider: DatabaseConfigProvider,
)(implicit ec: ExecutionContext)
    extends Repository {

  import profile.api._

  def paginate(qParam: QueryParamModel): DBIO[Either[MappingError, AuthorPaginateDTO]] = {
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
    def getFilterQuery(qParam: QueryParamModel) =
      for {
        author          <- Author.applyFilters(qParam, authorFilters)
        book            <- Book.applyFilters(qParam, bookFilters) if book.author_id === author.id
        bookToBookStore <- BookToBookStore if bookToBookStore.book_id === book.id
        bookStore       <- BookStore if bookStore.name === bookToBookStore.name
      } yield (author, book, bookToBookStore, bookStore)

    (for {
      ids           <- getFilterQuery(qParam).applySorter(qParam, sorters).map(_._1.id).paginate(qParam)
      count         <- getFilterQuery(qParam).map(_._1.id).distinct.length.result
      eitherAuthors <- fetch(ids)
    } yield (eitherAuthors, count)).map {
      case (eitherAuthors, count) =>
        eitherAuthors.map(authors => AuthorPaginateDTO(authors, count))
    }
  }

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
}
