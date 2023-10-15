package library.controllers

import foundation.dtos.QueryParamDTO
import library.dtos.AuthorDTO
import library.dtos.AuthorPaginateDTO
import library.managers.AuthorManager
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.AbstractController
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents

import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class AuthorController @Inject() (cc: ControllerComponents, authorManager: AuthorManager)
    extends AbstractController(cc)
    with I18nSupport {

  /**
   * Paginate Authors
   *
   * @return pagination result
   */
  def paginate =
    Action.async(parse.json[QueryParamDTO]) { implicit request =>
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
  def deleteAuthor(authorId: Int) =
    Action.async { implicit request =>
      authorManager.delete(authorId).map {
        case Left(error) => InternalServerError(error.message)
        case Right(_)    => Ok("deleted")
      }
    }

}
