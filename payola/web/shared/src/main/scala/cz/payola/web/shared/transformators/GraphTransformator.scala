package cz.payola.web.shared.transformators

import cz.payola.common.rdf.Graph
import s2js.compiler._

@remote trait GraphTransformator
{
    /**
     * Loads graph (or its part) from the cache and transforms it to the form that its (pair) view-plugin requires.
     */
    @async def transform(evaluationId: String)
        (successCallback: Option[Graph] => Unit)(errorCallback: Throwable => Unit)

    /**
     * Checks if the input graph can be transformed with this transformator
     */
    @async def isAvailable(input: Graph)
        (successCallback: Boolean => Unit)(errorCallback: Throwable => Unit)

    /**
     * Returns a small graph that represents the structure of the whole graph. This should be used to determine if
     * a visual plugin can process the result of this transformation.
     */
    @async def getSampleGraph(evaluationId: String)
        (successCallback: Graph => Unit)(errorCallback: Throwable => Unit)

    /**
     * Server-side-only visible function
     */
    def isAvailable(input: Graph): Boolean
}
