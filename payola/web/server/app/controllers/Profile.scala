package controllers

import helpers.Secured
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import views._


object Profile extends PayolaController with Secured
{
    def index(username: String) = maybeAuthenticated { user =>
        df.getUserByUsername(username).map { profileUser =>
            val profileUserAnalyses = df.getPublicAnalysesByOwner(profileUser)
            Ok(views.html.userProfile.index(user, profileUser, profileUserAnalyses))
        }.getOrElse {
            NotFound(views.html.errors.err404("The user does not exist."))
        }
    }

    val profileForm = Form(
        tuple(
            "email" -> text,
            "name" -> text
        ) verifying("Invalid email or password", result =>
            result match {
                case (email, name) => !df.getUserByUsername(email).isDefined
            }
        )
    )

    // TODO is the username necessary here? A user may edit only his own profile...
    def edit(username: String) = authenticated { user =>
        Ok(html.userProfile.edit(user, profileForm))
    }

    // TODO is the username necessary here? A user may edit only his own profile...
    def save(username: String) =  authenticated { user =>
        Ok("TODO")
    }
}
