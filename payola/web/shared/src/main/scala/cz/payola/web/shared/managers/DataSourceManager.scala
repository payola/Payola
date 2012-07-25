package cz.payola.web.shared.managers

import s2js.compiler._
import cz.payola.domain.entities.plugins.DataSource
import cz.payola.web.shared.Payola
import cz.payola.domain.entities.User
import cz.payola.common.rdf.Graph

@remote @secured object DataSourceManager
    extends ShareableEntityManager[DataSource, cz.payola.common.entities.plugins.DataSource](
        Payola.model.dataSourceModel)
{
    /** Returns true if a data source with this name already exists.
      *
      * @param name Name of the potential data source.
      * @param user User.
      * @return True or false.
      */
    def dataSourceExistsWithName(name: String, user: User = null): Boolean = {
        model.getAll().exists(_.name == name)
    }

    @async def getInitialGraph(dataSourceId: String, user: Option[User] = null)
        (successCallback: (Option[Graph] => Unit))
        (failCallback: (Throwable => Unit)) {

        val graph = getDataSource(dataSourceId, user).flatMap { dataSource =>
            val uri = dataSource.getFirstTriple.map(_.origin.uri)
            uri.map(dataSource.getNeighbourhood(_))
        }
        successCallback(graph)
    }

    @async def getNeighbourhood(dataSourceId: String, vertexURI: String, user: Option[User] = null)
        (successCallback: (Option[Graph] => Unit))
        (failCallback: (Throwable => Unit)) {

        val graph = getDataSource(dataSourceId, user).map(_.getNeighbourhood(vertexURI))
        successCallback(graph)
    }

    private def getDataSource(dataSourceId: String, user: Option[User]): Option[DataSource] = {
        Payola.model.dataSourceModel.getAccessibleToUserById(user, dataSourceId)
    }
}
