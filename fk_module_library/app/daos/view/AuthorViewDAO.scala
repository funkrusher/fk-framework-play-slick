package daos.view

import daos.ViewDAO
import play.api.db.slick.DatabaseConfigProvider
import tables.Tables._

import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.ExecutionContext
import util.QueryParamModel

@Singleton
class AuthorViewDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit ec: ExecutionContext)
  extends ViewDAO {

  import profile.api._

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

  def getFilterQuery(qParam: QueryParamModel) = for {
    author <- Author.applyFilters(qParam, authorFilters)
    book <- Book.applyFilters(qParam, bookFilters) if book.author_id === author.id
    bookToBookStore <- BookToBookStore if bookToBookStore.book_id === book.id
    bookStore <- BookStore if bookStore.name === bookToBookStore.name
  } yield (author, book, bookToBookStore, bookStore)

  def paginate(qParam: QueryParamModel): DBIO[Seq[Int]] = getFilterQuery(qParam)
    .applySorter(qParam, sorters)
    .map(_._1.id)
    .paginate(qParam)

  def paginateCount(qParam: QueryParamModel): DBIO[Int] = getFilterQuery(qParam)
    .map(_._1.id)
    .distinct.length.result
}
