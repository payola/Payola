package cz.payola.web.shared

import s2js.compiler.secured
import cz.payola.common.rdf.Graph
import cz.payola.domain.entities.User

@remote object DataSourceBrowser
{
    // TODO change user type from User to Option[User].
    @secured def getInitialGraph(dataSourceId: String, user: User = null): Option[Graph] = {
        val dataSource = Payola.model.dataSourceModel.getAccessibleToUserById(Some(user), dataSourceId)
        val result = dataSource.flatMap(d => d.getFirstTriple.map(e => d.getNeighbourhood(e.origin.uri)))
        result
    }

    @secured def getNeighbourhood(dataSourceId: String, vertexURI: String, user: User = null): Option[Graph] = {
        None

    }
}
