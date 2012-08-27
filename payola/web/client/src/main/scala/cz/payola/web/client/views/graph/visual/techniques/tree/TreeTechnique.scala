package cz.payola.web.client.views.graph.visual.techniques.tree

import cz.payola.web.client.views.graph.visual.animation.Animation
import cz.payola.web.client.views.graph.visual.techniques.BaseTechnique
import cz.payola.web.client.views.graph.visual.graph._
import cz.payola.web.client.views.algebra._
import cz.payola.web.client.views.graph.visual.graph.positioning.GraphPositionHelper
import scala.Some

/**
 * Visual plug-in technique that places the vertices into a tree structure.
 */
class TreeTechnique extends BaseTechnique("Tree Visualization")
{
    protected def getTechniquePerformer(component: Component,
        animate: Boolean): Animation[_] = {

        val graphCenterCorrector = new GraphPositionHelper(
            () => Vector2D(topLayer.size.x, 50), component.getCenter)
        val animation = new Animation(Animation.moveGraphByFunction,
            (graphCenterCorrector, component.vertexViews), None, redrawQuick, redraw, None)

        if (animate) {
            animation.addFollowingAnimation(
                new Animation(basicTreeStructure, component.vertexViews, None, redrawQuick, redraw, None))
            //basicTreeStructure(component.vertexViews, None, redrawQuick, redraw, None))
        } else {
            animation.addFollowingAnimation(
                new Animation(basicTreeStructure, component.vertexViews, None, redrawQuick, redraw, Some(0)))
            //basicTreeStructure(component.vertexViews, None, redrawQuick, redraw, Some(0)))
        }
        animation
    }
}
