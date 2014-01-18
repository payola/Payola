package cz.payola.web.shared.transformators

import cz.payola.common.rdf.Graph
import s2js.compiler._
import cz.payola.web.shared.Payola
import cz.payola.common.PayolaException

@remote object GoogleMapTransformator extends GraphTransformator
{
    protected def performTransformation(input: Graph) = input

    @async def transform(evaluationId: String)(successCallback: Graph => Unit)(errorCallback: Throwable => Unit) {
        errorCallback(new PayolaException("Transform is not supported for Google Map Transformator"))
    }

    @async
    def isAvailable(input: Graph)(successCallback: Boolean => Unit)(errorCallback: Throwable => Unit) {
        successCallback(isAvailable(input))
    }

    @async def getSmapleGraph(evaluationId: String)
        (successCallback: Graph => Unit)(errorCallback: Throwable => Unit) {
        //TODO is it possible to get a sample graph and to perform a "visual plugin is able to process result graph" check?
        successCallback(Payola.model.analysisResultStorageModel.getEmptyGraph())
    }

    def isAvailable(input: Graph): Boolean = {
        //TODO when is available?
        true
    }

    @async
    def getCompleteGraph(evaluationId: String, format: String)
        (successCallback: String => Unit)(errorCallback: Throwable => Unit) {

        val string = Payola.model.analysisResultStorageModel.getGraphJena(evaluationId, format)
        successCallback("#!json~*"+string)
    }
}
