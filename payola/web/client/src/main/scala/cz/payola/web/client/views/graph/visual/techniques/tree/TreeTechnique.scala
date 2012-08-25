package cz.payola.web.client.views.graph.visual.techniques.tree

import cz.payola.web.client.views.graph.visual.animation.Animation
import cz.payola.web.client.views.graph.visual.techniques.BaseTechnique
import cz.payola.web.client.views.graph.visual.graph._
import collection.mutable.ListBuffer
import cz.payola.web.client.views.algebra.Point2D
import cz.payola.web.client.views.graph.visual.graph.positioning.GraphPositionHelper

/**
 * Visual plug-in technique that places the vertices into a tree structure.
 */
class TreeTechnique extends BaseTechnique("Tree Visualization")
{
    protected def getTechniquePerformer(component: Component,
        animate: Boolean): Animation[_] = {

        val graphCenterCorrector = new GraphPositionHelper(() => topLayer.size, component.getCenter)
        val animation = new Animation(Animation.moveGraphByFunction,
            (graphCenterCorrector, component.vertexViews), None, redrawQuick, redraw, None)

        if (animate) {
            animation.addFollowingAnimation(
                basicTreeStructure(component.vertexViews, None, redrawQuick, redraw, None))
        } else {
            animation.addFollowingAnimation(
                basicTreeStructure(component.vertexViews, None, redrawQuick, redraw, Some(0)))
        }
        animation
    }
}
