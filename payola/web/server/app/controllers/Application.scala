package controllers

import s2js.runtime.shared.DependencyProvider
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import views._
import helpers.Secured
import cz.payola.domain.entities.User
import cz.payola.web.shared.Payola
import cz.payola.web.shared.managers.PasswordManager
import cz.payola.domain.IDGenerator
import cz.payola.common.ValidationException

object Application extends PayolaController with Secured
{

    def index = maybeAuthenticated { user =>
        Ok(views.html.application.index(user))
    }

    def javaScriptPackage(symbol: String) = Action {
        val javaScript = try {
            DependencyProvider.get(List("s2js.bootstrap", symbol), Nil).javaScript
        } catch {
            case e => {
                e.printStackTrace()
                ""
            }
        }
        Ok(javaScript).as("text/javascript")
    }

    def dashboard = maybeAuthenticated { user =>
        Ok(views.html.application.dashboard(user, Payola.model.analysisModel.getAccessibleToUser(user, forListing = true),
            Payola.model.dataSourceModel.getAccessibleToUser(user)))
    }

    def resetPassword = maybeAuthenticated{ user =>
        Ok(views.html.application.reset_password(user)(new Flash()))
    }

    def reallyResetPassword = Action { implicit request =>
        val reqOpt = request.body.asFormUrlEncoded
        if (reqOpt.isEmpty){
            Ok(views.html.application.reset_password(None)(new Flash(Map("error" -> "Couldn't reset password for an unexpected error."))))
        }else{
            val req = reqOpt.get
            val email = req("email")(0)
            val password = req("password")(0)

            val userOpt = Payola.model.userModel.getByEmail(email)
            if (userOpt.isEmpty){
                Ok(views.html.application.reset_password(None)(new Flash(Map("error" -> "The email you've entered isn't associated with any user in our database."))))
            }else{
                PasswordManager.sendRecoveryEmailToUser(IDGenerator.newId,userOpt.get, password)
                Ok(views.html.application.reset_password(None)(new Flash(Map("success" -> "A confirmation link has been emailed to you."))))
            }
        }
    }

    def confirmReset(uuid: String) = maybeAuthenticated { user =>
        if (PasswordManager.confirmPasswordReset(uuid)){
            Ok(views.html.application.reset_password(user)(new Flash(Map("success" -> "Your password has been successfully reset."))))
        }else{
            Ok(views.html.application.reset_password(user)(new Flash(Map("error" -> "Your reset token has expired."))))
        }
    }

    // -- Authentication
    val loginForm = Form(
        tuple(
            "email" -> text,
            "password" -> text
        ) verifying("Invalid email or password", result =>
            result match {
                case (email, password) => {
                    val user = Payola.model.userModel.getByCredentials(email, password)
                    user.isDefined

                }
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
            user => Redirect(routes.Application.dashboard).withSession( session + ("email" -> user._1))
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
        ) verifying("Username already taken.", _ match {
            case (email, password) => Payola.model.userModel.getByName(email).isEmpty
        })
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
            {   try {
                    Payola.model.userModel.create(user._1, user._2)
                    Redirect(routes.Application.dashboard).withSession(session + ("email" -> user._1))
                } catch {
                    case v: ValidationException =>
                        Redirect(routes.Application.signup)
                        .withSession("email" -> "sdsdsdsd")
                            .flashing("error" -> v.message)
                }
            }
        )
    }
}
