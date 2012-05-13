package cz.payola.model

import cz.payola.domain.rdf.Graph
import cz.payola.domain.entities.sources.SparqlEndpointDataSource

class DataFacade
{
    val defaultDataSources = List(
        new SparqlEndpointDataSource("DBpedia", owner = None, endpointUrl = "http://dbpedia.org/sparql" +
            "?default-graph-uri=http%3A%2F%2Fdbpedia.org&format=application%2Frdf%2Bxml&save=display")
    )

    def getGraph(uri: String): Graph = {
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

        // TODO: Where is DataProvider?
        // DataProvider.executeQuery(defaultDataSources, query).data.headOption.map(rdf => Graph(rdf)).get
        null
    }
}
