package cz.payola.model.components

import cz.payola.domain.RdfStorageComponent
import cz.payola.domain.entities.User
import cz.payola.domain.rdf._
import cz.payola.common.rdf._
import cz.payola.domain.entities.AnalysisResult
import cz.payola.data.DataContextComponent
import scala.collection._
import cz.payola.domain.entities.plugins.concrete.data.PayolaStorage
import cz.payola.common.rdf.Graph
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

            def saveGraph(graph: Graph, analysisId: String, evaluationId: String/*, persist: Boolean*/, host: String, user: Option[User] = None) {

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
                    analysisId, user, evaluationId, true, graph.vertices.size, //persisting all
                    new java.sql.Timestamp(System.currentTimeMillis)))

                val uri = constructUri(evaluationId)

                val serializedGraph = domainGraph.toStringRepresentation(RdfRepresentation.RdfXml)

                val tmpFile = new File("/opt/www/virtuoso/evaluation/"+evaluationId+".rdf")
                printToFile(tmpFile)(p => {
                    p.println(serializedGraph)
                })

                //store graph in virtuoso
                //rdfStorage.storeGraph(uri, serializedGraph)
                //rdfStorage.storeGraphFromFile(uri, new File("/tmp/"+evaluationId+".rdf"), RdfRepresentation.RdfXml)
                rdfStorage.storeGraphAtURL(uri, "http://"+host+"/evaluation/"+evaluationId+".rdf")

                tmpFile.delete()
            }

            /**
             * Checks if the evaluation if stored.
             */
            def exists(evaluationId: String) = analysisResultRepository.exists(evaluationId)

            /**
             * Get whole graph
             */
            def getGraph(evaluationId: String): Graph = {
                getGraph("CONSTRUCT { ?s ?p ?o } WHERE {?s ?p ?o.}", evaluationId)
            }

            def getGraph(sparqlQuery: String, evaluationId: String): Graph = {
                val graphSize = rdfStorage.executeSPARQLQuery("SELECT (COUNT(*) as ?graphsize) WHERE {?s ?p ?o.}", constructUri(evaluationId))
                val graphVerticesCount = graphSize.edges.find(_.uri.contains("value")).map(_.destination.toString().toInt)

                val graph = rdfStorage.executeSPARQLQuery(sparqlQuery, constructUri(evaluationId), graphVerticesCount)
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

            def getGraph(sparqlQueryList: List[String], evaluationId: String): Graph = {
                val graphSize = rdfStorage.executeSPARQLQuery("SELECT (COUNT(*) as ?graphsize) WHERE {?s ?p ?o.}", constructUri(evaluationId))
                val graphVerticesCount = graphSize.edges.find(_.uri.contains("value")).map(_.destination.toString().toInt)

                val graph = sparqlQueryList.map{ query =>
                    rdfStorage.executeSPARQLQuery(query, constructUri(evaluationId), graphVerticesCount)
                }.reduce(_ + _)
                analysisResultRepository.updateTimestamp(evaluationId)
                graph
            }

            def removeGraph(evaluationId: String, analysisId: String) {
                rdfStorage.deleteGraph(constructUri(evaluationId))
                analysisResultRepository.deleteResult(evaluationId, analysisId)
            }

            private def constructUri(evaluationId: String): String = {
                "http://"+evaluationId
            }

            def getEmptyGraph(): Graph = {
                Graph.empty
            }
        }

    val maxStoredAnalyses: Long

    val maxStoredAnalysesPerUser: Long
}