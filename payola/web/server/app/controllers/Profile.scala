package controllers

import helpers.Secured
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import views._


object Profile extends PayolaController with Secured
{
    def index(username: String) = IsAuthenticatedWithFallback ({ loggedUsername => rh =>
        val u = df.getUserByUsername(username)
        u.isDefined match {
            case true => Ok(views.html.userProfile.index(getUser(rh), u.get))
            case false => NotFound(views.html.errors.err404("The user does not exist."))
        }
    }, {  _ =>
        val u = df.getUserByUsername(username)
        u.isDefined match {
            case true => Ok(views.html.userProfile.index(None, u.get))
            case false => NotFound(views.html.errors.err404("The user does not exist."))
        }
    })

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

    def edit(username: String) = IsAuthenticated { username => _ =>
        df.getUserByUsername(username).map { user =>
            Ok(
                html.userProfile.edit(user, profileForm)
            )
        }.getOrElse(Forbidden)
    }


    def save(username: String) = IsAuthenticated { username => _ =>
        Ok("TODO")
    }
}
