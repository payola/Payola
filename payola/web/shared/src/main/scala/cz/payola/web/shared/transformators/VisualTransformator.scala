package cz.payola.web.shared.transformators

import s2js.compiler._
import cz.payola.common.rdf._
import cz.payola.web.shared.Payola

@remote object VisualTransformator extends GraphTransformator
{
    @async
    def transform(evaluationId: String)(successCallback: Option[Graph] => Unit)(errorCallback: Throwable => Unit) {
        successCallback(getOrigin(evaluationId))
    }

    @async
    def isAvailable(input: Graph)(successCallback: Boolean => Unit)(errorCallback: Throwable => Unit) {
        successCallback(isAvailable(input))
    }

    def isAvailable(input: Graph): Boolean = {
        true //VisualTransformator is always available
    }

    @async def getSmapleGraph(evaluationId: String)
        (successCallback: Graph => Unit)(errorCallback: Throwable => Unit) {
        successCallback(Payola.model.analysisResultStorageModel.getEmptyGraph())
    }


    @async
    def getVertexDetail(evaluationId: String, vertexUri: String)
        (successCallback: Option[Graph] => Unit)(errorCallback: Throwable => Unit) {

        successCallback(getVertexNeighbourhood(evaluationId, vertexUri))
    }

    private def getOrigin(evaluationId: String): Option[Graph] = {
        //this hopefully gets always the same vertex
        val originVertexGraph = Payola.model.analysisResultStorageModel.getGraph(
            "CONSTRUCT { ?v ?p ?o . } WHERE { ?v ?p ?o .} LIMIT 1", evaluationId)
        if(originVertexGraph.isEmpty) {
            None
        } else {
            getVertexNeighbourhood(evaluationId, originVertexGraph.vertices(0).toString)
        }
    }

    private def getVertexNeighbourhood(evaluationId: String, uri: String): Option[Graph] = {

        val queryList = List("CONSTRUCT { <%s> ?p ?o . } WHERE { <%s> ?p ?o .}",
            "CONSTRUCT { ?o ?p <%s> . } WHERE { ?o ?p <%s> . }").map(_.format(uri, uri, uri, uri))
        val vertexNeighbourhood = Payola.model.analysisResultStorageModel.getGraph(queryList, evaluationId)
        if(vertexNeighbourhood.isEmpty) {
            None
        } else {
            Some(separetaIntoGroups(uri, vertexNeighbourhood))
        }
    }

    private def separetaIntoGroups(mainVertexURI: String, graph: Graph): Graph = {
        val mainVertex: IdentifiedVertex = graph.getVertexWithURI(mainVertexURI).get

        val edges = graph.edges
        val (identifiedVertices, literalVertices) = graph.vertices.partition{ vertex =>
                vertex != mainVertex && vertex.isInstanceOf[IdentifiedVertex]
        }

        //separate identified vertices by the type of edge
        val validEdges = edges.filter{ edge =>
            edge.destination.isInstanceOf[IdentifiedVertex] && (edge.destination.toString() != mainVertexURI || edge.origin.uri !=  mainVertexURI)
        }

        val groups = validEdges.map(_.uri).distinct.map{ uniqueEdgeName =>
            val verticesToGroup = validEdges.filter(_.uri == uniqueEdgeName).map{ validEdge =>
                if(validEdge.origin.uri !=  mainVertexURI) {
                    new VertexLink(validEdge.origin.uri) }
                else {
                    new VertexLink(validEdge.destination.toString()) }
            }
            new VertexGroup("", verticesToGroup)
        }

        new Graph(List(mainVertex) ++ groups ++ literalVertices.filter(_ != mainVertex), edges, None)
    }
}
