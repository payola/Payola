package controllers

import s2js.runtime.shared.DependencyProvider
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import views._
import helpers.Secured

object Application extends PayolaController with Secured
{

    def index = maybeAuthenticated { user =>
        Ok(views.html.application.index(user))
    }

    def rpcTest = Action {
        Ok(views.html.test())
    }

    def javaScriptPackage(symbol: String) = Action {
        val javaScript = DependencyProvider.get(List("bootstrap", symbol), Nil).javaScript
        Ok(javaScript).as("text/javascript")
    }

    def dashboard = maybeAuthenticated { user =>
        Ok(views.html.application.dashboard(user, df.topAnalyses, df.getPublicDataSources(10), df.getGroupsByOwner(user)))
    }

    // -- Authentication
    val loginForm = Form(
        tuple(
            "email" -> text,
            "password" -> text
        ) verifying("Invalid email or password", result =>
            result match {
                case (email, password) => df.getUserByCredentials(email, password).isDefined
            }
        )
    )

    /**
      * Login page.
      */
    def login = Action {implicit request =>
        Ok(html.login(loginForm))
    }

    /**
      * Handle login form submission.
      */
    def authenticate = Action {implicit request =>
        loginForm.bindFromRequest.fold(
            formWithErrors => BadRequest(html.login(formWithErrors)),
            user => Redirect(routes.Application.dashboard).withSession("email" -> user._1)
        )
    }

    /**
      * Logout and clean the session.
      */
    def logout = Action {
        Redirect(routes.Application.login).withNewSession.flashing(
            "success" -> "You've been logged out"
        )
    }

    val signupForm = Form(
        tuple(
            "email" -> text,
            "password" -> text
        ) verifying("Username already taken.", result =>
            result match {
                case (email, password) => !df.getUserByUsername(email).isDefined
            }
        )
    )

    def signup = Action {implicit request =>
        Ok(html.application.signup(signupForm))
    }

    /**
      * Handle login form submission.
      */
    def register = Action {implicit request =>
        signupForm.bindFromRequest.fold(
            formWithErrors => BadRequest(html.application.signup(formWithErrors)),
            user =>
            {   df.register(user._1, user._2)
                Redirect(routes.Application.dashboard).withSession("email" -> user._1) }
        )
    }
}
