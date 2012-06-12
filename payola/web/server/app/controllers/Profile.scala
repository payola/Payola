package controllers

import helpers.Secured
import play.api.data._
import play.api.data.Forms._
import views._
import cz.payola.domain.entities._
import play.api.mvc._

object Profile extends PayolaController with Secured
{
    def index(username: String) = maybeAuthenticated { user =>
        df.getUserByUsername(username).map { profileUser =>
            val profileUserAnalyses = df.getPublicAnalysesByOwner(profileUser)
            val profileUserGroups = df.getGroupsByOwner(Some(profileUser))
            Ok(views.html.userProfile.index(user, profileUser, profileUserAnalyses, profileUserGroups))
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

    val groupForm = Form(
        "name" -> text
    )

    // TODO is the username necessary here? A user may edit only his own profile...
    def edit(username: String) = authenticated { user =>
        Ok(html.userProfile.edit(user, profileForm))
    }

    // TODO is the username necessary here? A user may edit only his own profile...
    def save(username: String) = authenticated { user =>
        Ok("TODO")
    }

    def createGroup = authenticated { user =>
        Ok(html.userProfile.createGroup(user, groupForm))
    }

    def saveGroup = authenticatedWithRequest { (request: Request[_], user: User) =>
        val name = groupForm.bindFromRequest()(request).get
        df.createGroup(name, user)

        //TODO flashMessage

        Redirect(routes.Profile.index(user.email))
    }

    def editGroup = Action{ implicit request =>
        Ok("TODO")
    }
    /*
    def removeGroupMember = authenticated( user =>
        //Ok()
    )

    def deleteGroup = authenticated { user =>
        Ok("TODO")
    }           */
}
