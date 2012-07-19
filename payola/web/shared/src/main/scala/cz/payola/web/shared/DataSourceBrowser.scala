package cz.payola.web.shared

import s2js.compiler._
import cz.payola.common.rdf.Graph
import cz.payola.domain.entities.User
import cz.payola.domain.entities.plugins.DataSource
import cz.payola.model.ModelException

@remote object DataSourceBrowser
{
    @secured @async def getDataSources(user: Option[User] = null)
        (successCallback: (Seq[DataSource] => Unit))
        (failCallback: (Throwable => Unit)) {

        successCallback(Payola.model.dataSourceModel.getAccessibleToUser(user))
    }

    @secured @async def getInitialGraph(dataSourceId: String, user: Option[User] = null)
        (successCallback: (Option[Graph] => Unit))
        (failCallback: (Throwable => Unit)) {

        val graph = getDataSource(dataSourceId, user).flatMap { dataSource =>
            val uri = dataSource.getFirstTriple.map(_.origin.uri)
            uri.map(dataSource.getNeighbourhood(_))
        }
        successCallback(graph)
    }

    @secured @async def getNeighbourhood(dataSourceId: String, vertexURI: String, user: Option[User] = null)
        (successCallback: (Option[Graph] => Unit))
        (failCallback: (Throwable => Unit)) {

        val graph = getDataSource(dataSourceId, user).map(_.getNeighbourhood(vertexURI, 1))
        successCallback(graph)
    }

    private def getDataSource(dataSourceId: String, user: Option[User]): Option[DataSource] = {
        Payola.model.dataSourceModel.getAccessibleToUserById(user, dataSourceId)
    }
}
