package library.managers

import foundation.error.MappingError
import foundation.manager.Manager
import foundation.persistence.DbRunner
import foundation.util.QueryParamModel
import library.daos.AuthorDAO
import library.daos.BookDAO
import library.dtos.AuthorDTO
import library.dtos.AuthorPaginateDTO
import library.repositories.AuthorRepository
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

@Singleton
class AuthorExampleManager @Inject() (
    authorRepository: AuthorRepository,
    authorDAO: AuthorDAO,
)(implicit ec: ExecutionContext, dbConfigProvider: DatabaseConfigProvider)
    extends Manager {

  val logger: Logger = Logger(this.getClass())

  // We want the JdbcProfile for this provider
  // it must be defined as protected because we return DBIO as result.
  protected val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  def testTransactionWithErrorAndRollback(): Unit = {
    // see also: https://github.com/slick/slick/issues/1197
    // see also: https://stackoverflow.com/questions/38221021/transactional-method-in-scala-play-with-slick
    // -similar-to-spring-transactional
    val action = (for {
      author  <- authorDAO.select(1)
      author2 <- authorDAO.select(2)
      save1   <- authorDAO.update(author.head.copy(id = Some(1000), first_name = Some("Gustave")))
      save2   <- authorDAO.update(author.head.copy(id = Some(1001), first_name = Some("Lydia")))
    } yield (author, author2, save1, save2)).flatMap {
      case (author, author2, save1, save2) =>
        // lets force a rollback after all statements have been resolved
        // but while the transaction is still open.
        DBIO.failed(new Exception("force a rollback of the transaction with this ex-throw!"))
    }.transactionally;

    DbRunner.tryRun(action).map {
      case Left(error) => logger.error("got a db-error, as expected: " + error.message)
      case Right(_)    => logger.error("this should not happen!")
    }
  }
}
