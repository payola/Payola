package cz.payola.model.components

import cz.payola.domain.RdfStorageComponent
import cz.payola.domain.entities.User
import cz.payola.domain.rdf.RdfRepresentation
import cz.payola.common.rdf._
import cz.payola.domain.entities.AnalysisResult
import cz.payola.data.DataContextComponent
import scala.collection._

trait AnalysisResultStorageModelComponent
{
    self: DataContextComponent with RdfStorageComponent =>

    lazy val analysisResultStorageModel = new
        {
            def saveGraph(graph: Graph, analysisId: String, evaluationId: String, persist: Boolean, user: Option[User] = None) {

                if(!graph.isInstanceOf[cz.payola.domain.rdf.Graph]) {
                    return
                }

                val domainGraph = graph.asInstanceOf[cz.payola.domain.rdf.Graph]

                val inDB = analysisResultRepository.getResultsCount()
                if(inDB >= maxStoredAnalyses) {
                    analysisResultRepository.purge()      //TODO use removeGraph, this way the virtuoso might get filled up
                }

                //store control in DB
                analysisResultRepository.storeResult(new AnalysisResult(
                    analysisId, user, evaluationId, persist, graph.vertices.size,
                    new java.util.Date(System.currentTimeMillis)))

                val uri = constructUri(evaluationId)
                val serializedGraph = domainGraph.toStringRepresentation(RdfRepresentation.RdfXml)
                //store graph in virtuoso
                rdfStorage.storeGraph(uri, serializedGraph)
            }

            def getGraph(evaluationId: String): Graph = {

                //Console.println("Trying to load graph")
                val graph = rdfStorage.executeSPARQLQuery(
                    "CONSTRUCT { ?s ?p ?o } WHERE {?s ?p ?o.}", constructUri(evaluationId))
                analysisResultRepository.updateTimestamp(evaluationId)
                //Console.println("Im done")
                graph
            }

            def removeGraph(evaluationId: String, analysisId: String) {
                rdfStorage.deleteGraph(constructUri(evaluationId))
                analysisResultRepository.deleteResult(evaluationId, analysisId)
            }

            private def constructUri(evaluationId: String): String = {
                "http://"+evaluationId
            }

            def paginate(graph: Graph, page: Int = 0, allowedLinesOnPage: Int = 50): Graph = {
                //group edges by origin vertex
                val edgesByOrigin = new mutable.HashMap[String, mutable.HashMap[String, mutable.ListBuffer[Edge]]]
                graph.edges.foreach { edge =>
                    //get or create grouping by origin vertex
                    val edgesByEdgeType = edgesByOrigin.getOrElseUpdate(edge.origin.uri,
                        new mutable.HashMap[String, mutable.ListBuffer[Edge]])
                    //get or create grouping by edge and add edge
                    edgesByEdgeType.getOrElseUpdate(edge.uri, new mutable.ListBuffer[Edge]) += edge
                }

                var linesOnPage = 0
                var currentPageNumber = 0

                val requestedPageEdges = new mutable.ListBuffer[Edge]
                edgesByOrigin.foreach{ gE =>
                    linesOnPage += gE._2.size

                    if (linesOnPage > allowedLinesOnPage) {
                        currentPageNumber += 1
                        linesOnPage = gE._2.size
                    }

                    if(currentPageNumber != page) {
                        false
                    } else if (linesOnPage < allowedLinesOnPage){ //edge is on current page
                        gE._2.foreach{ groupedEdges =>
                            requestedPageEdges ++= groupedEdges._2
                        }
                    } else {
                        false
                    }
                }

                val numberOfPages = currentPageNumber + 1

                new Graph(immutable.Seq[Vertex](), requestedPageEdges.toList, Some(numberOfPages))
            }
        }



    val maxStoredAnalyses: Long

    val maxStoredAnalysesPerUser: Long
}
