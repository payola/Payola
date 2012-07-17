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

    def edit(id: String) = authenticated { user =>
        Ok(views.html.analysis.edit(user, id))
    }

    def delete(id: String) = authenticated { user =>
        user.ownedAnalyses.find(_.id == id).map(Payola.model.analysisModel.remove(_))
            .getOrElse(NotFound("Analysis not found."))
        Redirect(routes.Application.dashboard())
    }

    def listOwned(page: Int = 1) = authenticated { user: User =>
        Ok(views.html.analysis.list(Some(user), user.ownedAnalyses, page, true))
    }

    def list(page: Int = 1) = maybeAuthenticated { user: Option[User] =>
        Ok(views.html.analysis.list(user, Payola.model.analysisModel.getAccessibleToUser(user), page))
    }
}
