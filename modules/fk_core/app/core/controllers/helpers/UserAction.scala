package core.controllers.helpers

import core.models.{SessionDAO, User, UserDAO}

import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.inject.Inject
import play.api.mvc._

import scala.concurrent.ExecutionContext
import scala.concurrent.Future

class UserRequest[A](val user: Option[User], request: Request[A]) extends WrappedRequest[A](request)

class UserAction @Inject() (val parser: BodyParsers.Default)(implicit val executionContext: ExecutionContext)
    extends ActionBuilder[UserRequest, AnyContent]
    with ActionTransformer[Request, UserRequest] {

  def transform[A](request: Request[A]) =
    Future.successful {

      val sessionTokenOpt = request.session.get("sessionToken")

      val user = sessionTokenOpt
        .flatMap(token => SessionDAO.getSession(token))
        .filter(_.expiration.isAfter(LocalDateTime.now(ZoneOffset.UTC)))
        .map(_.username)
        .flatMap(UserDAO.getUser)

      new UserRequest(user, request)
    }
}
