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

    def createNew() = authenticated { user: User =>



        Redirect(routes.DataSource.list())
    }

    def delete(id: String) = authenticated { user: User =>
        val ds: Option[cz.payola.domain.entities.plugins.DataSource] = Payola.model.dataSourceModel.getById(id)
        ds.map { d =>
            user.removeOwnedDataSource(d)
            Payola.model.dataSourceModel.remove(d)
            Payola.model.userModel.persist(user)

            Redirect(routes.DataSource.list)
        }.getOrElse {
            NotFound(views.html.errors.err404("The data source does not exist."))
        }
    }

    def detail(id: String) = maybeAuthenticated { user: Option[User] =>
        Payola.model.dataSourceModel.getById(id).map { d =>
            Ok(views.html.datasource.detail(user, d, None))
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
        form foreach { case (key, values) =>
            if (key == "__dataSourceName__") {
                // The data source name itself
                dataSource.name = values(0)
            }else if (key == "__dataSourceDescription__") {
                dataSource.description = values(0)
            }else if (key != "__dataSourceIsPublic__") {
                // A parameter value
                println("Setting " + key + " to " + values(0))

                val paramOption = dataSource.getParameterValue(key)
                assert(paramOption.isDefined, key + " is not a defined parameter name")
                dataSource.setParameter(paramOption.get, values(0))
            }
        }

        dataSource.isPublic = form.get("__dataSourceIsPublic__").isDefined

        Payola.model.dataSourceModel.persist(dataSource)

        Redirect(routes.DataSource.list())
    }


}
