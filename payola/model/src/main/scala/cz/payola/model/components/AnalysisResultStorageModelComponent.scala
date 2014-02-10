package cz.payola.model.components

import cz.payola.domain.RdfStorageComponent
import cz.payola.domain.entities.User
import cz.payola.domain.rdf.RdfRepresentation
import cz.payola.common.rdf._
import cz.payola.domain.entities.AnalysisResult
import cz.payola.data.DataContextComponent
import scala.collection._
import java.io._
import scala.actors.Futures._



import com.hp.hpl.jena.query._
import com.hp.hpl.jena.rdf.model._
import org.apache.jena.riot._
import org.apache.jena.riot.lang._

trait AnalysisResultStorageModelComponent
{
    self: DataContextComponent with RdfStorageComponent =>

    lazy val analysisResultStorageModel = new
        {
            private def printToFile(f: java.io.File)(op: java.io.PrintWriter => Unit) {
                val p = new java.io.PrintWriter(f, "UTF-8")
                try { op(p) } finally { p.close() }
            }

            def queryProperties(evaluationId: String, query: String) : scala.collection.Seq[String] = {

                val graph = rdfStorage.executeSPARQLQuery(query, constructUri(evaluationId))
                graph.edges
                    .filter(_.uri == "http://www.w3.org/2005/sparql-results#value").map(_.destination.toString)
                    .filterNot(_.startsWith("http://schema.org"))
            }

            def saveGraph(graph: Graph, analysisId: String, evaluationId: String, host: String, user: Option[User] = None) {

                if(!graph.isInstanceOf[cz.payola.domain.rdf.Graph]) {
                    return
                }

                val domainGraph = graph.asInstanceOf[cz.payola.domain.rdf.Graph]

                //store control in DB
                analysisResultRepository.storeResult(new AnalysisResult(
                    analysisId, user, evaluationId, graph.vertices.size,
                    new java.sql.Timestamp(System.currentTimeMillis)))

                val uri = constructUri(evaluationId)

                val serializedGraph = domainGraph.toStringRepresentation(RdfRepresentation.RdfXml)

                val tmpFile = new File("/opt/www/virtuoso/evaluation/"+evaluationId+".rdf")
                printToFile(tmpFile)(p => {
                    p.println(serializedGraph)
                })

                rdfStorage.storeGraphAtURL(uri, "http://"+host+"/evaluation/"+evaluationId+".rdf")

                tmpFile.delete()
            }

            def getGraph(evaluationId: String): Graph = {

                //Console.println("Trying to load graph")
                val graph = rdfStorage.executeSPARQLQuery("CONSTRUCT { ?s ?p ?o } WHERE {?s ?p ?o.}", constructUri(evaluationId))
                analysisResultRepository.updateTimestamp(evaluationId)
                graph
            }

            def getGraphJena(evaluationId: String, format: String = "RDF/JSON"): String = {
                val dataset = rdfStorage.executeSPARQLQueryJena("CONSTRUCT { ?s ?p ?o } WHERE {?s ?p ?o.}", constructUri(evaluationId))
                analysisResultRepository.updateTimestamp(evaluationId)

                val outputStream = new java.io.ByteArrayOutputStream()
                if (format.toLowerCase == "json-ld"){
                    com.github.jsonldjava.jena.JenaJSONLD.init()
                    RDFDataMgr.write(outputStream, dataset, com.github.jsonldjava.jena.JenaJSONLD.JSONLD)
                }else{
                    dataset.getDefaultModel().write(outputStream, format)
                }

                new String(outputStream.toByteArray(),"UTF-8")

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
                    val edgesByEdgeType = edgesByOrigin.getOrElseUpdate(edge.origin.uri, new mutable.HashMap[String, mutable.ListBuffer[Edge]])
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
