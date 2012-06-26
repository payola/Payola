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

    def saveCreateGroup = authenticatedWithRequest { (request: Request[_], user: User) =>
        val name = groupForm.bindFromRequest()(request).get
        val groupOption = df.createGroup(name, user)

        if (groupOption.isDefined)
        {
            Redirect(routes.Profile.editGroup(groupOption.get.id)).flashing("success" -> "The group has been sucessfully created.")
        }else
        {
            Redirect(routes.Profile.listGroups()).flashing("error" -> "The group could not be created.")
        }
    }

    def saveGroup(id: String) = authenticatedWithRequest{ (request: Request[_], user: User) =>

        val data = request.body match {
            case AnyContentAsFormUrlEncoded(data) => data
            case _ => Map.empty[String, Seq[String]]
        }

        val membersNew = data.getOrElse("members",Nil).flatMap{ u => df.userDAO.getById(u) }
        val group = df.getGroupByOwnerAndId(user, id)

        if (group.isDefined)
        {
            val g = group.get
            g.name = data.getOrElse("name", g.name).toString
            g.members.diff(membersNew).map{m =>
                g.removeMember(m)
            }

            membersNew.diff(g.members).map{ m =>
                g.addMember(m)
            }

            df.groupDAO.persist(g)
            Redirect(routes.Profile.index(user.email)).flashing("success" -> "The group has been sucessfully saved.")
        }else{
            Forbidden
        }
    }

    def editGroup(id: String) = authenticatedWithRequest{ (request: Request[_], user: User) =>
        val g = df.getGroupByOwnerAndId(user, id)

        if (g.isDefined)
        {
            val allUsers = df.getAllUsers()

            Ok(views.html.Profile.editGroup(user, g.get, allUsers))
        }else{
            NotFound("The group does not exist.")
        }
    }

    def listGroups = authenticatedWithRequest( (request: Request[_], user: User) =>
        Ok(views.html.Profile.listGroups(user)(request.flash))
    )

    def deleteGroup(id: String) = authenticated{ user =>
        if (df.getGroupByOwnerAndId(user, id).isDefined && df.groupDAO.removeById(id))
        {
            Redirect(routes.Profile.listGroups()).flashing("success" -> "The group has been successfully deleted.")
        }else{
            Redirect(routes.Profile.listGroups()).flashing("error" -> "The group could not been deleted.")
        }
    }

    def createPlugin = authenticated{ user =>
        Ok(views.html.Profile.createPlugin(user))
    }
}
