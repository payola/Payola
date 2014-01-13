package cz.payola.web.shared.transformators

import s2js.compiler._
import cz.payola.common.rdf._
import cz.payola.web.shared.Payola

@remote object VisualTransformator extends GraphTransformator
{
    @async
    def transform(evaluationId: String)(successCallback: Graph => Unit)(errorCallback: Throwable => Unit) {
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
        (successCallback: Graph => Unit)(errorCallback: Throwable => Unit) {

        successCallback(getVertexNeighbourhood(evaluationId, vertexUri))
    }

    private def getOrigin(evaluationId: String): Graph = {
        //this hopefully gets always the same vertex
        val originVertexGraph = Payola.model.analysisResultStorageModel.getGraph(
            "CONSTRUCT { ?v ?p ?o . } WHERE { ?v ?p ?o .} LIMIT 1", evaluationId)

        getVertexNeighbourhood(evaluationId, originVertexGraph.vertices(0).toString)
    }

    private def getVertexNeighbourhood(evaluationId: String, uri: String): Graph = {

        val queryList = List("CONSTRUCT { <%s> ?p ?o . } WHERE { <%s> ?p ?o .}",
            "CONSTRUCT { ?o ?p <%s> . } WHERE { ?o ?p <%s> . }").map(_.format(uri, uri, uri, uri))
        val vertexNeighbourhood = Payola.model.analysisResultStorageModel.getGraph(queryList, evaluationId)

        separetaIntoGroups(uri, vertexNeighbourhood)
    }

    private def separetaIntoGroups(mainVertexURI: String, graph: Graph): Graph = {
        val mainVertex: IdentifiedVertex = graph.getVertexWithURI(mainVertexURI).get
        //TODO separate to more than one group by edges
        val edges = graph.edges//.filter{ edge => edge.origin.toString == mainVertex.uri || edge.destination.toString == mainVertex.uri } there should not be any other edges
        val splittedVertices = graph.vertices.partition{ vertex =>
                vertex != mainVertex && vertex.isInstanceOf[IdentifiedVertex]
                /*vertex match {
                case idVertex: IdentifiedVertex => true
                    //edges.exists{ edge => edge.origin.uri == idVertex.uri || edge.destination.toString == idVertex.uri } there should not be any other vertices
                case _ => false
            }*/
        }

        val toGroup = splittedVertices._1.map{neighbourVertex => new VertexLink(neighbourVertex.toString())}
        val group = new VertexGroup("", toGroup)

        new Graph(List(mainVertex, group) ++ splittedVertices._2.filter(_ != mainVertex), edges, None)
    }
}
