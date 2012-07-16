package cz.payola.web.shared

import s2js.compiler._
import cz.payola.common.rdf.Graph
import cz.payola.domain.entities.User
import cz.payola.domain.entities.plugins.DataSource
import scala.Some

@remote object DataSourceBrowser
{
    // TODO change user type from User to Option[User].
    @secured @async def getInitialGraph(dataSourceId: String, user: User = null)
        (successCallback: (Option[Graph] => Unit))
        (failCallback: (Throwable => Unit)) {

        val graph = getDataSource(dataSourceId, user).flatMap { dataSource =>
            val uri = dataSource.getFirstTriple.map(_.origin.uri)
            uri.map(dataSource.getNeighbourhood(_))
        }
        successCallback(graph)
    }

    // TODO change user type from User to Option[User].
    @secured @async def getNeighbourhood(dataSourceId: String, vertexURI: String, user: User = null)
        (successCallback: (Option[Graph] => Unit))
        (failCallback: (Throwable => Unit)) {

        val graph = getDataSource(dataSourceId, user).map(_.getNeighbourhood(vertexURI, 1))
        successCallback(graph)
    }

    private def getDataSource(dataSourceId: String, user: User): Option[DataSource] = {
        Payola.model.dataSourceModel.getAccessibleToUserById(Some(user), dataSourceId)
    }
}
