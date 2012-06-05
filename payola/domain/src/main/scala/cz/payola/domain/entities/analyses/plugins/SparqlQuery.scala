package cz.payola.domain.entities.analyses.plugins

import cz.payola.domain.rdf.Graph
import cz.payola.domain.entities.analyses.{PluginInstance, Plugin, Parameter}
import collection.immutable

abstract class SparqlQuery(name: String, inputCount: Int, parameters: immutable.Seq[Parameter[_]], id: String)
    extends Plugin(name, inputCount, parameters, id)
{
    def evaluate(instance: PluginInstance, inputs: IndexedSeq[Option[Graph]], progressReporter: Double => Unit) = {
        val definedInputs = getDefinedInputs(inputs)
        val query = getQuery(instance)

        definedInputs(0).executeSPARQLQuery(query)
    }

    /**
      * Returns the query to execute based on the plugin instance.
      * @param instance The evaluated plugin instance.
      * @return The query.
      */
    def getQuery(instance: PluginInstance): String
}
