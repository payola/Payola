package controllers

import helpers.Secured
import play.api.data._
import play.api.data.Forms._
import views._
import cz.payola.domain.entities._
import play.api.mvc._

object Profile extends PayolaController with Secured
{
    def index(username: String) = maybeAuthenticated { user: Option[User] =>
        df.getUserByUsername(username).map { profileUser =>
            val profileUserAnalyses = df.getPublicAnalysesByOwner(profileUser)

            Ok(views.html.Profile.index(user, profileUser, profileUserAnalyses))
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
        Ok(html.Profile.edit(user, profileForm))
    }

    // TODO is the username necessary here? A user may edit only his own profile...
    def save(username: String) = authenticated { user =>
        Ok("TODO")
    }

    def createGroup = authenticated { user =>
        Ok(html.Profile.createGroup(user, groupForm))
    }

    def saveGroup = authenticatedWithRequest { (request: Request[_], user: User) =>
        val name = groupForm.bindFromRequest()(request).get
        df.createGroup(name, user)

        Redirect(routes.Profile.index(user.email)).flashing("success" -> "The group has been sucessfully created.")
    }

    def editGroup(id: String) = authenticatedWithRequest{ (request: Request[_], user: User) =>
        val g = df.getGroupByOwnerAndId(user, id)

        if (g.isDefined)
        {
            Ok(views.html.Profile.editGroup(user, g.get))
        }else{
            NotFound("The group does not exist.")
        }
    }
    /*
    def removeGroupMember = authenticated( user =>
        //Ok()
    )
                */
    def deleteGroup = TODO
}
