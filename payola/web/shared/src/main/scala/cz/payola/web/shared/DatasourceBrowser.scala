package cz.payola.web.shared

import s2js.compiler.secured
import cz.payola.common.rdf.Graph
import cz.payola.domain.entities.User
import cz.payola.domain.entities.plugins.DataSource

@remote object DataSourceBrowser
{
    // TODO change user type from User to Option[User].
    @secured def getInitialGraph(dataSourceId: String, user: User = null): Option[Graph] = {
        getDataSource(dataSourceId, user).flatMap { dataSource =>
            val uri = dataSource.getFirstTriple.map(_.origin.uri)
            uri.map(dataSource.getNeighbourhood(_))
        }
    }

    // TODO change user type from User to Option[User].
    @secured def getNeighbourhood(dataSourceId: String, vertexURI: String, user: User = null): Option[Graph] = {
        getDataSource(dataSourceId, user).map(_.getNeighbourhood(vertexURI, 1))
    }

    private def getDataSource(dataSourceId: String, user: User): Option[DataSource] = {
        Payola.model.dataSourceModel.getAccessibleToUserById(Some(user), dataSourceId)
    }
}
