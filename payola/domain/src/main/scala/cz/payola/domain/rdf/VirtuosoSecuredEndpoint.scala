package cz.payola.domain.rdf

import cz.payola.domain.DomainException
import cz.payola.domain.net.Downloader
import com.hp.hpl.jena.query._

class VirtuosoSecuredEndpoint(val endpointURL: String, val username: String, val password: String)
{
    def executeQuery(query: String): Graph = {
        val queryUrl = endpointURL + "?query=" + java.net.URLEncoder.encode(query, "UTF-8")
        QueryFactory.create(query).getQueryType match {
            case Query.QueryTypeConstruct => {
                Graph(RdfRepresentation.RdfXml, new Downloader(queryUrl, "application/rdf+xml", credentials = Some((username, password))).result)
            }
            case Query.QueryTypeSelect => {
                Graph(RdfRepresentation.Turtle, new Downloader(queryUrl, "text/rdf+n3", credentials = Some((username, password))).result)
            }
            case _ => throw new DomainException(
                "Unsupported query type. The only supported query types are CONSTRUCT and SELECT.")
        }
    }
}
