package cz.payola.web.shared

import cz.payola.common.rdf.Graph

@scala.remote
object GraphFetcher
{
    def getInitialGraph: Graph = {
        // TODO implement via calling model.
        null
    }

    def getNeighborhoodOfVertex(vertexUri: String): Graph = {
        // TODO implement via calling model.
        null
    }
}
