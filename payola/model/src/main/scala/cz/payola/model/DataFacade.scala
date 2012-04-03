package cz.payola.model

import cz.payola.common.rdf.Graph
import cz.payola.data.rdf.configurations.SparqlEndpointConfiguration
import cz.payola.data.rdf.QueryExecutor
import cz.payola.domain.rdf.RDFGraph

class DataFacade
{
    def getGraph(uri: String): Option[Graph] = {
        val dbPediaEndpointUrl = "http://dbpedia.org/sparql" +
            "?default-graph-uri=http%3A%2F%2Fdbpedia.org" +
            "&format=application%2Frdf%2Bxml" +
            "&save=display"
        val configurations = List(new SparqlEndpointConfiguration(dbPediaEndpointUrl))

        val query = """
            CONSTRUCT {
                <%s> ?p1 ?n1 .
                ?n1 ?p2 ?n2 .
            }
            WHERE {
                <%s> ?p1 ?n1 .
                OPTIONAL { ?n1 ?p2 ?n2 }
            }
            LIMIT 40
        """.format(uri, uri)
       
        QueryExecutor.executeQuery(configurations, query).data.headOption.map(rdf => RDFGraph(rdf))
    }
}
