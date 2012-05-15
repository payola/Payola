package cz.payola.domain.entities.analyses.plugins.data

import cz.payola.domain.entities.analyses.plugins.DataFetcher
import cz.payola.domain.entities.analyses.PluginInstance
import cz.payola.domain.entities.analyses.parameters.StringParameter
import cz.payola.domain.rdf.Graph
import io.Source
import java.net.URL

sealed class SparqlEndpoint extends DataFetcher("SPARQL Endpoint", List(new StringParameter("EndpointURL", "")))
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
