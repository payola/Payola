package controllers

import helpers.Secured
import play.api.mvc._

object Profile extends Controller with Secured
{
    def view(username: String) = Action {
        Ok(views.html.userProfile.index())
    }

    def edit(username: String) = IsAuthenticated { username => _ =>
        /*User.findByEmail(username).map { user =>
            Ok(
                html.dashboard(
                    Project.findInvolving(username),
                    Task.findTodoInvolving(username),
                    user
                )
            )
        }.getOrElse(Forbidden)*/
        Ok(views.html.userProfile.index())
    }
}
