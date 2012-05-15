package cz.payola.domain.entities.analyses.plugins

import cz.payola.domain.rdf.Graph
import cz.payola.domain.entities.analyses.{PluginInstance, Plugin, Parameter}
import collection.immutable

abstract class SparqlQuery(name: String, inputCount: Int, parameters: immutable.Seq[Parameter[_]], id: String)
    extends Plugin(name, inputCount, parameters, id)
{
    def evaluate(instance: PluginInstance, inputs: IndexedSeq[Graph], progressReporter: Double => Unit): Graph = {
        val query = getQuery(instance)

        /** Unfortunately because of the the way Jena returns results,
          * it is necessary to distinguish each query type.
          */

        // TODO smarter matching

        // TODO wouldn't this fit better to the graph class? so code of this method would be just:
        // inputs(0).executeSPARQLQuery(getQuery(parameterValues))
        val graph = getQuery(instance).map { query =>
            if (query.contains("SELECT")) {
                inputs(0).executeSelectSPARQLQuery(query)
            } else if (query.contains("CONSTRUCT")) {
                inputs(0).executeConstructSPARQLQuery(query)
            } else {
                // TODO ASK and possibly DESCRIBE?
                throw new IllegalArgumentException("Unknown SPARQL query type (" + query + ")")
            }
        }

        graph.getOrElse(Graph.empty)
    }

    /**
      * Returns the query to execute based on the plugin instance.
      * @param instance The evaluated plugin instance.
      * @return The query.
      */
    def getQuery(instance: PluginInstance): Option[String]
}
