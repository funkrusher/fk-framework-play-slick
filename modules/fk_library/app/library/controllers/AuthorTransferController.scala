package library.controllers

import com.hhandoko.play.pdf.PdfGenerator
import core.util.QueryParamModel
import io.swagger.annotations._
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

@Api("AuthorTransfer")
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
  @ApiOperation(
    value = "Stream Authors as CSV-Export",
    notes = "TODO",
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "body",
        value = """JSON Body
                {
                  "drop": 0,
                  "take": 10,
                  "sorter": {
                    "tableName": "author",
                    "sortOrder": "asc",
                    "sortName": "last_name"
                  },
                  "filters": [
                    {
                      "tableName": "author",
                      "filterName": "last_name",
                      "filterValue": "Orwell,Coelho",
                      "filterComparator": "in"
                    },
                    {
                      "tableName": "book",
                      "filterName": "published_in",
                      "filterValue": "1948,1945,1988",
                      "filterComparator": "in"
                    }
                  ]
                }""",
        required = true,
        paramType = "body",
        dataTypeClass = classOf[QueryParamModel],
      )
    )
  )
  @ApiResponses(
    Array(
      new ApiResponse(code = 200, message = "The Csv-Export was executed"),
      new ApiResponse(code = 500, message = "Internal Server Error"),
    )
  )
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
  @ApiOperation(
    value = "Create Pdf-Export",
    notes = "TODO",
  )
  @ApiResponses(
    Array(
      new ApiResponse(code = 200, message = "The Pdf-Export was executed"),
      new ApiResponse(code = 500, message = "Internal Server Error"),
    )
  )
  def exportPdf =
    Action.async { implicit request =>
      Future.successful(
        Ok("ok")
        // pdfGen.ok(library.views.html.example(), BASE_URL)
      )
    }

}
