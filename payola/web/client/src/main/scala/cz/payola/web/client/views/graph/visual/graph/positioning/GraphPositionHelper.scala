package cz.payola.web.client.views.graph.visual.graph.positioning

import cz.payola.web.client.views.algebra._

/**
 * Helper for getting current size of the graph.
 * Is used by move graph by function to get not original but its at-the-time size for displaying multiple graph
 * components.
 * @param drawingSpaceSizeGetter function for getting size of the graph (component)
 * @param graphCenterGetter function for getting center of the graph (component)
 */
class GraphPositionHelper(drawingSpaceSizeGetter: () => Vector2D, graphCenterGetter: () => Point2D)
    extends PositionHelper
{
    def getPositionCorrection(): Vector2D = {
        val drawingSpaceSize = drawingSpaceSizeGetter()
        val drawingSpaceCenter = Point2D(drawingSpaceSize.x / 2, drawingSpaceSize.y / 2)

        val graphCenter = graphCenterGetter()
        graphCenter.createVector(drawingSpaceCenter)
    }
}
