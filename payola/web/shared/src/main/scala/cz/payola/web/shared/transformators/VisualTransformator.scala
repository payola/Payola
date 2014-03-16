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

    @async def getSampleGraph(evaluationId: String)
        (successCallback: Graph => Unit)(errorCallback: Throwable => Unit) {
        successCallback(Payola.model.analysisResultStorageModel.getEmptyGraph())
    }

    @async def getVerticesDetail(evaluationId: String, vertexURIs: List[String])
        (successCallback: Option[Graph] => Unit)(errorCallback: Throwable => Unit) {

        val queries = List("CONSTRUCT { <%s> ?p ?o . } WHERE { <%s> ?p ?o .}",
            "CONSTRUCT { ?o ?p <%s> . } WHERE { ?o ?p <%s> . }")
        val queryList: List[String] = queries.map{ query => vertexURIs.map{ uri => query.format(uri, uri) } }.reduce(_ ++ _)

        val vertexNeighbourhood = Payola.model.analysisResultStorageModel.getGraph(queryList, evaluationId)
        successCallback(if(vertexNeighbourhood.isEmpty) {
            None
        } else {
            Some(separetaIntoGroups(vertexURIs, vertexNeighbourhood))
        })
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
            Some(separetaIntoGroups(List(uri), vertexNeighbourhood))
        }
    }

    private def separetaIntoGroups(mainVertexURIs: List[String], graph: Graph): Graph = {
        val mainVertices = graph.vertices.collect{
            case iv: IdentifiedVertex if mainVertexURIs.exists(_ == iv.uri) => iv
        }

        val edges = graph.edges
        val (identifiedVertices, literalVertices) = graph.vertices.filter{
            vertex =>
                !mainVertexURIs.exists(_ == vertex.toString)}.partition{
            vertex =>
                vertex.isInstanceOf[IdentifiedVertex]
        }

        //separate identified vertices by the type of edge
        val validEdges = edges.filter{ edge =>
            edge.destination.isInstanceOf[IdentifiedVertex] &&
                (!mainVertexURIs.exists(_ == edge.destination.toString) || !mainVertexURIs.exists(_ == edge.origin.toString))
        }

        val groups = validEdges.map(_.destination.toString).distinct.map{ uniqueDestinationName =>
            val verticesToGroup = validEdges.filter(_.destination.toString == uniqueDestinationName).map{ validEdge =>
                if(!mainVertexURIs.exists(_ == validEdge.origin.uri)) {
                    new VertexLink(validEdge.origin.uri) }
                else {
                    new VertexLink(validEdge.destination.toString()) }
            }
            new VertexGroup("", verticesToGroup)
        }
        new Graph(mainVertices ++ groups ++ literalVertices, edges, None)
    }
}
