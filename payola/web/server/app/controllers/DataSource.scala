package controllers

import helpers.Secured
import cz.payola.domain.entities.User
import play.api.mvc.Request

object DataSource extends PayolaController with Secured
{

    def detail(id: String) = maybeAuthenticated { user: Option[User] =>
        val dataSource = df.getDataSourceById(id)
        dataSource.map(
            d => Ok(views.html.datasource.detail(user, d))
        ).getOrElse {
            NotFound(views.html.errors.err404("The data source does not exist."))
        }
    }
}
