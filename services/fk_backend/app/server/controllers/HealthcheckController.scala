package server.controllers

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
      Future.successful(Ok("Rockin' in the Free World!"))
    }

//  implicit val cl = getClass.getClassLoader
//
//  lazy val generator = SwaggerSpecGenerator(true, false, false, "library.dtos")
//
//  lazy val swagger = Action { request =>
//    generator
//      .generate("library.Routes")
//      .fold(e => InternalServerError("Couldn't generate swagger."), s => Ok(s))
//  }
//
//  def specs = swagger

}
