package cz.payola.web.shared.managers

import cz.payola.web.shared.transformators._
import cz.payola.common.rdf.Graph
import s2js.compiler._

@remote object TransformationManager
{
    private val transformators = List[GraphTransformator](
        TripleTableTransformator,
        IdentityTransformator,
        VisualTransformator,
        RdfJsonTransformator
    )

    /**
     * Retuns list of transformators classes names, that are able to transformate the input graph
     */
    @async def getAvailableTransformations(input: Graph)
        (successCallback: List[String] => Unit)(errorCallback: Throwable => Unit) {
        successCallback(getAvailableTransformations(input))
    }

    /**
     * Retuns list of transformators classes names, that are able to transformate the input graph
     */
    def getAvailableTransformations(input: Graph): List[String] = {
        transformators.filter(_.isAvailable(input)).map(_.getClass.getName.split("\\$").last)
    }

    /**
     * Retuns list of all transformators classes names
     */
    def allTransformations = transformators.map(_.getClass.getName.split("\\$").last)
}
