package cz.payola.data.rdf.providers

import configurations.SparqlEndpointConfiguration
import scala.io.Source

class SparqlDataProvider(configuration: SparqlEndpointConfiguration) extends SingleDataProvider
{
    override protected def executeQuery(query: String): String = {
        // Query is composed and URL Coded
        val request = composeQueryRequest(query);

        // Return query result
        Source.fromURL(request, "UTF-8").mkString
    }

    private def composeQueryRequest(query: String): String = {
        // Request already contains query param -> fill value
        if (configuration.url.contains("query=")) {
            configuration.url.replaceAllLiterally(
                "query=",
                "query=" + java.net.URLEncoder.encode(query, "UTF-8"))
        }

        // Append query to the end of pre-configured request
        configuration.url + "&query=" + java.net.URLEncoder.encode(query, "UTF-8")
    }
}
