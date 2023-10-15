package scheduler.controllers

import play.api.i18n.I18nSupport
import play.api.mvc.AbstractController
import play.api.mvc.ControllerComponents

import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.Future

@Singleton
class HealthcheckController @Inject() (cc: ControllerComponents) extends AbstractController(cc) with I18nSupport {

  /**
   * Healthcheck endpoint
   *
   * @return success of healthcheck
   */
  def healtcheck() =
    Action.async { implicit request =>
      Future.successful(Ok("Schedulin' in the Free World!"))
    }
}
