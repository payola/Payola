package cz.payola.web.shared.transformators

import cz.payola.web.shared.Payola
import s2js.compiler._
import scala.collection.immutable
import scala.collection.mutable
import cz.payola.common.rdf._

@remote object TripleTableTransformator extends GraphTransformator
{
    private val tripleTableDefaultPage = 0
    private val tripleTableDefaultRecordsOnPage = 50

    @async
    def transform(evaluationId: String)(successCallback: Option[Graph] => Unit)(errorCallback: Throwable => Unit) {
        successCallback(getGraphPage(evaluationId, tripleTableDefaultPage, tripleTableDefaultRecordsOnPage))
    }

    @async
    def isAvailable(input: Graph)
        (successCallback: Boolean => Unit)(errorCallback: Throwable => Unit) {

        successCallback(isAvailable(input))
    }

    def isAvailable(input: Graph): Boolean = {
        true //tripleTable is always available
    }

    @async def getSampleGraph(evaluationId: String)
        (successCallback: Graph => Unit)(errorCallback: Throwable => Unit) {
        successCallback(Payola.model.analysisResultStorageModel.getEmptyGraph())
    }


    @async def getCachedPage(evaluationId: String, page: Int = tripleTableDefaultPage, tripplesOnPage: Int = tripleTableDefaultRecordsOnPage)
        (successCallback: Option[Graph] => Unit)(errorCallback: Throwable => Unit) {

        val pageNumber = if(page > -1) page else tripleTableDefaultPage
        val recordsOnPage = if(tripplesOnPage > -1) tripplesOnPage else tripleTableDefaultRecordsOnPage
        successCallback(getGraphPage(evaluationId, pageNumber, recordsOnPage))
    }

    /**
     * Returns first page of the graph using default count of records on page
     */
    private def getGraph(evaluationId: String): Option[Graph] = {
        getGraphPage(evaluationId, tripleTableDefaultPage, tripleTableDefaultRecordsOnPage)
    }


    private def getGraphPage(evaluationId: String, pageNumber: Int, recordsOnPage: Int): Option[Graph] = {
        //Console.println("CONSTRUCT { ?s ?p ?o } WHERE {?s ?p ?o.} ORDER BY ?s OFFSET "+((pageNumber)*recordsOnPage) + " LIMIT "+recordsOnPage)
        //TODO
        val resultGraph = Payola.model.analysisResultStorageModel.getGraph( //TODO pri listovani tabulkou se pri offsetu 10000 odehraje nejaka chyba
            "CONSTRUCT { ?s ?p ?o } WHERE {?s ?p ?o.} ORDER BY ?s OFFSET "+((pageNumber)*recordsOnPage) + " LIMIT "+recordsOnPage,
            evaluationId)
        if(resultGraph.isEmpty) {
            None
        } else {
            Some(resultGraph)
        }
    }
}
