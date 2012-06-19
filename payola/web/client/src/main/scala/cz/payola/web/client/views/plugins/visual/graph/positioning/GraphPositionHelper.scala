package cz.payola.web.client.views.plugins.visual.graph.positioning

import cz.payola.web.client.views.plugins.visual._
import s2js.adapters.js.browser.window

class GraphPositionHelper(drawingSpaceSizeGetter: () => Vector, graphCenterGetter: () => Point) extends PositionHelper {

    def getPositionCorrection(): Vector = {
        val drawingSpaceSize = drawingSpaceSizeGetter()
        val drawingSpaceCenter = Point(drawingSpaceSize.x / 2, drawingSpaceSize.y / 2)

        val graphCenter = graphCenterGetter()
        //val vec = graphCenter.createVector(drawingSpaceCenter)
        //window.alert("canvas center: "+drawingSpaceCenter.toString+" graph center: "+graphCenter.toString+"; result: "+vec.toString)
        graphCenter.createVector(drawingSpaceCenter)
    }
}
