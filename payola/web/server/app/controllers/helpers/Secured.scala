package controllers.helpers

import play.api.mvc._
import controllers.routes

/**
  * Provide security features
  */
trait Secured {

    /**
      * Retrieve the connected user email.
      */
    private def username(request: RequestHeader) = request.session.get("email")

    /**
      * Redirect to login if the user in not authorized.
      */
    private def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Application.login)

    // --

    /**
      * Action for authenticated users.
      */
    def IsAuthenticated(f: => String => Request[AnyContent] => Result) = Security.Authenticated(username, onUnauthorized) { user =>
        Action(request => f(user)(request))
    }

}
