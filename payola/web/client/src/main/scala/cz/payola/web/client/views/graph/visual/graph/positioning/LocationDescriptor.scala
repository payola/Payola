package cz.payola.web.client.views.graph.visual.graph.positioning

import cz.payola.web.client.views.algebra._

/**
 * Class for getting specified positions for InformationViews based on their parent View.
 */
object LocationDescriptor
{
    /**
     * Correction for drawing of text.
     */
    private val informationPositionCorrection = Vector2D(0, 4)

    /**
     * Position getter for InformationViews owned by VertexViews.
     * @param position of the owner VertexView object
     * @return corrected position for the owned InformationView object
     */
    def getVertexInformationPosition(position: Point2D): Point2D = {
        position + informationPositionCorrection
    }

    /**
     * Position getter for InformationViews owned by EdgeViews.
     * @param originPosition position of the owners origin VertexView position
     * @param destinationPosition position of the owners destination VertexView position
     * @return corrected position for the owned InformationView object
     */
    def getEdgeInformationPosition(originPosition: Point2D, destinationPosition: Point2D): Point2D = {
        val x = (originPosition.x + destinationPosition.x) / 2
        val y = (originPosition.y + destinationPosition.y) / 2
        Point2D(x, y) + informationPositionCorrection
    }
}
