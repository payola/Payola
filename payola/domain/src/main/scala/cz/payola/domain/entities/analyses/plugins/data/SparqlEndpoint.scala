package cz.payola.domain.entities.analyses.plugins.data

import cz.payola.domain.entities.analyses.plugins.DataFetcher
import cz.payola.domain.rdf.Graph
import java.net.URL
import scala.collection.immutable
import cz.payola.domain.entities.analyses.parameters.StringParameter
import cz.payola.domain.IDGenerator
import cz.payola.domain.entities.analyses._
import cz.payola.domain.sparql._

sealed class SparqlEndpoint(
    name: String = "SPARQL Endpoint",
    inputCount: Int = 0,
    parameters: immutable.Seq[Parameter[_]] = List(new StringParameter("EndpointURL", "")),
    id: String = IDGenerator.newId)
    extends DataFetcher(name, inputCount, parameters, id)
{
    def executeQuery(instance: PluginInstance, query: String): Graph = {
        usingDefined(instance.getStringParameter("EndpointURL")) {endpointURL =>
            val queryUrl = endpointURL + "?query=" + java.net.URLEncoder.encode(query, "UTF-8")
            val connection = new URL(queryUrl).openConnection()
            val requestProperties = Map(
                "Accept" -> "application/rdf+xml"
            )

            requestProperties.foreach(p => connection.setRequestProperty(p._1, p._2))
            Graph(connection.getInputStream)
        }
    }

    def getNeighbourhood(instance: PluginInstance, nodeURI: String, distance: Int = 1): Graph = {
        require(distance > 0, "The distance has to be a positive number.")

        val rootQuery = ConstructQuery(TriplePattern(Uri(nodeURI), Variable("p0"), Variable("n1")))
        val optionalNeighboursQuery = (1 to (distance - 1)).foldRight(ConstructQuery.empty) {(i, query) =>
            val triple = TriplePattern(Variable("n" + i), Variable("p" + i), Variable("n" + (i + 1)))
            ConstructQuery(triple +: query.template, Some(GraphPattern(List(triple), query.pattern.toList, Nil)))
        }

        executeQuery(instance, (rootQuery + optionalNeighboursQuery).toString)
    }
}
