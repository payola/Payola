package controllers

import helpers.Secured
import cz.payola.data.entities.dao.FakeAnalysisDAO

object DataSource extends PayolaController with Secured
{
    def detail(id: String) = IsAuthenticatedWithFallback({ loggedUsername =>
        rh =>
            val d = df.getDataSourceById(id)
            d.isDefined match {
                case true => Ok(views.html.datasource.detail(getUser(rh), d.get))
                case false => NotFound(views.html.errors.err404("The data source does not exist."))
            }
    }, { _ =>
        val d = df.getDataSourceById(id)
        d.isDefined match {
            case true => Ok(views.html.datasource.detail(None, d.get))
            case false => NotFound(views.html.errors.err404("The data source does not exist."))
        }
    })
}
