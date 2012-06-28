package cz.payola.domain.entities.plugins.concrete.data

import scala.collection.immutable
import cz.payola.domain.IDGenerator
import cz.payola.domain.entities.plugins._
import cz.payola.domain.entities.plugins.concrete.DataFetcher
import cz.payola.domain.entities.plugins.parameters.StringParameter
import cz.payola.domain.rdf._
import cz.payola.domain.sparql._
import cz.payola.domain.net.Downloader
import cz.payola.domain.sparql.Uri
import scala.Some
import cz.payola.domain.sparql.TriplePattern
import cz.payola.domain.sparql.Variable

sealed class SparqlEndpoint(name: String, inputCount: Int, parameters: immutable.Seq[Parameter[_]], id: String)
    extends DataFetcher(name, inputCount, parameters, id)
{
    def this() = this("SPARQL Endpoint", 0, List(new StringParameter("EndpointURL", "")), IDGenerator.newId)

    def executeQuery(instance: PluginInstance, query: String): Graph = {
        usingDefined(instance.getStringParameter("EndpointURL")) { endpointURL =>
            val queryUrl = endpointURL + "?query=" + java.net.URLEncoder.encode(query, "UTF-8")
            Graph(RdfRepresentation.RdfXml, new Downloader(queryUrl, accept = "application/rdf+xml").result)
        }
    }

    def getNeighbourhood(instance: PluginInstance, nodeURI: String, distance: Int = 1): Graph = {
        require(distance > 0, "The distance has to be a positive number.")

        val rootTriplePattern = TriplePattern(Uri(nodeURI), Variable("p0"), Variable("n1"))
        val neighbourTriplePatterns = (1 to (distance - 1)).map { i =>
            TriplePattern(Variable("n" + i), Variable("p" + i), Variable("n" + (i + 1)))
        }
        val triplePatterns = rootTriplePattern +: neighbourTriplePatterns
        val graphPattern = triplePatterns.foldRight(GraphPattern.empty)((t, g) => GraphPattern(List(t), List(g)))

        executeQuery(instance, ConstructQuery(triplePatterns, Some(graphPattern)).toString)
    }
}
