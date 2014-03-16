package cz.payola.web.shared.transformators

import cz.payola.common.rdf.Graph
import s2js.compiler._
import cz.payola.web.shared.Payola
import cz.payola.common.PayolaException

@remote object RdfJsonTransformator extends GraphTransformator
{
    protected def performTransformation(input: Graph) = input

    @async def transform(evaluationId: String)(successCallback: Option[Graph] => Unit)(errorCallback: Throwable => Unit) {
        errorCallback(new PayolaException("Transform is not supported for RDF/JSON Transformator"))
    }

    @async
    def isAvailable(input: Graph)(successCallback: Boolean => Unit)(errorCallback: Throwable => Unit) {
        successCallback(isAvailable(input))
    }

    @async def getSampleGraph(evaluationId: String)
        (successCallback: Graph => Unit)(errorCallback: Throwable => Unit) {
        //TODO is it possible to get a sample graph and to perform a "visual plugin is able to process result graph" check?
        successCallback(Payola.model.analysisResultStorageModel.getEmptyGraph())
    }

    def isAvailable(input: Graph): Boolean = {
        //TODO when is available?
        true
    }

    @async
    def getCompleteGraph(evaluationId: String)
        (successCallback: Option[String] => Unit)(errorCallback: Throwable => Unit) {

        val serializedJenaGraph = Payola.model.analysisResultStorageModel.getGraphJena(evaluationId)
        successCallback(Some("#!json~*"+serializedJenaGraph))
    }

    @async def queryProperties(evaluationId: String, query: String)
        (successCallback: (Seq[String] => Unit))(failCallback: (Throwable => Unit)) {

        val result = Payola.model.analysisResultStorageModel.queryProperties(evaluationId, query)
        successCallback(result)
    }
}
