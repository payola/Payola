package cz.payola.web.shared.managers

import s2js.compiler._
import cz.payola.domain.entities.plugins.DataSource
import cz.payola.web.shared.Payola
import cz.payola.domain.entities.User
import cz.payola.common.rdf.Graph
import cz.payola.data.DataException
import cz.payola.common._
import cz.payola.domain.entities.plugins.concrete.DataFetcher

@remote @secured object DataSourceManager
    extends ShareableEntityManager[DataSource, cz.payola.common.entities.plugins.DataSource](
        Payola.model.dataSourceModel)
{

    private def getOwnedDataSourceByIDSync(dataSourceID: String, user: User): DataSource = {
        // See is the user has access to the data source
        Payola.model.dataSourceModel.getAccessibleToUserById(Some(user), dataSourceID) match {
            case Some(ds: DataSource) => ds
            case _ => throw new PayolaException("Invalid ID of the data source.")
        }
    }

    @async def changeDataSourceDescription(dataSourceID: String, newDescription: String, user: User = null)
        (successCallback: () => Unit)
        (failCallback: Throwable => Unit) {

        val dataSource = getOwnedDataSourceByIDSync(dataSourceID, user)
        dataSource.description = newDescription
        Payola.model.dataSourceModel.persist(dataSource)
        successCallback()
    }

    @async def changeDataSourceName(dataSourceID: String, newName: String, user: User = null)
        (successCallback: () => Unit)
        (failCallback: Throwable => Unit) {
        val dataSource = getOwnedDataSourceByIDSync(dataSourceID, user)

        // Now make sure a DS with this name doesn't exist
        val conflictingDataSource = Payola.model.dataSourceModel.getByName(newName)
        if (conflictingDataSource.isDefined && conflictingDataSource.get != dataSource) throw new ValidationException("Data source with this name already exists.")

        dataSource.name = newName
        Payola.model.dataSourceModel.persist(dataSource)

        successCallback()
    }

    @async def changeDataSourceParameterValue(dataSourceID: String, parameterID: String, newValue: String, user: User = null)
        (successCallback: () => Unit)
        (failCallback: Throwable => Unit) {

        val dataSource = getOwnedDataSourceByIDSync(dataSourceID, user)
        val parameter = dataSource.plugin.parameters.find(_.id == parameterID)
        if (parameter.isEmpty){
            throw new PayolaException("Wrong parameter ID.")
        }

        dataSource.setParameter(parameter.get, newValue)
        val parameterValue = dataSource.parameterValues.find(_.parameter.id == parameterID)
        if (parameterValue.isEmpty) {
            throw new PayolaException("Fatal error: non-existing parameter.")
        }

        Payola.model.dataSourceModel.persistParameterValue(parameterValue.get)
        //    Payola.model.dataSourceModel.persist(dataSource)
        successCallback()
    }

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

    @async def getOwnedDataSourceByID(dataSourceID: String, user: User = null)
        (successCallback: cz.payola.common.entities.plugins.DataSource => Unit)
        (failCallback: Throwable => Unit) {
        val dsOption = Payola.model.dataSourceModel.getAccessibleToUserById(Some(user), dataSourceID)
        if (dsOption.isDefined){
            val ds = dsOption.get
            if (ds.owner.isEmpty){
                throw new PayolaException("This data source isn't owned by anyone.")
            }
            val owner = ds.owner.get
            if (owner.id != user.id){
                throw new PayolaException("Wrong owner.")
            }
            successCallback(ds)
        }else{
            throw new PayolaException("Invalid data source ID..")
        }

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
