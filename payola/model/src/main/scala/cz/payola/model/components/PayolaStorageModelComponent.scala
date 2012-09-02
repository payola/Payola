package cz.payola.model.components

import cz.payola.domain._
import cz.payola.domain.entities.User
import cz.payola.domain.entities.plugins.concrete.data.PayolaStorage
import cz.payola.domain.entities.plugins.DataSource
import cz.payola.model.ModelException
import java.io._
import scala.io.Source
import cz.payola.domain.rdf.RdfRepresentation

trait PayolaStorageModelComponent
{
    self: RdfStorageComponent with PluginModelComponent with DataSourceModelComponent =>

    lazy val payolaStorageModel = new
    {
        /**
          * Creates a private storage for the specified user.
          * @param user The owner of the private storage.
          * @return A data source corresponding to the private storage.
          */
        def createUsersPrivateStorage(user: User): DataSource = {
            val plugin = pluginModel.getByName(PayolaStorage.pluginName).getOrElse {
                throw new ModelException("The PayolaStorage plugin doesn't exist.")
            }

            // Create the "users database" in the rdf storage.
            val groupURI = user.id
            rdfStorage.createGroup(groupURI)

            // Create the corresponding data source.
            val instance = plugin.createInstance().setParameter(PayolaStorage.groupURIParameter, groupURI)
            val dataSource = DataSource("Private Storage of " + user.name, Some(user), instance)
            dataSource.isEditable = false
            dataSourceModel.persist(dataSource)
            dataSource
        }

        /** Stores a graph that is stored at graphURL to the user's private data storage.
          *
          * @param graphURL Graph URL.
          * @param user User.
          */
        def addGraphToUser(graphURL: String, user: User) {
            val graphID = IDGenerator.newId
            rdfStorage.storeGraphAtURL(graphID, graphURL)
            rdfStorage.addGraphToGroup(graphID, user.id)
        }

        /** Stores a graph that is stored in the file to the user's private data storage.
          *
          * @param file File with graph.
          * @param user User.
          */
        def addGraphToUser(file: File, user: User, rdfType: RdfRepresentation.Type) {
            val graphID = IDGenerator.newId
            rdfStorage.storeGraphFromFile(graphID, file, rdfType)
            rdfStorage.addGraphToGroup(graphID, user.id)
        }
    }
}
