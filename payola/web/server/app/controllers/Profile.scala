package controllers

import helpers.Secured
import play.api.data._
import play.api.data.Forms._
import views._
import cz.payola.domain.entities._
import play.api.mvc._
import cz.payola.web.shared.Payola

object Profile extends PayolaController with Secured
{
    def index(username: String) = maybeAuthenticated { user: Option[User] =>
        Payola.model.userModel.getByName(username).map { profileUser =>
            val profileUserAnalyses = Payola.model.analysisModel.getPublicByOwner(profileUser)
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

    def saveCreateGroup = authenticatedWithRequest { (user, request) =>
        val name = groupForm.bindFromRequest()(request).get
        val group = Payola.model.groupModel.create(name, user)

        if (group != null)
        {
            Redirect(routes.Profile.editGroup(group.id)).flashing("success" -> "The group has been sucessfully created.")
        }else
        {
            Redirect(routes.Profile.listGroups()).flashing("error" -> "The group could not be created.")
        }
    }

    def saveGroup(id: String) = authenticatedWithRequest{ (user, request) =>

        val data = request.body match {
            case AnyContentAsFormUrlEncoded(data) => data
            case _ => Map.empty[String, Seq[String]]
        }

        val membersNew = data.getOrElse("members",Nil).flatMap{ u => Payola.model.userModel.getById(u) }
        val group = user.ownedGroups.find(_.id == id)

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

            Payola.model.groupModel.persist(g)
            Redirect(routes.Profile.index(user.email)).flashing("success" -> "The group has been sucessfully saved.")
        }else{
            Forbidden
        }
    }

    def editGroup(id: String) = authenticatedWithRequest{ (user, request) =>
        val g = user.ownedGroups.find(_.id == id)

        if (g.isDefined)
        {
            val allUsers = Payola.model.userModel.getAll

            Ok(views.html.Profile.editGroup(user, g.get, allUsers))
        }else{
            NotFound("The group does not exist.")
        }
    }

    def listGroups = authenticatedWithRequest( (user, request) =>
        Ok(views.html.Profile.listGroups(user)(request.flash))
    )

    def deleteGroup(id: String) = authenticated{ user =>
        val group = user.ownedGroups.find(_.id == id)
        Redirect(routes.Profile.listGroups()).flashing(
            if (group.map(g => Payola.model.groupModel.remove(g)).getOrElse(false)) {
                "success" -> "The group has been successfully deleted."
            } else {
                "error" -> "The group could not been deleted."
            }
        )
    }
}
