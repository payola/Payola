package controllers.helpers

import play.api.mvc._
import cz.payola.domain.entities.User
import controllers._
import scala.Left
import play.api.libs.iteratee._
import scala.Left
import scala.Left

/**
  * Provide security features
  */
trait Secured
{
    self: PayolaController =>

    def authenticated(action: User => Result) = {
        authenticatedWithRequest((user, request) => action(user))
    }

    def authenticatedWithRequest(action: (User, Request[AnyContent]) => Result) = {
        maybeAuthenticatedWithRequest { (user, request) =>
            val result = user.map(u => action(u, request))
            result.getOrElse(Results.Redirect(routes.Application.login))
        }
    }

    def maybeAuthenticated(action: Option[User] => Result) = {
        maybeAuthenticatedWithRequest((user, request) => action(user))
    }

    def maybeAuthenticatedWithRequest(action: (Option[User], Request[AnyContent]) => Result): Action[AnyContent] = {
        Action { request =>
            val user = request.session.get("email").flatMap(e => getUser(e))
            action(user, request)
        }
    }
}
