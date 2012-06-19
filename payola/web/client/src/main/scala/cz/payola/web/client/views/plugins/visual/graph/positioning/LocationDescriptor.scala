package cz.payola.web.client.views.plugins.visual.graph.positioning

import cz.payola.web.client.views.plugins.visual.{Point, Vector}
import s2js.adapters.js.browser.window
import scala.collection.mutable.ListBuffer

/**
  * Class for getting specified positions for InformationViews based on their parent View.
  */
object LocationDescriptor
{
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
        val x = (originPosition.x + destinationPosition.x) / 2
        val y = (originPosition.y + destinationPosition.y) / 2
        Point(x, y) + informationPositionCorrection
    }
}