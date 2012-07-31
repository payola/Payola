package cz.payola.web.client.views.graph.visual.graph.positioning

import cz.payola.web.client.views.algebra.Vector2D

/**
 * Positioning tool for animations. By this animations can reach current sizes and coordinates of vertexViews and
 * other elements of graphView.
 */
trait PositionHelper
{
    /**
     * Getter of the required value in an animation.
     * @return
     */
    def getPositionCorrection(): Vector2D
}
