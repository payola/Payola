package cz.payola.web.client.views.graph.visual.graph.positioning

import s2js.adapters.js.browser.window
import cz.payola.web.client.views.graph.visual.graph.Component
import cz.payola.web.client.views.algebra._
import cz.payola.web.client.views.bootstrap.modals.AlertModal

/**
  * // TODO why from 1?
  * IMPORTANT, the first component must have number 1 (not 0)!!!!!
  * @param componentNumber COUNT FROM 1!!!!! not form 0
  */
class ComponentPositionHelper(val componentNumber: Int, val componentsCount: Int, val prevComp: Option[Component])
    extends PositionHelper
{
    def getPositionCorrection(): Vector2D = {
        val componentSpacing = 50.0

        if (componentNumber == 0 || componentsCount < 0) {
            // TODO get rid of this. Write it in a way, that this can't happen.
            AlertModal.display("Error in component position helper", "")
        }

        val bottomRight = if (prevComp.isDefined) {
            prevComp.get.getBottomRight()
        } else {
            Point2D(0, 0)
        }
        val topRight = if (prevComp.isDefined) {
            prevComp.get.getTopRight()
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
        val numberOfCurrentLine = math.ceil(componentNumber / componentsInRowCount)
        val positionInRow = componentNumber - ((numberOfCurrentLine - 1) * componentsInRowCount)

        if (positionInRow == 1) {
            //next row
            Vector2D(componentSpacing, previousComponentBottomRight.y + componentSpacing)
        } else {
            //continue in the current row
            Vector2D(previousComponentBottomRight.x + componentSpacing, topRight.y)
        }
    }
}
