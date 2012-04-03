package cz.payola.web.shared

import cz.payola.common.rdf.Graph
import cz.payola.model.DataFacade

@remote object GraphFetcher
{
    def getInitialGraph: Option[Graph] = {
        (new DataFacade).getGraph("http://payola.cz")
    }

    def getNeighborhoodOfVertex(vertexUri: String): Graph = {
        // TODO implement via calling model.
        null
    }
}
