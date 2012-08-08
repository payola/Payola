package cz.payola.web.client.views.graph.visual.graph.positioning

import cz.payola.web.client.views.graph.visual.graph.Component
import cz.payola.web.client.views.algebra._

/**
 * Helper for getting vector correcting the position of a component in the context of other components in a whole graph.
 * Is used during components placement of the graph by move graph by function animation function.
 * @param componentsCount count of components of the whole graph
 * @param previousComponent component that was drawn previously
 */
class ComponentPositionHelper(val componentsCount: Int, val previousComponent: Option[Component])
    extends PositionHelper
{
    def getPositionCorrection(): Vector2D = {
        val componentSpacing = 50.0
        val componentNumber = if (previousComponent.get.componentNumber >= 0) {
            previousComponent.get.componentNumber + 1
            //^it is expected, that components are numbered from 0
        } else {
            1
            //in case the component number is wrong, this wont cause the calculation to crash
        }


        val bottomRight = if (previousComponent.isDefined) {
            previousComponent.get.getBottomRight()
        } else {
            Point2D(0, 0)
        }
        val topRight = if (previousComponent.isDefined) {
            previousComponent.get.getTopRight()
        } else {
            Point2D(0, 0)
        }
        val previousComponentBottomRight = bottomRight.toVector + Vector2D(50, 100)


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
        }
    }
}
