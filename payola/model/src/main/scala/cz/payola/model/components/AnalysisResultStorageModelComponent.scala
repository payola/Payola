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
                    new java.sql.Timestamp(System.currentTimeMillis)))

                val uri = constructUri(evaluationId)
                val serializedGraph = domainGraph.toStringRepresentation(RdfRepresentation.RdfXml)
                //store graph in virtuoso
                rdfStorage.storeGraph(uri, serializedGraph)
            }

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
