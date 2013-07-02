package controllers

import controllers.helpers.Secured
import cz.payola.domain.entities.User
import cz.payola.web.shared.Payola
import play.api.mvc.AnyContentAsFormUrlEncoded
import play.api.data.Form
import play.api.data.Forms._
import views.html
import cz.payola.common.ValidationException

/**
 *
 */
object Prefix extends PayolaController with Secured
{
    def list(page: Int = 1) = maybeAuthenticatedWithRequest { (user: Option[User], request) =>
        // Get prefixes from logged user / only general prefixes in proper order (first users, then general)
        val prefixes =
            user.map(_.availablePrefixes)
                .getOrElse(Payola.model.prefixModel.getAllAvailableToUser(None))
                    .sortBy(_.prefix).reverse.sortBy(p => p.owner.map(_.id)).reverse

        Ok(views.html.prefix.list(user, prefixes, page)(request.flash))
    }

    def create = authenticatedWithRequest {(user, request) =>
        Ok(views.html.prefix.create(user, prefixForm)(request.flash))
    }

    def saveCreate = authenticatedWithRequest {(user, request) =>
        val data = request.body match {
            case AnyContentAsFormUrlEncoded(data) => data
            case _ => Map.empty[String, Seq[String]]
        }

        val pref = data.getOrElse("prefix", Nil).head
        val url = data.getOrElse("url", Nil).head

        try {
             // Use prefix as a name
            val prefix = Payola.model.prefixModel.create(pref, url, Some(user))

            // If available prefixes are not loaded yet, this will ensure that this prefix will be available too
            if (!user.availablePrefixes.contains(prefix))
                user.addOwnedPrefix(prefix)

            Redirect(routes.Prefix.list()).flashing("success" -> "The prefix has been successfully saved.")
        }
        catch {
            case validationExc: ValidationException =>
                Redirect(routes.Prefix.create())
                    .flashing("error" -> validationExc.message)
            // Otherwise, let the exception bubble up
        }
    }

    def save(id: String) = authenticatedWithRequest {(user, request) =>
        val data = request.body match {
            case AnyContentAsFormUrlEncoded(data) => data
            case _ => Map.empty[String, Seq[String]]
        }

        val prefix = user.availablePrefixes.find(p => p.id == id && p.owner == Some(user))

        if (prefix.isDefined) {

            try {
                val p = prefix.get
                p.name = data.getOrElse("prefix", Nil).head // Really use prefix as a name
                p.prefix = data.getOrElse("prefix", Nil).head
                p.url = data.getOrElse("url", Nil).head

                // Validate data here ... ugly
                if (p.prefix.length == 0)
                    throw new ValidationException("prefix", "Prefix has to be specified")
                if (p.url.length == 0)
                    throw new ValidationException("url", "Url has to be specified.")

                Payola.model.prefixModel.persist(p)
                    Redirect(routes.Prefix.list())
                        .flashing("success" -> "The prefix has been successfully saved.")
            }
            catch {
                case validationExc: ValidationException =>
                    Redirect(routes.Prefix.edit(id))
                        .flashing("error" -> validationExc.message)
                // Otherwise, let the exception bubble up
            }
        } else {
            NotFound("The prefix does not exist.")
        }
    }

    def edit(id: String) = authenticatedWithRequest {(user, request) =>
        val prefix = user.availablePrefixes.find(p => p.id == id && p.owner == Some(user))
        if (prefix.isDefined)
            Ok(views.html.prefix.edit(user, prefix.get)(request.flash))
        else
            NotFound("The prefix does not exist.")
    }

    def delete(id: String) = authenticated {
        user =>
            try
            {
                val prefix = user.availablePrefixes.find(p => p.id == id && p.owner == Some(user))
                if (prefix.isDefined) {
                    user.removeOwnedPrefix(prefix.get)
                    Redirect(routes.Prefix.list()).flashing("success" -> "The prefix has been successfully deleted.")
                }
                else {
                    NotFound("The prefix does not exist.")
                }
            }
            catch {
                case validationExc: ValidationException =>
                    Redirect(routes.Prefix.list()).flashing("error" -> validationExc.message)
                // Otherwise, let the exception bubble up
            }
    }

    // Prefix form with input fields type definition
    val prefixForm = Form(
        //Map("name" -> text, "prefix" -> text, "url" -> text)
        "name" -> text
    )
}
