package core.controllers

import auth.controllers.routes
import core.controllers.helpers.{UserAction, UserRequest}
import core.models.{SessionDAO, User, UserDAO}

import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject._
import play.api.mvc.AnyContent
import play.api.mvc.BaseController
import play.api.mvc.ControllerComponents
import play.api.mvc.EssentialAction
import play.api.mvc.Request
import play.api.mvc.RequestHeader
import play.api.mvc.Result
import play.api.mvc.Security

@Singleton
class HomeController @Inject() (val userAction: UserAction, val controllerComponents: ControllerComponents)
    extends BaseController {

  def index() =
    Action { implicit request: Request[AnyContent] =>
      Ok("ok")
    }

  def public() =
    Action { implicit request: Request[AnyContent] =>
      Ok("ok")
    }

  def register(username: String, password: String) =
    Action { implicit request: Request[AnyContent] =>
      UserDAO
        .addUser(username, password)
        .map(_ => Ok("ok"))
        .getOrElse(Conflict(views.html.defaultpages.unauthorized()))
    }

  def login(username: String, pass: String) =
    Action { implicit request: Request[AnyContent] =>
      if (isValidLogin(username, pass)) {
        val token = SessionDAO.generateToken(username)

        Redirect(routes.HomeController.index)
          .withSession(request.session + ("sessionToken" -> token))
      } else {
        // we should redirect to login page
        Unauthorized(views.html.defaultpages.unauthorized()).withNewSession
      }
    }

  def logout() =
    Action { implicit request: Request[AnyContent] =>
      Redirect(routes.HomeController.index).withNewSession
    }

  def priv() =
    Action { implicit request: Request[AnyContent] =>
      withUser(user => Ok("ok"))
    }

  def privPlay() =
    withPlayUser { user =>
      Ok("ok")
    }

  def privAction() =
    userAction { user: UserRequest[AnyContent] =>
      Ok("ok")
    }

  private def isValidLogin(username: String, password: String): Boolean = {
    UserDAO.getUser(username).exists(_.password == password)
  }

  private def withPlayUser[T](block: User => Result): EssentialAction = {
    Security.WithAuthentication(extractUser)(user => Action(block(user)))
  }

  private def withUser[T](block: User => Result)(implicit request: Request[AnyContent]): Result = {
    val user = extractUser(request)

    user
      .map(block)
      .getOrElse(
        Unauthorized(views.html.defaultpages.unauthorized())
      ) // 401, but 404 could be better from a security point of view
  }

  private def extractUser(req: RequestHeader): Option[User] = {

    val sessionTokenOpt = req.session.get("sessionToken")

    sessionTokenOpt
      .flatMap(token => SessionDAO.getSession(token))
      .filter(_.expiration.isAfter(LocalDateTime.now(ZoneOffset.UTC)))
      .map(_.username)
      .flatMap(UserDAO.getUser)
  }

}
