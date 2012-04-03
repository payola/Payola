package cz.payola.web.shared

import cz.payola.common.rdf.Graph
import cz.payola.model.DataFacade

@remote object GraphFetcher
{
    def getInitialGraph: Graph = {
        (new DataFacade).getGraph("http://dbpedia.org/resource/Prague")
    }

    def getNeighborhoodOfVertex(vertexUri: String): Graph = {
        // TODO implement via calling model.
        null
    }
}
