package controllers

import helpers.Secured
import play.api.data._
import play.api.data.Forms._
import views._
import cz.payola.domain.entities._
import cz.payola.web.shared.Payola
import play.api.mvc.Request
import cz.payola.common.ValidationException

object Profile extends PayolaController with Secured
{
    /**Index page for user.
     *
     * @param username User.
     * @return The user's page.
     */
    def index(username: String) = maybeAuthenticated {user: Option[User] =>
        Payola.model.userModel.getByName(username).map {profileUser =>
            val profileUserAnalyses = Payola.model.analysisModel.getAccessibleToUserByOwner(user, profileUser)
            val profileUserDataSources = Payola.model.dataSourceModel.getAccessibleToUserByOwner(user, profileUser)
            Ok(views.html.Profile.index(user, profileUser, profileUserAnalyses, profileUserDataSources))
        }.getOrElse {
            NotFound(views.html.errors.err404("The user does not exist."))
        }
    }

    def getForm(user: User): play.api.data.Form[(String, String, String)] = {
        Form(
            tuple(
                "email" -> text,
                "oldpassword" -> text,
                "password" -> text
            ) verifying("Current password does not match", _ match {
                case (email, oldpassword, password) => {
                    Payola.model.userModel.getByCredentials(user.name, oldpassword).isDefined
                }
            }) verifying("E-mail already taken", _ match {
                case (email, oldpassword, password) => {
                    (!email.isEmpty) && (email == user.email || Payola.model.userModel.getByEmail(email).isEmpty)
                }
            })
        )
    }

    def edit() = authenticatedWithRequest {(user: User, request: Request[_]) =>
        Ok(html.Profile.edit(user, getForm(user), request.flash))
    }

    def save() = authenticatedWithRequest {(user: User, request: Request[_]) =>
        try {
            getForm(user).bindFromRequest()(request).fold(
                formWithErrors => BadRequest(html.Profile.edit(user, formWithErrors, request.flash)),
                triple => {
                    user.password = Payola.model.userModel.cryptPassword(triple._3)
                    user.email = triple._1

                    Payola.model.userModel.persist(user)
                    Redirect(routes.Application.dashboard)
                }
            )
        } catch {
            case v: ValidationException =>
                Redirect(routes.Profile.edit).flashing("error" -> v.message)
        }
    }
}
