package library.controllers

import library.dtos.AuthorDTO
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

@Singleton
class AuthorController @Inject() (cc: ControllerComponents, authorManager: AuthorManager)
    extends AbstractController(cc)
    with I18nSupport {

  def paginate: Action[AnyContent] =
    Action.async { implicit request =>
      val qParam = QueryParamModel(
        drop = Some(0),
        take = Some(10),
        sorter = Some(
          QueryParamSorterModel(
            tableName = "author",
            sortOrder = "asc",
            sortName = "last_name",
          )
        ),
        filters = Some(
          Seq(
            QueryParamFilterModel(
              tableName = "author",
              filterName = "first_name",
              filterValue = "George,Paulo",
              filterComparator = "in",
            ),
            QueryParamFilterModel(
              tableName = "author",
              filterName = "last_name",
              filterValue = "Orwell,Coelho",
              filterComparator = "in",
            ),
            QueryParamFilterModel(
              tableName = "book",
              filterName = "published_in",
              filterValue = "1948,1945,1988",
              filterComparator = "in",
            ),
          )
        ),
      )
      authorManager.paginate(qParam).map {
        case Left(error)   => BadRequest(error.message)
        case Right(result) => Ok(Json.toJson(result))
      }
    }

  /**
   * add a new author to the list
   *
   * @return author-data as json
   */
  def addAuthor =
    Action.async(parse.json[AuthorDTO]) { implicit request =>
      authorManager.insert(request.body).map {
        case Left(error)   => BadRequest(error.message)
        case Right(author) => Ok(Json.toJson(author))
      }
    }

  /**
   * delete a author from the list
   *
   * @return success-status
   */
  def deleteAuthor(authorId: Int) =
    Action.async { implicit request =>
      authorManager.delete(authorId).map {
        case Left(error) => BadRequest(error.message)
        case Right(_)    => Ok("deleted")
      }
    }

}
