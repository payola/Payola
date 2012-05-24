package cz.payola.web.client.views.plugins.visual.graph

import cz.payola.web.client.views.plugins.visual.{Point, Vector}
import s2js.adapters.js.browser.window

/**
  * Class for getting specified positions for InformationViews based on their parent View.
  */
object LocationDescriptor {
    /**
      * Correction for drawing of text.
      */
    private val informationPositionCorrection = Vector(0, 4)

    /*using these variables kills the script, why the F*CK!? */
    //private val componentPositionCorrection = Vector(50, 50)
    //private val componentSpacing = 50

    /**
      * Position getter for InformationViews owned by VertexViews.
      * @param position of the owner VertexView object
      * @return corrected position for the owned InformationView object
      */
    def getVertexInformationPosition(position: Point): Point = {
        position + informationPositionCorrection
    }

    /**
      * Position getter for InformationViews owned by EdgeViews.
      * @param originPosition position of the owners origin VertexView position
      * @param destinationPosition position of the owners destination VertexView position
      * @return corrected position for the owned InformationView object
      */
    def getEdgeInformationPosition(originPosition: Point, destinationPosition: Point): Point = {
        val x = (originPosition.x + destinationPosition.x)/2
        val y = (originPosition.y + destinationPosition.y)/2
        Point(x, y) + informationPositionCorrection
    }

    /**
     * IMPORTANT, first component must have number 1 (not 0)!!!!!
     * @param componentNumber COUNT FROM 1!!!!! not form 0
     */
    class ComponentPositionHelper(val componentNumber: Int, val componentsCount: Int, val prevComp: Option[Component]) {

        def getComponentPosition(): Point = {

            val componentSpacing = 50.0

            if(componentNumber == 0 || componentsCount < 0) {
                window.alert("Error in component position helper")
                Point(0, 0)
            }

            val bottomRight = if(prevComp.isDefined) { prevComp.get.getBottomRight() } else { Point(0, 0) }
            val topRight = if(prevComp.isDefined) { prevComp.get.getTopRight() } else { Point(0, 0) }
            val previousComponentBottomRight = bottomRight.toVector + Vector(50, 100)


            val componentsInRowCount =
                if(componentsCount <= 4) {
                    2.0
                } else {
                    math.ceil(math.sqrt(componentsCount))
                }

            //lets enjoy some little math :-)
            val numberOfCurrentLine = math.ceil(componentNumber / componentsInRowCount)
            val positionInRow = componentNumber - ((numberOfCurrentLine - 1) * componentsInRowCount)

            if(positionInRow == 1) { //next row
                Point(componentSpacing, previousComponentBottomRight.y + componentSpacing)
            } else { //continue in the current row
                Point(previousComponentBottomRight.x + componentSpacing, topRight.y)
            }
        }
    }

}