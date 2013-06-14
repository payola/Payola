package cz.payola.web.client.views.graph.visual.techniques.circle

import cz.payola.web.client.views.graph.visual.animation.Animation
import cz.payola.web.client.views.graph.visual.techniques.BaseTechnique
import cz.payola.web.client.views.graph.visual.graph._
import collection.mutable.ListBuffer
import cz.payola.web.client.views.algebra.Point2D

/**
 * Visual plug-in technique that places the vertices into a circled tree structure.
 */
class CircleTechnique extends BaseTechnique("Circle Visualization")
{
    protected def getTechniquePerformer(component: Component,
        animate: Boolean): Animation[_] = {
        if (animate) {
            basicTreeCircledStructure(component.vertexViews, None, redrawQuick, redraw, None)
        } else {
            basicTreeCircledStructure(component.vertexViews, None, redrawQuick, redraw, Some(0))
        }
    }
}
