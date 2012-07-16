package controllers

import helpers.Secured
import cz.payola.domain.entities._
import play.api.mvc._
import cz.payola.web.shared.Payola
import cz.payola.domain.entities.plugins.concrete.DataFetcher
import scala.Some

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

    def detail(id: String) = maybeAuthenticated { user: Option[User] =>
        Payola.model.dataSourceModel.getById(id).map { d =>
            Ok(views.html.datasource.detail(user, d, Some("http://ld.opendata.cz/resource/rejskol.msmt.cz/facility/000055069")))
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

    def saveEdited(id: String) = authenticatedWithRequest { (user, request) =>
        // Before touching anything, get the data source
        val dataSourceOption = Payola.model.dataSourceModel.getAccessibleToUserById(Some(user), id)
        if (dataSourceOption.isEmpty){
            NotFound(views.html.errors.err404("The data source does not exist."))
        }else{
            val dataSource = dataSourceOption.get

            assert(request.body.asFormUrlEncoded.isDefined, "Wrong POST content. Content isn't a URL-encoded form.")
            val form = request.body.asFormUrlEncoded.get

            saveEditedDataSource(dataSource, form)
        }
    }

    def saveEditedDataSource(dataSource: plugins.DataSource, form: Map[String, Seq[String]]) = {
        // The name


        Redirect(routes.DataSource.list())
    }


}
