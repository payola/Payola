package cz.payola.domain.entities.plugins.concrete.data

import scala.collection.immutable
import cz.payola.domain._
import cz.payola.domain.entities.plugins._
import cz.payola.domain.entities.plugins.concrete.DataFetcher
import cz.payola.domain.entities.plugins.parameters.StringParameter
import cz.payola.domain.rdf._

object PayolaStorage
{
    val pluginName = "Payola Private Storage"

    val groupURIParameterName = "GroupURI"
}

sealed class PayolaStorage(name: String, inputCount: Int, parameters: immutable.Seq[Parameter[_]], id: String)
    (implicit val storageComponent: RdfStorageComponent)
    extends DataFetcher(name, inputCount, parameters, id)
{
    def this() = {
        this(PayolaStorage.pluginName, 0, List(new StringParameter(PayolaStorage.groupURIParameterName, "", false)),
            IDGenerator.newId)(null)
        isPublic = false
    }

    def executeQuery(instance: PluginInstance, query: String): Graph = {
        usingDefined(instance.getStringParameter("GroupURI")) { groupURI =>
            if (storageComponent == null) {
                throw new PluginException("The storage component is null. " +
                    "The plugin has to be instantiated with non-null storage component.")
            }
            storageComponent.rdfStorage.executeSPARQLQuery(query, groupURI)
        }
    }
}
