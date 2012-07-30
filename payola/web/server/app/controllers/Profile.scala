package controllers

import helpers.Secured
import play.api.data._
import play.api.data.Forms._
import views._
import cz.payola.domain.entities._
import cz.payola.web.shared.Payola

object Profile extends PayolaController with Secured
{
    /** Index page for user.
      *
      * @param username User.
      * @return The user's page.
      */
    def index(username: String) = maybeAuthenticated { user: Option[User] =>
        Payola.model.userModel.getByName(username).map { profileUser =>
            val profileUserAnalyses = Payola.model.analysisModel.getAccessibleToUserByOwner(user, profileUser)
            Ok(views.html.Profile.index(user, profileUser, profileUserAnalyses))
        }.getOrElse {
            NotFound(views.html.errors.err404("The user does not exist."))
        }
    }

    val profileForm = Form(
        tuple(
            "email" -> text,
            "name" -> text
        ) verifying("Invalid email or password", _ match {
                case (email, name) => Payola.model.userModel.getByName(email).isEmpty
        })
    )

    // TODO is the username necessary here? A user may edit only his own profile...
    def edit(username: String) = authenticated { user =>
        Ok(html.Profile.edit(user, profileForm))
    }

    // TODO is the username necessary here? A user may edit only his own profile...
    def save(username: String) = authenticated { user =>
        Ok("TODO")
    }

}
