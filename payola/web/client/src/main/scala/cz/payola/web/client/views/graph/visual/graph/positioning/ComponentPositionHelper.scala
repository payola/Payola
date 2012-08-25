package cz.payola.web.client.views.graph.visual.graph.positioning

import cz.payola.web.client.views.graph.visual.graph.Component
import cz.payola.web.client.views.algebra._

/**
 * Helper for getting vector correcting the position of a component in the context of other components in a whole graph.
 * Is used during components placement of the graph by move graph by function animation function.
 * @param componentsCount count of components of the whole graph
 * @param previousComponent component that was drawn previously
 */
class ComponentPositionHelper(drawingSpaceSizeGetter: () => Vector2D, componentCenterGetter: () => Point2D,
    val previousComponent: Option[Component]) extends PositionHelper {

    def getPositionCorrection(): Vector2D = {
        val componentSpacing = 50.0


        val drawingSpaceSize = drawingSpaceSizeGetter()
        val drawingSpaceCenter = Point2D(drawingSpaceSize.x / 2, drawingSpaceSize.y / 2)

        val componentCenter = componentCenterGetter()
        val spacing = if(previousComponent.isDefined) {
            Vector2D(0, componentSpacing)
        } else {
            Vector2D.Zero
        }
        componentCenter.createVector(drawingSpaceCenter) + spacing


        /*val componentNumber = previousComponent.map(_.componentNumber).filter(_ >= 0).getOrElse(0) + 1


        val prevComponentCenter = if(previousComponent.isDefined) {
            previousComponent.get.getCenter()
        } else {
            centerGetter()
        }


        val componentsInRowCount =
            if (componentsCount <= 4) {
                2.0
            } else {
                math.ceil(math.sqrt(componentsCount))
            }

        //lets enjoy some little math :-)
        val numberOfCurrentLine = math.ceil((componentNumber) / componentsInRowCount)
        val positionInRow = (componentNumber) - ((numberOfCurrentLine - 1) * componentsInRowCount)

        if (positionInRow == 1) {
            //next row
            Vector2D(componentSpacing, previousComponentBottomRight.y + componentSpacing)
        } else {
            //continue in the current row
            Vector2D(previousComponentBottomRight.x + componentSpacing, topRight.y)
        }*/
    }
}
