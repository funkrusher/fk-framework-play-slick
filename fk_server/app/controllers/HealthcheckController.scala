package controllers

import dtos.AuthorDTO
import managers.AuthorManager
import play.api.i18n.I18nSupport
import play.api.libs.json.Json
import play.api.mvc.{AbstractController, Action, AnyContent, ControllerComponents}
import util.{QueryParamFilterModel, QueryParamModel, QueryParamSorterModel}

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class HealthcheckController @Inject()(cc: ControllerComponents)
  extends AbstractController(cc)
    with I18nSupport {

  /**
   * Healthcheck endpoint
   *
   * @return success of healthcheck
   */
  def healtcheck() = Action.async { implicit request =>
    Future.successful(Ok("Rockin' in the Free World!"))
  }
}
