package cz.payola.web.client.views.graph.visual.graph.positioning

import cz.payola.web.client.views.graph.visual.graph.Component
import cz.payola.web.client.views.algebra._
import  s2js.adapters.browser._

/**
 * Helper for getting vector correcting the position of a component in the context of other components in a whole graph.
 * Is used during components placement of the graph by move graph by function animation function.
 * @param drawingSpaceSizeGetter for getting size of the drawing space
 * @param componentCenterGetter for getting the center of the component that has to be placed
 * @param previousComponent component that was drawn previously
 */
class ComponentPositionHelper(drawingSpaceSizeGetter: () => Vector2D, componentCenterGetter: () => Point2D,
    val previousComponent: Option[Component]) extends PositionHelper {

    def getPositionCorrection(): Vector2D = {

        val componentSpacing = 100.0
        val componentFirstIndent = 50.0

        val drawingSpaceSize = drawingSpaceSizeGetter()
        val drawingSpaceCenter = Point2D(drawingSpaceSize.x / 2, drawingSpaceSize.y / 2)

        val componentCenter = componentCenterGetter()

        if(previousComponent.isDefined) {
            componentCenter.createVector(Point2D(drawingSpaceCenter.x,
                componentSpacing + previousComponent.get.getBottomLeft().y))
        } else {
            componentCenter.createVector(Point2D(drawingSpaceCenter.x, componentFirstIndent))
        }
    }
}
