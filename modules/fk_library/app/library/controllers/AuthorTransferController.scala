package library.controllers

import com.hhandoko.play.pdf.PdfGenerator
import core.util.QueryParamModel
import library.dtos.AuthorDTO
import library.dtos.AuthorPaginateDTO
import library.managers.AuthorManager
import library.transfers.AuthorTransfer
import play.api.http.HttpEntity
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.AbstractController
import play.api.mvc.ControllerComponents
import play.api.mvc.ResponseHeader
import play.api.mvc.Result

import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class AuthorTransferController @Inject() (
    cc: ControllerComponents,
    authorTransfer: AuthorTransfer,
    pdfGen: PdfGenerator,
) extends AbstractController(cc)
    with I18nSupport {

  /**
   * Stream Authors as csv-export
   *
   * @return streaming result as csv-export
   */
  def exportCsv =
    Action.async(parse.json[QueryParamModel]) { implicit request =>
      Future.successful(
        Result(
          header = ResponseHeader(OK, Map(CONTENT_DISPOSITION -> s"attachment; filename=authors.csv")),
          body = HttpEntity.Streamed(authorTransfer.exportCsv(request.body), None, None),
        )
      )
    }

  val BASE_URL = "http://localhost:9000"

  /**
   * Create Pdf-Export
   *
   * @return create a Pdf-Export of a selection of data.
   */
  def exportPdf =
    Action.async { implicit request =>
      Future.successful(
        pdfGen.ok(library.views.html.example(), BASE_URL)
      )
    }

}
