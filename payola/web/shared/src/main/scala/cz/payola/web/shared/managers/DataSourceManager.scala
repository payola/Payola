package cz.payola.web.shared.managers

import s2js.compiler._
import cz.payola.domain.entities.plugins.DataSource
import cz.payola.web.shared.Payola
import cz.payola.domain.entities.User
import cz.payola.common.rdf.Graph
import cz.payola.data.DataException
import cz.payola.common._
import cz.payola.domain.entities.plugins.concrete.DataFetcher
import scala.Some

@remote @secured object DataSourceManager
    extends ShareableEntityManager[DataSource, cz.payola.common.entities.plugins.DataSource](
        Payola.model.dataSourceModel)
{
    @async def create(name: String, description: String, pluginId: String, parameters: Seq[String], user: User = null)
        (successCallback: () => Unit)
        (failCallback: Throwable => Unit) {

        val dataSource = Payola.model.pluginModel.getById(pluginId) match {
            case Some(plugin: DataFetcher) => DataSource(name, Some(user), plugin.createInstance())
            case _ => throw new PayolaException("Invalid id of the plugin.")
        }
        dataSource.description = description
        dataSource.parameterValues.zipWithIndex.foreach { v =>
            dataSource.setParameter(v._1, parameters(v._2))
        }

        Payola.model.dataSourceModel.persist(dataSource)
        successCallback()
    }

    @async def getInitialGraph(dataSourceId: String, user: Option[User] = null)
        (successCallback: Option[Graph] => Unit)
        (failCallback: Throwable => Unit) {

        val graph = getDataSource(dataSourceId, user).flatMap { dataSource =>
            val uri = dataSource.getFirstTriple.map(_.origin.uri)
            uri.map(dataSource.getNeighbourhood(_))
        }
        successCallback(graph)
    }

    @async def getNeighbourhood(dataSourceId: String, vertexURI: String, user: Option[User] = null)
        (successCallback: Option[Graph] => Unit)
        (failCallback: Throwable => Unit) {

        val graph = getDataSource(dataSourceId, user).map(_.getNeighbourhood(vertexURI))
        successCallback(graph)
    }

    @async def executeSparqlQuery(dataSourceId: String, query: String, user: Option[User] = null)
        (successCallback: Option[Graph] => Unit)
        (failCallback: Throwable => Unit) {

        try {
            successCallback(getDataSource(dataSourceId, user).map(_.executeQuery(query)))
        } catch {
            case d: DataException => throw d
            case t => throw new ValidationException("sparqlQuery", t.getMessage)
        }
    }

    private def getDataSource(dataSourceId: String, user: Option[User]): Option[DataSource] = {
        Payola.model.dataSourceModel.getAccessibleToUserById(user, dataSourceId)
    }
}
