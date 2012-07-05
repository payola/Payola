package cz.payola.domain.entities.plugins.concrete.data

import scala.collection.immutable
import cz.payola.domain.IDGenerator
import cz.payola.domain.entities.plugins._
import cz.payola.domain.entities.plugins.concrete.DataFetcher
import cz.payola.domain.entities.plugins.parameters.StringParameter
import cz.payola.domain.rdf._
import cz.payola.domain.virtuoso.PayolaVirtuosoStorage

sealed class PayolaStorage(name: String, inputCount: Int, parameters: immutable.Seq[Parameter[_]], id: String)
    extends DataFetcher(name, inputCount, parameters, id)
{
    def this() = this("Payola Private Storage", 0, List(new StringParameter("GroupURI", "")), IDGenerator.newId)

    def executeQuery(instance: PluginInstance, query: String): Graph = {
        usingDefined(instance.getStringParameter("GroupURI")) { groupURI =>
            PayolaVirtuosoStorage.executeSPARQLQuery(query, groupURI)
        }
    }
}
