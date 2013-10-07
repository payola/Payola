package cz.payola.model.components

import cz.payola.domain.RdfStorageComponent
import cz.payola.domain.entities.User
import cz.payola.domain.rdf.RdfRepresentation
import cz.payola.common.rdf._
import cz.payola.domain.entities.AnalysisResult
import cz.payola.data.DataContextComponent
import scala.Some
import scala.collection._

trait AnalysisResultStorageModelComponent
{
    //self: RdfStorageComponent with PluginModelComponent with DataSourceModelComponent =>
    self: DataContextComponent with RdfStorageComponent =>

    lazy val analysisResultStorageModel = new
        {
            def saveGraph(graph: Graph, user: User, analysisId: String, evaluationId: String, persist: Boolean) {
                //check how many graphs are stored for the user...if too many remove some and have nice day ;-)

                //Console.println("Trying to save graph")
                if(!graph.isInstanceOf[cz.payola.domain.rdf.Graph]) {
                    return
                }

                val domainGraph = graph.asInstanceOf[cz.payola.domain.rdf.Graph]

                //TODO save evaluationId
                val inDB = analysisResultRepository.getNumberOfStoredAnalyses()
                if(inDB >= maxStoredAnalyses) {
                    analysisResultRepository.deleteOldest()
                }
                //Console.println("1")
                val perUserInDb = analysisResultRepository.getNumberOfStoredForUser(user.id)

                //Console.println("2")
                if(perUserInDb >= maxStoredAnalysesPerUser) {
                    //Console.println("deleting")
                    analysisResultRepository.deleteOldest(user.id)
                }
                //Console.println("3")
                //store control in DB
                analysisResultRepository.storeAnalysis(new AnalysisResult(
                    analysisId, Some(user), evaluationId, persist, user.id, graph.vertices.size,
                    new java.util.Date(System.currentTimeMillis)))

                //Console.println("4")
                val uri = constructUri(user, analysisId)
                val serializedGraph = domainGraph.toStringRepresentation(RdfRepresentation.RdfXml)
                //store graph in virtuoso
                rdfStorage.storeGraph(uri, serializedGraph)
            }

            def getGraph(user: User, analysisId: String): Graph = {

                //Console.println("Trying to load graph")
                val graph = rdfStorage.executeSPARQLQuery(
                    "CONSTRUCT { ?s ?p ?o } WHERE {?s ?p ?o.}", constructUri(user, analysisId))
                analysisResultRepository.updateTimestamp(user.id, "" + analysisId)
                graph
            }

            def getEvaluationId(user: User, analysisId: String): Option[String] = {
                analysisResultRepository.getEvaluationId(user.id, analysisId)
            }

            def removeGraph(user: User, analysisId: String) {
                rdfStorage.deleteGraph(constructUri(user, analysisId))
                analysisResultRepository.deleteStoredAnalysis(user.id, "" + analysisId)
            }

            private def constructUri(user: User, analysisId: String): String = {
                "http://"+user+"/"+analysisId
            }

            /*def paginate(graph: Graph): Graph = { //TODO implement in next release with pagination layer
                var typed = Seq[VertexGroup]()
                var nonTyped = graph.vertices

                graph.edges.foreach{ edge =>
                    if(edge.uri.endsWith("#type")) {

                        val group = typed.find(_.uri == equals(getNameForGroup(edge.destination)))
                        if(group.isEmpty) {
                            nonTyped = nonTyped.filter(vertex => getNameForGroup(vertex) == getNameForGroup(edge.origin)
                                || getNameForGroup(vertex) == getNameForGroup(edge.destination))
                            typed :+= new VertexGroup(getNameForGroup(edge.origin), Seq(new VertexLink(edge.origin.uri)))
                        } else {
                            nonTyped = nonTyped.filter(vertex => getNameForGroup(vertex) == getNameForGroup(edge.destination))
                            group.get.content :+= new VertexLink(edge.origin.uri)
                        }
                    }
                }

                val result =
                    if(typed.size > 50) { //group groups
                        val groupOfTypeGroups = new VertexGroup("types", Seq[VertexLink]())
                        groupOfTypeGroups.content ++= typed
                        List(groupOfTypeGroups) ++ nonTyped.toList
                    } else {
                        typed.toList ++ nonTyped.toList
                    }

                //TODO nevracet hrany co nevedou mezi nevracenymi vrcholy - mozna vratit nove hrany

                new Graph(result, graph.edges, None)
            }*/

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

            /*private def getNameForGroup(vertex: Vertex): String = { //will be used in next release
                vertex match {
                    case i: IdentifiedVertex =>
                        i.uri
                    case i: LiteralVertex =>
                        i.value.toString()
                    case _ =>
                        vertex.toString
                }
            }*/
        }



    val maxStoredAnalyses: Long

    val maxStoredAnalysesPerUser: Long
}
