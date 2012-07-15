package cz.payola.web.client.views.graph.visual.graph.positioning

import cz.payola.web.client.views.algebra._

class GraphPositionHelper(drawingSpaceSizeGetter: () => Vector2D, graphCenterGetter: () => Point2D)
    extends PositionHelper
{
    def getPositionCorrection(): Vector2D = {
        val drawingSpaceSize = drawingSpaceSizeGetter()
        val drawingSpaceCenter = Point2D(drawingSpaceSize.x / 2, drawingSpaceSize.y / 2)

        val graphCenter = graphCenterGetter()
        //val vec = graphCenter.createVector(drawingSpaceCenter)
        //window.alert("canvas center: "+drawingSpaceCenter.toString+" graph center: "+graphCenter.toString+"; result:
        // "+vec.toString)
        graphCenter.createVector(drawingSpaceCenter)
    }
}
