package library.controllers

import library.dtos.AuthorDTO
import library.dtos.BookToBookStoreDTO
import library.managers.AuthorManager
import library.managers.BookToBookStoreManager
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.AbstractController
import play.api.mvc.Action
import play.api.mvc.AnyContent
import play.api.mvc.ControllerComponents
import util.QueryParamFilterModel
import util.QueryParamModel
import util.QueryParamSorterModel

import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.ExecutionContext.Implicits.global

@Singleton
class BookToBookStoreController @Inject() (cc: ControllerComponents, bookToBookStoreManager: BookToBookStoreManager)
    extends AbstractController(cc)
    with I18nSupport {

  /**
   * Update a book-to-book-store relation
   *
   * @return book-to-book-store-data as json
   */
  def update =
    Action.async(parse.json[BookToBookStoreDTO]) { implicit request =>
      bookToBookStoreManager.update(request.body).map {
        case Left(error)   => BadRequest(error.message)
        case Right(author) => Ok(Json.toJson(author))
      }
    }

}
