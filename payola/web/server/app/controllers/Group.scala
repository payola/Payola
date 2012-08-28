package controllers

import views.html
import cz.payola.web.shared.Payola
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.data.Form
import play.api.data.Forms._
import controllers.helpers.Secured
import cz.payola.common.ValidationException

object Group extends PayolaController with Secured
{
    /**Shows a group creation page.
     *
     * @return Page with group creation.
     */
    def create = authenticatedWithRequest {
        (user, request) =>
            Ok(html.group.create(user, groupForm)(request.flash))
    }

    /**Save a newly created group.
     *
     * @return Redirects to group listing.
     */
    def saveCreate = authenticatedWithRequest {
        (user, request) =>
            val name = groupForm.bindFromRequest()(request).get
            try {
                val group = Payola.model.groupModel.create(name, user)

                if (group != null) {
                    Redirect(routes.Group.edit(group.id))
                        .flashing("success" -> "The group has been sucessfully created.")
                } else {
                    Redirect(routes.Group.list()).flashing("error" -> "The group could not be created.")
                }
            }catch {
                case validationExc: ValidationException =>
                    Redirect(routes.Group.create()).flashing("error" -> validationExc.message)
                // Otherwise, let the exception bubble up
            }
    }

    /**Saves a group with id.
     *
     * @param id ID of group.
     * @return Redirects to the user index page.
     */
    def save(id: String) = authenticatedWithRequest {
        (user, request) =>

            val data = request.body match {
                case AnyContentAsFormUrlEncoded(data) => data
                case _ => Map.empty[String, Seq[String]]
            }

            val membersNew = data.getOrElse("members", Nil).flatMap {
                s => s.split(',')
            }.flatMap {
                uId => Payola.model.userModel.getById(uId)
            }
            val group = user.ownedGroups.find(_.id == id)

            if (group.isDefined) {
                val g = group.get
                g.name = data.getOrElse("name", Nil).head
                g.members.diff(membersNew).map {
                    m =>
                        g.removeMember(m)
                }

                membersNew.diff(g.members).map {
                    m =>
                        g.addMember(m)
                }

                Payola.model.groupModel.persist(g)
                Redirect(routes.Group.list()).flashing("success" -> "The group has been sucessfully saved.")
            } else {
                NotFound("The group does not exist.")
            }
    }

    /**Edit a group.
     *
     * @param id ID of a group to edit.
     * @return Redirects to the group listing.
     */
    def edit(id: String) = authenticatedWithRequest {
        (user, request) =>
            val g = user.ownedGroups.find(_.id == id)

            if (g.isDefined) {
                val allUsers = Payola.model.userModel.getAll()

                Ok(views.html.group.edit(user, g.get, allUsers))
            } else {
                NotFound("The group does not exist.")
            }
    }

    /**Shows the listing page for groups.
     *
     * @return Listing page for groups.
     */
    def list(page: Int = 1) = authenticatedWithRequest((user, request) =>
        Ok(views.html.group.list(Some(user),page)(request.flash))
    )

    /**Deletes group with id.
     *
     * @param id ID of the group to delete.
     * @return Redirects to the group listing.
     */
    def delete(id: String) = authenticated {
        user =>
            val group = user.ownedGroups.find(_.id == id)
            Redirect(routes.Group.list()).flashing(
                if (group.map(g => Payola.model.groupModel.remove(g)).getOrElse(false)) {
                    "success" -> "The group has been successfully deleted."
                } else {
                    "error" -> "The group could not been deleted."
                }
            )
    }

    val groupForm = Form(
        "name" -> text
    )
}
