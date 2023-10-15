package foundation.persistence

import foundation.error.DatabaseError
import play.api.Logger
import play.api.db.slick.DatabaseConfigProvider
import slick.dbio.DBIOAction
import slick.dbio.Effect
import slick.dbio.NoStream
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext
import scala.concurrent.Future
import scala.util.Failure
import scala.util.Success
import scala.util.Try
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DbRun @Inject() ()(implicit ec: ExecutionContext, dbConfigProvider: DatabaseConfigProvider) {

  // We want the JdbcProfile for this provider
  // it must be defined as protected because we return DBIO as result.
  protected val dbConfig = dbConfigProvider.get[JdbcProfile]

  val logger: Logger = Logger(this.getClass())

  /**
   * Use this method if you need to explicitly detect if the execution on the database had an error,
   * and be able to handle this at the place where it happened.
   *
   * This is often only needed for places, where multiple systems are involved, like for example:
   * - system a) a user-pool in an external provider (like AWS Cognito)
   * - system b) an internal database with a user-table.
   *
   * When the execution in the database has an error we need to manually rollback our transaction,
   * that spans with external systems.
   *
   * Only for those cases it makes sense to use this method, to be able to manually react to database-errors.
   *
   * @param action           action
   * @param dbConfigProvider dbConfigProvider
   * @param ec               ec
   * @tparam R R
   * @tparam S S
   * @tparam E E
   * @return Either (Success/Failure)
   */
  def tryRun[R, S <: NoStream, E <: Effect](
      action: DBIOAction[R, S, E]
  )(implicit dbConfigProvider: DatabaseConfigProvider, ec: ExecutionContext): Future[Either[DatabaseError, R]] = {
    val dbConfig = dbConfigProvider.get[JdbcProfile]
    import dbConfig._
    Try(db.run(action)) match {
      case Success(result) =>
        result.map(Right(_)).recover {
          case ex: Throwable => Left(DatabaseError(ex.getMessage))
        }
      case Failure(ex) => Future.successful(Left(DatabaseError(ex.getMessage)))
    }
  }

  /**
   * Execute the database-transaction.
   *
   * If an error occurs during execution this error will let the Future fail silently.
   * Therefore we also Log this error here immediately, so it will not get lost.
   *
   * @param action           action
   * @param dbConfigProvider dbConfigProvider
   * @param ec               ec
   * @tparam R R
   * @tparam S S
   * @tparam E E
   * @return Future
   */
  def run[R, S <: NoStream, E <: Effect](
      action: DBIOAction[R, S, E]
  )(implicit dbConfigProvider: DatabaseConfigProvider, ec: ExecutionContext): Future[R] = {
    val dbConfig = dbConfigProvider.get[JdbcProfile]
    import dbConfig._

    db.run(action)
  }
}
