package cz.payola.domain.entities.analyses.executors

import cz.payola.domain.entities.sources.SparqlEndpointDataSource
import scala.io.Source
import java.net.URL

class SparqlEndpointQueryExecutor(dataSource: SparqlEndpointDataSource) extends SingleQueryExecutor(dataSource)
{
    override protected def executeQuery(query: String): String = {
        val url = dataSource.endpointUrl + "?query=" + java.net.URLEncoder.encode(query, "UTF-8")
        val connection = new URL(url).openConnection()
        val requestProperties = Map(
            "Accept" -> "application/rdf+xml"
        )

        requestProperties.foreach(p => connection.setRequestProperty(p._1, p._2))
        Source.fromInputStream(connection.getInputStream, "UTF-8").mkString
    }
}
