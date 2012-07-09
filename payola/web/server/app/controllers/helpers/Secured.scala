package controllers.helpers

import play.api.mvc._
import cz.payola.domain.entities.User
import controllers._

/**
  * Provide security features
  */
trait Secured
{
    self: PayolaController =>

    def authenticated(f: User => Result): Action[(Action[AnyContent], AnyContent)] = {
        authenticatedWithRequest((_, user) => f(user))
    }

    def authenticatedWithRequest(f: (Request[_], User) => Result) = {
        maybeAuthenticatedWithRequest { r: Option[(Request[AnyContent], User)] =>
            val result = r.map { case (request: Request[_], user: User) => f(request, user) }
            result.getOrElse(Results.Redirect(routes.Application.login))
        }
    }

    def maybeAuthenticated(f: Option[User] => Result): Action[(Action[AnyContent], AnyContent)] = {
        maybeAuthenticatedWithRequest((r: Option[(Request[AnyContent], User)]) => f(r.map(_._2)))
    }

    def maybeAuthenticatedWithRequest(f: (Option[(Request[AnyContent], User)]) => Result):
        Action[(Action[AnyContent], AnyContent)] = {

        def username(requestHeader: RequestHeader): Option[String] = {
            requestHeader.session.get("email")
        }
        def onUnauthorized(requestHeader: RequestHeader): Result = {
            f(None)
        }
        def onAuthorized(userName: String): Action[AnyContent] = {
            Action(request => f(getUser(userName).map((request, _))))
        }

        Security.Authenticated[AnyContent](username _, onUnauthorized _)(onAuthorized _)
    }
}
