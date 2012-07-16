package controllers

import helpers.Secured
import cz.payola.domain.entities.User
import play.api.mvc.Request
import cz.payola.web.shared.Payola
import cz.payola.domain.entities.plugins.concrete.DataFetcher

object DataSource extends PayolaController with Secured
{
    def create() = authenticated { user: User =>
        Ok(views.html.datasource.create(user))
    }

    def delete(id: String) = authenticated { user: User =>
        val ds: Option[cz.payola.domain.entities.plugins.DataSource] = Payola.model.dataSourceModel.getById(id)
        ds.map { d =>
            user.removeOwnedDataSource(d)
            Payola.model.dataSourceModel.remove(d)
            Redirect(routes.DataSource.list)
        }.getOrElse {
            NotFound(views.html.errors.err404("The data source does not exist."))
        }
    }

    def detail(id: String, initialVertexUri: Option[String]) = maybeAuthenticated { user: Option[User] =>
        Payola.model.dataSourceModel.getById(id).map { d =>
            Ok(views.html.datasource.detail(user, d, initialVertexUri))
        }.getOrElse {
            NotFound(views.html.errors.err404("The data source does not exist."))
        }
    }

    def edit(id: String) = authenticated { user: User =>
        Payola.model.dataSourceModel.getById(id).map { d =>
            val availableDataFetchers = Payola.model.pluginModel.getAccessibleToUser(Some(user)).filter(p => p.isInstanceOf[DataFetcher]).asInstanceOf[Seq[DataFetcher]]
            Ok(views.html.datasource.edit(user, d, availableDataFetchers))
        }.getOrElse {
            NotFound(views.html.errors.err404("The data source does not exist."))
        }
    }

    def list() = authenticated { user: User =>
        Ok(views.html.datasource.list(user))
    }


}
