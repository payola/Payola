package cz.payola.domain.rdf

import cz.payola.domain.DomainException
import cz.payola.domain.net.Downloader
import com.hp.hpl.jena.query._

/**
 * A class representing secured virtuoso endpoint.
 *
 * @author Jiri Helmich
 * @param endpointURL The URL of the secured endpoint the user would like to fetch from.
 * @param username Secured endpoint username.
 * @param password Secured endpoint password.
 */
class VirtuosoSecuredEndpoint(val endpointURL: String, val username: String, val password: String)
{
    def executeQuery(query: String): Graph = {
        val queryUrl = endpointURL + "?query=" + java.net.URLEncoder.encode(query, "UTF-8")
        QueryFactory.create(query).getQueryType match {
            case Query.QueryTypeConstruct => {
                JenaGraph(RdfRepresentation.RdfXml, new Downloader(queryUrl, "application/rdf+xml", credentials = Some((username, password))).result)
            }
            case Query.QueryTypeSelect => {
                JenaGraph(RdfRepresentation.Turtle, new Downloader(queryUrl, "text/rdf+n3", credentials = Some((username, password))).result)
            }
            case _ => throw new DomainException(
                "Unsupported query type. The only supported query types are CONSTRUCT and SELECT.")
        }
    }
}
