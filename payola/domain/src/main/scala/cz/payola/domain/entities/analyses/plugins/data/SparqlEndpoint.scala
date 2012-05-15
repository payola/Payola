package cz.payola.domain.entities.analyses.plugins.data

import cz.payola.domain.entities.analyses.plugins.DataFetcher
import cz.payola.domain.rdf.Graph
import java.net.URL
import scala.collection.immutable
import cz.payola.domain.entities.analyses.parameters.StringParameter
import cz.payola.domain.IDGenerator
import cz.payola.domain.entities.analyses._

sealed class SparqlEndpoint(
    name: String = "SPARQL Endpoint",
    inputCount: Int = 0,
    parameters: immutable.Seq[Parameter[_]] = List(new StringParameter("EndpointURL", "")),
    id: String = IDGenerator.newId)
    extends DataFetcher(name, inputCount, parameters, id)
{
    def evaluateWithQuery(instance: PluginInstance, query: String, progressReporter: Double => Unit): Graph = {
        val endpointUrl = instance.getStringParameter("EndpointURL").get
        val queryUrl = endpointUrl + "?query=" + java.net.URLEncoder.encode(query, "UTF-8")
        val connection = new URL(queryUrl).openConnection()
        val requestProperties = Map(
            "Accept" -> "application/rdf+xml"
        )

        requestProperties.foreach(p => connection.setRequestProperty(p._1, p._2))
        Graph(connection.getInputStream)
    }
}
