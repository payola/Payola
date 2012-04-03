package cz.payola.data.rdf.providers

import scala.io.Source

class SparqlDataProvider(val endpointUrl: String) extends SingleDataProvider
{
    override protected def executeQuery(query: String): String = {
        Source.fromURL(getQueryRequestUrl(query), "UTF-8").mkString
    }

    /**
      * Appends the query parameter to the endpoint url.
      * @param query The query.
      * @return The request URL containing the query.
      */
    private def getQueryRequestUrl(query: String): String = {
        endpointUrl + "&query=" + java.net.URLEncoder.encode(query, "UTF-8")
    }
}
