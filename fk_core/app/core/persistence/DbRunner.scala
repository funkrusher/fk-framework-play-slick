package core.persistence

import core.persistence.errors.DbError
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

object DbRunner {
  def run[R, S <: NoStream, E <: Effect](
      action: DBIOAction[R, S, E]
  )(implicit dbConfigProvider: DatabaseConfigProvider, ec: ExecutionContext): Future[Either[DbError, R]] = {
    val dbConfig = dbConfigProvider.get[JdbcProfile]
    import dbConfig._
    Try(db.run(action)) match {
      case Success(result) =>
        result.map(Right(_)).recover {
          case ex: Throwable => Left(DbError(ex.getMessage))
        }
      case Failure(ex) => Future.successful(Left(DbError(ex.getMessage)))
    }
  }

}
