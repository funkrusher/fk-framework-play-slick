package server.controllers

import io.swagger.annotations.Api
import io.swagger.annotations.ApiImplicitParams
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiResponse
import io.swagger.annotations.ApiResponses
import play.api.i18n.I18nSupport
import play.api.mvc.AbstractController
import play.api.mvc.ControllerComponents

import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.Future

@Api("Healthcheck")
@Singleton
class HealthcheckController @Inject() (cc: ControllerComponents) extends AbstractController(cc) with I18nSupport {

  /**
   * Healthcheck endpoint
   *
   * @return success of healthcheck
   */
  @ApiOperation(
    value = "Healthcheck"
  )
  @ApiResponses(
    Array(
      new ApiResponse(code = 200, message = "The Healthcheck was executed successfully")
    )
  )
  def healtcheck() =
    Action.async { implicit request =>
      Future.successful(Ok("Rockin' in the Free World!"))
    }
}
