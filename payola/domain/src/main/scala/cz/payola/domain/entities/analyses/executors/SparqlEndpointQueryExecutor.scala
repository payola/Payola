package cz.payola.domain.entities.analyses.executors

import scala.io.Source
import cz.payola.domain.entities.sources.SparqlEndpointDataSource

class SparqlEndpointQueryExecutor(dataSource: SparqlEndpointDataSource) extends SingleQueryExecutor(dataSource)
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
        dataSource.endpointUrl + "&query=" + java.net.URLEncoder.encode(query, "UTF-8")
    }
}
