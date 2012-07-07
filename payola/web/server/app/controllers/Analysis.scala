package controllers

import helpers.Secured
import cz.payola.domain.entities.User
import cz.payola.web.shared.Payola

object Analysis extends PayolaController with Secured
{
    def detail(id: String) = maybeAuthenticated { user: Option[User] =>
        Payola.model.analysisModel.getById(id).map(a => Ok(views.html.analysis.detail(user, a))).getOrElse {
            NotFound(views.html.errors.err404("The analysis does not exist."))
        }
    }

    def create = authenticated { user =>
        Ok(views.html.analysis.create(user))
    }
}
