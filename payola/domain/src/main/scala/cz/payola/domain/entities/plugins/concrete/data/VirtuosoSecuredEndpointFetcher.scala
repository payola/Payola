package cz.payola.domain.entities.plugins.concrete.data

import scala.collection.immutable
import cz.payola.domain.IDGenerator
import cz.payola.domain.entities.plugins._
import cz.payola.domain.entities.plugins.concrete.DataFetcher
import cz.payola.domain.entities.plugins.parameters.StringParameter
import cz.payola.domain.rdf._
import com.hp.hpl.jena.query.QueryFactory
import scala.collection.JavaConversions._

/**
 * A custom plugin for fetching data from Virtuoso Secured Endpoint.
 *
 * (Digest HTTP Auth) The plugin's definition (especially username and PW params).
 *
 * @param name
 * @param inputCount
 * @param parameters
 * @param id
 * @author Jiri Helmich
 */
sealed class VirtuosoSecuredEndpointFetcher(name: String, inputCount: Int, parameters: immutable.Seq[Parameter[_]],
    id: String)
    extends DataFetcher(name, inputCount, parameters, id)
{
    def this() = {
        this("Virtuoso Secured SPARQL Endpoint", 0, List(
            new StringParameter(VirtuosoSecuredEndpointFetcher.endpointURLParameter, "", false, false, false, true),
            new StringParameter(VirtuosoSecuredEndpointFetcher.graphURIsParameter, "", true, false, false, true),
            new StringParameter(VirtuosoSecuredEndpointFetcher.usernameParameter, "", false),
            new StringParameter(VirtuosoSecuredEndpointFetcher.passwordParameter, "", false, false, true)
        ), IDGenerator.newId)
    }

    def getEndpointURL(instance: PluginInstance): Option[String] = {
        instance.getStringParameter(VirtuosoSecuredEndpointFetcher.endpointURLParameter)
    }

    def getGraphURIs(instance: PluginInstance): Option[Seq[String]] = {
        instance.getStringParameter(SparqlEndpointFetcher.graphURIsParameter).map(_.split("\\s+").filter(_ != "").toList)
    }

    def getUsername(instance: PluginInstance): Option[String] = {
        instance.getStringParameter(VirtuosoSecuredEndpointFetcher.usernameParameter)
    }

    def getPassword(instance: PluginInstance): Option[String] = {
        instance.getStringParameter(VirtuosoSecuredEndpointFetcher.passwordParameter)
    }

    def executeQuery(instance: PluginInstance, query: String): Graph = {
        usingDefined(getEndpointURL(instance), getGraphURIs(instance), getUsername(instance), getPassword(instance)) {
            (endpointURL, endpointGraphURIs, username, password) =>
                val sparqlQuery = QueryFactory.create(query)
                val queryGraphURIs = sparqlQuery.getGraphURIs.toList

                // Replace the graph URIs with intersection of them and URIs specified in the endpoint. If any of the two
                // collections is empty, then it represents all graphs.
                val union = endpointGraphURIs.union(queryGraphURIs)
                val intersection = endpointGraphURIs.intersect(queryGraphURIs)
                val graphURIs =
                    if (endpointGraphURIs.isEmpty || queryGraphURIs.isEmpty) {
                        Some(union)
                    } else if (intersection.nonEmpty) {
                        Some(intersection)
                    } else {
                        None
                    }

                // Execute the query only if the intersection wasn't empty.
                val result = graphURIs.map {
                    uris =>
                        sparqlQuery.getGraphURIs.clear()
                        uris.foreach(sparqlQuery.addGraphURI(_))
                        new VirtuosoSecuredEndpoint(endpointURL, username, password).executeQuery(sparqlQuery.toString)
                }

                result.getOrElse(JenaGraph.empty)
        }
    }
}

object VirtuosoSecuredEndpointFetcher
{
    val endpointURLParameter = "Endpoint URL"

    val graphURIsParameter = "Graph URIs"

    val usernameParameter = "Username"

    val passwordParameter = "Password"
}

