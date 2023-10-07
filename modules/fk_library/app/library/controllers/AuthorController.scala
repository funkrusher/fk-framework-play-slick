package library.controllers

import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import io.swagger.annotations.Example
import io.swagger.annotations.ExampleProperty
import library.dtos.AuthorDTO
import library.dtos.AuthorPaginateDTO
import library.managers.AuthorManager
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.AbstractController
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import core.util.QueryParamFilterModel
import core.util.QueryParamModel
import core.util.QueryParamSorterModel

import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Api("Author")
@Singleton
class AuthorController @Inject() (cc: ControllerComponents, authorManager: AuthorManager)
    extends AbstractController(cc)
    with I18nSupport {

  /**
   * Paginate Authors
   *
   * @return pagination result
   */
  @ApiOperation(
    value = "Paginate Authors",
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
      new ApiResponse(code = 200, message = "The Pagination was executed", response = classOf[AuthorPaginateDTO]),
      new ApiResponse(code = 500, message = "Internal Server Error"),
    )
  )
  def paginate =
    Action.async(parse.json[QueryParamModel]) { implicit request =>
      authorManager.paginate(request.body).map {
        case Left(error)   => InternalServerError(error.message)
        case Right(result) => Ok(Json.toJson(result))
      }
    }

  /**
   * add a new author to the list
   *
   * @return author-data as json
   */
  @ApiOperation(
    value = "Create a new Author",
    notes = "TODO",
  )
  @ApiImplicitParams(
    Array(
      new ApiImplicitParam(
        name = "body",
        value = "JSON Body",
        required = true,
        paramType = "body",
        dataTypeClass = classOf[AuthorDTO],
      )
    )
  )
  @ApiResponses(
    Array(
      new ApiResponse(code = 200, message = "The Author was created", response = classOf[AuthorDTO]),
      new ApiResponse(code = 500, message = "Internal Server Error"),
    )
  )
  def addAuthor =
    Action.async(parse.json[AuthorDTO]) { implicit request =>
      authorManager.insert(request.body).map {
        case Left(error)   => InternalServerError(error.message)
        case Right(author) => Ok(Json.toJson(author))
      }
    }

  /**
   * delete a author from the list
   *
   * @return success-status
   */
  @ApiOperation(
    value = "Delete an Author",
    notes = "TODO",
  )
  @ApiResponses(
    Array(
      new ApiResponse(code = 200, message = "The Author was deleted"),
      new ApiResponse(code = 500, message = "Internal Server Error"),
    )
  )
  def deleteAuthor(authorId: Int) =
    Action.async { implicit request =>
      authorManager.delete(authorId).map {
        case Left(error) => InternalServerError(error.message)
        case Right(_)    => Ok("deleted")
      }
    }

}
