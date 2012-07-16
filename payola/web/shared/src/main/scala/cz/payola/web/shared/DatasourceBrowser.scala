package cz.payola.web.shared

import s2js.compiler.secured
import cz.payola.common.rdf.Graph
import cz.payola.domain.entities.User
import cz.payola.domain.entities.plugins.DataSource

@remote object DataSourceBrowser
{
    @secured def getInitialGraph(dataSourceId: String, user: Option[User] = null): Option[Graph] = {
        getDataSource(dataSourceId, user).flatMap { dataSource =>
            val uri = dataSource.getFirstTriple.map(_.origin.uri)
            uri.map(dataSource.getNeighbourhood(_))
        }
    }

    @secured def getNeighbourhood(dataSourceId: String, vertexURI: String, user: Option[User] = null): Option[Graph] = {
        getDataSource(dataSourceId, user).map(_.getNeighbourhood(vertexURI, 1))
    }

    private def getDataSource(dataSourceId: String, user: Option[User]): Option[DataSource] = {
        Payola.model.dataSourceModel.getAccessibleToUserById(user, dataSourceId)
    }
}
