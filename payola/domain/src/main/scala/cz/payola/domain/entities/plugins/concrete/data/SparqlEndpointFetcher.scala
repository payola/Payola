package cz.payola.domain.entities.plugins.concrete.data

import scala.collection.immutable
import cz.payola.domain.IDGenerator
import cz.payola.domain.entities.plugins._
import cz.payola.domain.entities.plugins.concrete.DataFetcher
import cz.payola.domain.entities.plugins.parameters.StringParameter
import cz.payola.domain.rdf._
import com.hp.hpl.jena.query.QueryFactory

sealed class SparqlEndpointFetcher(name: String, inputCount: Int, parameters: immutable.Seq[Parameter[_]], id: String)
    extends DataFetcher(name, inputCount, parameters, id)
{
    def this() = {
        this("SPARQL Endpoint", 0, List(
            new StringParameter(SparqlEndpointFetcher.endpointURLParameter, "", false),
            new StringParameter(SparqlEndpointFetcher.graphURIsParameter, "", true)
        ), IDGenerator.newId)
    }

    def getEndpointURL(instance: PluginInstance): Option[String] = {
        instance.getStringParameter(SparqlEndpointFetcher.endpointURLParameter)
    }

    def getGraphURIs(instance: PluginInstance): Option[Seq[String]] = {
        instance.getStringParameter(SparqlEndpointFetcher.graphURIsParameter).map(_.split("\n").filter(_ != "").toList)
    }

    def executeQuery(instance: PluginInstance, query: String): Graph = {
        usingDefined(getEndpointURL(instance), getGraphURIs(instance)) { (endpointURL, graphURIs) =>
            // Remove the graph URIs specified directly in the query and use the ones specified in the endpoint.
            val sparqlQuery = QueryFactory.create(query)
            sparqlQuery.getGraphURIs.clear()
            graphURIs.foreach(sparqlQuery.addGraphURI(_))

            new SparqlEndpoint(endpointURL).executeQuery(sparqlQuery.toString)
        }
    }
}

object SparqlEndpointFetcher
{
    val endpointURLParameter = "Endpoint URL"

    val graphURIsParameter = "Graph URIs"
}
