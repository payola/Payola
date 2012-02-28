package cz.payola.web.shared

import cz.payola.common.rdf.Graph
//import cz.payola.data.DataFacade

@scala.remote
object GraphFetcher
{
    def getInitialGraph(): Graph = {
        //(new DataFacade).getGraph("http://payola.cz")
        null
    }

    def getNeighborhoodOfVertex(vertexUri: String): Graph = {
        // TODO implement via calling model.
        null
    }
}
