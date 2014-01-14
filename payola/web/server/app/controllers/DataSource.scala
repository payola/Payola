package controllers

import helpers.Secured
import play.api.mvc._
import cz.payola.domain.entities._
import cz.payola.web.shared.Payola
import cz.payola.domain.entities.plugins.concrete.DataFetcher
import cz.payola.domain.entities.plugins._
import scala.collection.mutable.ListBuffer
import cz.payola.domain.entities.plugins.parameters._

object DataSource extends PayolaController with Secured
{
    /** Shows a create page so that the user can create a new data source.
      *
      * @return Data source creation page.
      */
    def create() = authenticated { user: User =>
        Ok(views.html.datasource.create(user))
    }

    /** Handles a create POST request, from which a new data source is created. The
      * user is redirected to the listing afterwards.
      *
      * @return Listing page.
      */
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

        Redirect(routes.DataSource.list(1))
    }

    /** Deletes an owned data source.
      *
      * @param id ID of the data source to delete.
      * @return Redirects back to the data source listing, or throws 404 if the resource doesn't exist.
      */
    def delete(id: String) = authenticated { user: User =>
        val ds: Option[cz.payola.domain.entities.plugins.DataSource] = Payola.model.dataSourceModel.getAccessibleToUserById(Some(user), id)
        ds.map { d =>
            user.removeOwnedDataSource(d)
            Payola.model.dataSourceModel.remove(d)
            Payola.model.userModel.persist(user)

            Redirect(routes.DataSource.list(1))
        }.getOrElse {
            NotFound(views.html.errors.err404("The data source does not exist."))
        }
    }

    /** Shows detail of the data source.
      *
      * @param id ID of the data source.
      * @param initialVertexUri URI of the initial vertex.
      * @return Detail of the data source or 404 if the resource doesn't exist.
      */
    def detail(id: String, initialVertexUri: Option[String]) = maybeAuthenticated { user: Option[User] =>
        Payola.model.dataSourceModel.getAccessibleToUserById(user, id).map { d =>
            Ok(views.html.datasource.detail(user, d, initialVertexUri))
        }.getOrElse {
            NotFound(views.html.errors.err404("The data source does not exist."))
        }
    }

    /** Shows an edit page of the data source.
      *
      * @param id ID of the data source.
      * @return Edit page of the data source or 404 if the resource doesn't exist.
      */
    def edit(id: String) = authenticated { user: User =>
        val dataSourceOpt = Payola.model.dataSourceModel.getAccessibleToUserById(Some(user), id)
        if (dataSourceOpt.isDefined && dataSourceOpt.get.isEditable) {
            Ok(views.html.datasource.edit(user, dataSourceOpt.get))
        }else{
            NotFound(views.html.errors.err404("The data source does not exist."))
        }
    }

    /** Lists owned data sources of the user.
      *
      * @return Listing page.
      */
    def list(page: Int = 1) = authenticated { user: User =>
        Ok(views.html.datasource.list(Some(user), user.ownedDataSources, page))
    }

    /** Lists data sources accessible by the user.
     *
     * @return Listing page.
     */
    def listAccessible(page: Int = 1) = maybeAuthenticated { user: Option[User] =>
        Ok(views.html.datasource.list(user, Payola.model.dataSourceModel.getAccessibleToUser(user), page, Some("Accessible data sources")))
    }

    /** Saves the edited data source.
      *
      * @param id ID of the data source.
      * @return Redirects back to the data source listing, or throws 404 if the resource doesn't exist.
      */
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

    /** Actually saves the edited data source.
      *
      * @param dataSource Edited data source.
      * @param form Form with values.
      * @return Redirects back to the data source listing.
      */
    private def saveEditedDataSource(dataSource: plugins.DataSource, form: Map[String, Seq[String]]) = {
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

                Payola.model.pluginModel.getAll()
            }
        }

        dataSource.isPublic = form.get("__dataSourceIsPublic__").isDefined

        Payola.model.dataSourceModel.persist(dataSource)

        Redirect(routes.DataSource.list())
    }

    def listAccessibleByOwner(ownerId: String, page: Int = 1) = maybeAuthenticated { user: Option[User] =>
        val owner = Payola.model.userModel.getById(ownerId)
        val analyses = if (owner.isDefined) {
            Payola.model.dataSourceModel.getAccessibleToUserByOwner(user, owner.get)
        }else{
            List()
        }
        Ok(views.html.datasource.list(user, analyses, page))
    }
}
