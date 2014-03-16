package cz.payola.web.shared.transformators

import cz.payola.common.rdf.Graph
import s2js.compiler._
import cz.payola.web.shared.Payola

@remote object IdentityTransformator extends GraphTransformator
{
    protected def performTransformation(input: Graph) = input

    @async def transform(evaluationId: String)
        (successCallback: Option[Graph] => Unit)(errorCallback: Throwable => Unit) {
        val identityGraph = Payola.model.analysisResultStorageModel.getGraph(evaluationId)
        successCallback(if(identityGraph.isEmpty) { None } else { Some(identityGraph) })
    }

    @async
    def isAvailable(input: Graph)(successCallback: Boolean => Unit)(errorCallback: Throwable => Unit) {
        successCallback(isAvailable(input))
    }

    @async def getSampleGraph(evaluationId: String)
        (successCallback: Graph => Unit)(errorCallback: Throwable => Unit) {
        successCallback(Payola.model.analysisResultStorageModel.getEmptyGraph())
    }

    def isAvailable(input: Graph): Boolean = {
        true //identity is always available
    }
}