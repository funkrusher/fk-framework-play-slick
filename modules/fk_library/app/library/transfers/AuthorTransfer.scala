package library.transfers

import akka.NotUsed
import akka.stream.scaladsl.Source
import akka.util.ByteString
import foundation.dtos.QueryParamDTO
import foundation.manager.Manager
import foundation.persistence.DbRunner
import library.repositories.AuthorRepository
import library.transfers.csv.AuthorCsvExport
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

@Singleton
class AuthorTransfer @Inject() (
    authorRepository: AuthorRepository
)(implicit ec: ExecutionContext, dbConfigProvider: DatabaseConfigProvider)
    extends Manager {

  // We want the JdbcProfile for this provider
  // it must be defined as protected because we return DBIO as result.
  protected val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import profile.api._

  private def prepareTransfer(qParam: QueryParamDTO) = {
    Source
      .fromPublisher(authorRepository.getIdPublisher(qParam))
      .grouped(50) // group our separate items into chunks of items
      .mapAsync(2)(chunkAuthorIds => {
        // spawn multiple futures, that can fetch data from different tables at the same time
        // for this chunk of items.
        DbRunner.run(authorRepository.fetch(chunkAuthorIds)).flatMap {
          case Left(error)    => Future.failed(error)
          case Right(authors) => Future.successful(authors)
        }
      })
      .mapConcat(s => s.toList) // flatten the chunk of items to separate items again.
  }

  def exportCsv(qParam: QueryParamDTO): Source[ByteString, NotUsed] = {
    AuthorCsvExport.from(prepareTransfer(qParam))
  }

}
