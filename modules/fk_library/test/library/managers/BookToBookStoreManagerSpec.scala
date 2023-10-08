package library.managers

import core.persistence.DbRun
import core.tables.Tables._
import library.daos.BookToBookStoreDAO
import library.dtos.BookToBookStoreDTO
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.Inside.inside
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.matchers.should.Matchers.convertToStringShouldWrapper
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.db.slick.DatabaseConfigProvider
import slick.dbio.DBIO

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class BookToBookStoreManagerSpec extends PlaySpec with MockitoSugar {

  "BookToBookStoreManager#update" should {
    "should update the relationship, when a valid book-to-book-store relationship is given" in {
      implicit val executionContext = ExecutionContext.global
      implicit val dbcp             = mock[DatabaseConfigProvider]

      val bookToBookStoreDAO = mock[BookToBookStoreDAO]
      when(bookToBookStoreDAO.update(any[BookToBookStoreRow])).thenReturn(DBIO.successful(1))

      val dbRun = mock[DbRun]
      when(dbRun.run(DBIO.successful(1))).thenReturn(Future.successful(1))

      val bookToBookStoreManager = new BookToBookStoreManager(dbRun, bookToBookStoreDAO)

      val futureEither = bookToBookStoreManager.update(
        new BookToBookStoreDTO(
          name = "Test",
          book_id = 1,
          stock = Some(1),
        )
      )
      futureEither.map(either => {
        inside(either) {
          case Right(obj) =>
            obj.name shouldBe "Test"
            obj.book_id shouldBe 1
            obj.stock shouldBe Some(1)
        }
      })
    }
  }
}
