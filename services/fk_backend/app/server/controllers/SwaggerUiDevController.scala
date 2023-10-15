package server.controllers

import javax.inject.Inject
import play.api.Environment
import play.api.Mode
import play.api.mvc.AbstractController
import play.api.mvc.ControllerComponents
import scala.concurrent.ExecutionContext

class SwaggerUiDevController @Inject() (environment: Environment)(
    cc: ControllerComponents,
    implicit val ec: ExecutionContext,
) extends AbstractController(cc) {
  def serveAsset(file: String) =
    Action {
      if (environment.mode == Mode.Dev) {
        // Serve swagger-ui only in Dev mode
        val assetPath = s"conf/swagger-ui/$file"
        Ok.sendFile(environment.getFile(assetPath))
      } else {
        // Return a 404 or some other response in production mode
        NotFound
      }
    }
}
