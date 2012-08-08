package cz.payola.web.client.views.graph.visual.graph.positioning

import cz.payola.web.client.views.algebra._

/**
 * Helper for getting current position of the graph center.
 * Is used by flip animation to get not original but its at-the-time position.
 * @param graphCenterGetter function for getting the center of the graph
 */
class GraphCenterHelper(graphCenterGetter: () => Point2D) extends PositionHelper
{
    def getPositionCorrection(): Vector2D = {
        graphCenterGetter().toVector
    }
}
