package cz.payola.web.client.views.graph.visual.graph.positioning

import cz.payola.web.client.views.algebra._

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
