package controllers

import helpers.Secured
import cz.payola.domain.entities.User

object Analysis extends PayolaController with Secured
{
    def detail(id: String) = maybeAuthenticated { user: Option[User] =>
        val analysis = df.getAnalysisById(id)
        analysis.map(a => Ok(views.html.analysis.detail(user, a))).getOrElse {
            NotFound(views.html.errors.err404("The analysis does not exist."))
        }
    }
}
