package cz.payola.web.client.views.plugins.visual.graph

import cz.payola.web.client.views.plugins.visual.{Point, Vector}

/**
  * Class for getting specified positions for InformationViews based on their parent View.
  */
object LocationDescriptor {
    /**
      * Correction for drawing of text.
      */
    private val informationPositionCorrection = Vector(0, 4)

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
}