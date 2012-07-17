package controllers

import helpers.Secured
import cz.payola.domain.entities._
import cz.payola.web.shared.Payola
import cz.payola.domain.entities.plugins.concrete.DataFetcher
import cz.payola.domain.entities.plugins._
import scala.collection.mutable.ListBuffer
import scala.Some
import cz.payola.domain.entities.plugins.parameters._
import scala.Some

object DataSource extends PayolaController with Secured
{
    def create() = authenticated { user: User =>
        Ok(views.html.datasource.create(user))
    }

    def createNew() = authenticatedWithRequest { (user, request) =>
        // First thing to do is to get the form:
        assert(request.body.asFormUrlEncoded.isDefined, "Wrong POST content. Content isn't a URL-encoded form.")
        val form = request.body.asFormUrlEncoded.get

        // Get the fetcher name and retrieve it:
        val dataFetcherName = form("__dataSourceFetcherType__")(0)
        val pluginOption = Payola.model.pluginModel.getByName(dataFetcherName)
        assert(pluginOption.isDefined, "Plugin called " + dataFetcherName + " isn't defined!")

        // Sanity check
        val plugin = pluginOption.get
        assert(plugin.isInstanceOf[DataFetcher], "Plugin not a data fetcher! " + plugin.getClass)
        val dataFetcher = plugin.asInstanceOf[DataFetcher]

        val parameterValues = new ListBuffer[ParameterValue[_]]()
        form foreach { case (key, values) =>
            // Internal keys start with two underscores
            if (!key.startsWith("__")){
                val value = values(0)
                val parameterOption = dataFetcher.getParameter(key)
                assert(parameterOption.isDefined, "Got a parameter name " + key +", which isn't defined in the data fetcher.")

                parameterOption.get match {
                    case boolParam: BooleanParameter => parameterValues += boolParam.createValue(value == "true")
                    case intParam: IntParameter => parameterValues += intParam.createValue(value.toInt)
                    case floatParam: FloatParameter => parameterValues += floatParam.createValue(value.toFloat)
                    case stringParam: StringParameter => parameterValues += stringParam.createValue(value)
                    case otherParam => throw new Exception("Unknown parameter type - " + otherParam)
                }
            }
        }

        val dataSource = new DataSource(form("__dataSourceName__")(0),
                Some(user),
                dataFetcher,
                parameterValues.toList
        )
        dataSource.description = form("__dataSourceFetcherType__")(0)

        if (form.get("__dataSourceIsPublic__").isDefined){
            dataSource.isPublic = true
        }

        Payola.model.dataSourceModel.persist(dataSource)

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
