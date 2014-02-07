package cz.payola.domain.rdf

import com.hp.hpl.jena.query._
import cz.payola.domain.DomainException
import cz.payola.domain.net.Downloader

class SparqlEndpoint(val endpointURL: String)
{
    def queryUrl(query: String) = endpointURL + "?query=" + java.net.URLEncoder.encode(query, "UTF-8")

    private def _executeQuery[B](query: String)(inflector: ((RdfRepresentation.Type, String) => B)): B = {
        QueryFactory.create(query).getQueryType match {
            case Query.QueryTypeConstruct => {
                inflector(RdfRepresentation.RdfXml, new Downloader(queryUrl(query), "application/rdf+xml").result)
            }
            case Query.QueryTypeSelect => {
                inflector(RdfRepresentation.Turtle, new Downloader(queryUrl(query), "text/rdf+n3").result)
            }
            case _ => throw new DomainException(
                "Unsupported query type. The only supported query types are CONSTRUCT and SELECT.")
        }
    }

    def executeQuery(query: String): Graph = {
        _executeQuery[JenaGraph](query){ (representation, data) =>
            JenaGraph(representation, data)
        }
    }

    def executeQueryJena(query: String): com.hp.hpl.jena.query.Dataset = {
        _executeQuery[com.hp.hpl.jena.query.Dataset](query){ (representation, data) =>
            Graph.rdf2JenaDataset(representation, data)
        }
    }
}
