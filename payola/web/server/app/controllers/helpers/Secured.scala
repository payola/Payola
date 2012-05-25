package controllers.helpers

import play.api.mvc._
import cz.payola.domain.entities.User
import controllers._
import play.api.libs.iteratee._

/**
  * Provide security features
  */
trait Secured
{ self: PayolaController =>

    def authenticated(f: User => Result): Action[(Action[AnyContent], AnyContent)] = {
        maybeAuthenticated { user: Option[User] =>
            user.map(f(_)).getOrElse(Results.Redirect(routes.Application.login))
        }
    }

    def maybeAuthenticated(f: Option[User] => Result): Action[(Action[AnyContent], AnyContent)] = {
        def username(requestHeader: RequestHeader): Option[String] = {
            requestHeader.session.get("email")
        }
        def onUnauthorized(requestHandler: RequestHeader): Result = {
            f(None)
        }
        def onAuthorized(userName: String): Action[AnyContent] = {
            Action(request => f(getUser(userName)))
        }

        Security.Authenticated[AnyContent](username _, onUnauthorized _)(onAuthorized _)
    }
}
