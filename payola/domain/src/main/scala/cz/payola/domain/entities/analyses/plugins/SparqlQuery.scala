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

        if (query.contains("SELECT")) {
            definedInputs(0).executeSelectSPARQLQuery(query)
        } else if (query.contains("CONSTRUCT")) {
            definedInputs(0).executeConstructSPARQLQuery(query)
        } else {
            // TODO ASK and possibly DESCRIBE?
            throw new IllegalArgumentException("Unknown SPARQL query type (" + query + ")")
        }
    }

    /**
      * Returns the query to execute based on the plugin instance.
      * @param instance The evaluated plugin instance.
      * @return The query.
      */
    def getQuery(instance: PluginInstance): String
}
