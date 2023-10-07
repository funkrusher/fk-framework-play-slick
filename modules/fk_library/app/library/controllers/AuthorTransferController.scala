package library.controllers

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

@Api("Author")
@Singleton
class AuthorTransferController @Inject() (cc: ControllerComponents, authorTransfer: AuthorTransfer)
    extends AbstractController(cc)
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

}
