package cz.payola.web.client.views.graph.visual.techniques.tree

import cz.payola.web.client.views.graph.visual.animation.Animation
import cz.payola.web.client.views.graph.visual.techniques.BaseTechnique
import cz.payola.web.client.views.graph.visual.graph._
import cz.payola.web.client.views.algebra._
import cz.payola.web.client.views.graph.visual.graph.positioning.GraphPositionHelper
import scala.Some
import cz.payola.web.client.models.PrefixApplier

/**
 * Visual plug-in technique that places the vertices into a tree structure.
 */
class TreeTechnique(prefixApplier: Option[PrefixApplier] = None) extends BaseTechnique("Tree Visualization", prefixApplier)
{
    protected def getTechniquePerformer(component: Component,
        animate: Boolean): Animation[_] = {

        /*val graphCenterCorrector = new GraphPositionHelper(
            () => Vector2D(topLayer.size.x, 50), component.getCenter)
        val animation = new Animation(Animation.moveGraphByFunction,
            (graphCenterCorrector, component.vertexViewElements), None, redrawQuick, redraw, None)*/

        val animation = if (animate) {
            //animation.addFollowingAnimation(
                new Animation(basicTreeStructure, component.vertexViewElements, None, redrawQuick, redraw, None)//)
            //basicTreeStructure(component.vertexViewElements, None, redrawQuick, redraw, None))
        } else {
            //animation.addFollowingAnimation(
                new Animation(basicTreeStructure, component.vertexViewElements, None, redrawQuick, redraw, Some(0))//)
            //basicTreeStructure(component.vertexViewElements, None, redrawQuick, redraw, Some(0)))
        }
        animation
    }
}
