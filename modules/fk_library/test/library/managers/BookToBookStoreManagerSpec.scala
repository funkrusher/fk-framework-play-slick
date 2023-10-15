package library.managers

import foundation.tables.Tables._
import foundation.persistence.DbRunner
import library.daos.BookToBookStoreDAO
import library.dtos.BookToBookStoreDTO
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatest.Inside.inside
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper
import org.scalatest.matchers.should.Matchers.convertToStringShouldWrapper
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import play.api.db.slick.DatabaseConfigProvider
import slick.dbio.DBIO

import scala.concurrent.Await
import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

class BookToBookStoreManagerSpec extends PlaySpec with MockitoSugar {

  "BookToBookStoreManager#update" should {
    "should update the relationship, when a valid book-to-book-store relationship is given" in {
      implicit val executionContext = ExecutionContext.global
      implicit val dbcp             = mock[DatabaseConfigProvider]

      val bookToBookStoreDAO = mock[BookToBookStoreDAO]
      when(bookToBookStoreDAO.update(any[BookToBookStoreRow])).thenReturn(DBIO.successful(1))

      val dbRun = mock[DbRunner]
      when(dbRun.run(DBIO.successful(1))).thenReturn(Future.successful(1))

      val bookToBookStoreManager = new BookToBookStoreManager(dbRun, bookToBookStoreDAO)

      val futureEither = bookToBookStoreManager.update(
        new BookToBookStoreDTO(
          name = "Test",
          book_id = 1,
          stock = Some(1),
        )
      )

      val either = Await.result(futureEither, 2000.milliseconds)
      inside(either) {
        case Right(obj) =>
          obj.name shouldBe "Test"
          obj.book_id shouldBe 1
          obj.stock shouldBe Some(1)
      }
    }
  }
}
