package cz.payola.web.client.views.graph.visual.techniques.circle

import cz.payola.web.client.views.graph.visual.animation.Animation
import cz.payola.web.client.views.graph.visual.techniques.BaseTechnique
import cz.payola.web.client.views.graph.visual.graph._
import collection.mutable.ListBuffer
import cz.payola.web.client.views.algebra.Point2D
import cz.payola.web.client.models.PrefixApplier

/**
 * Visual plug-in technique that places the vertices into a circled tree structure.
 */
class CircleTechnique(prefixApplier: Option[PrefixApplier]) extends BaseTechnique("Circle Visualization", prefixApplier)
{
    protected def getTechniquePerformer(component: Component,
        animate: Boolean): Animation[_] = {
        if (animate) {
            basicTreeCircledStructure(component.vertexViewElements, None, redrawQuick, redraw, None)
        } else {
            basicTreeCircledStructure(component.vertexViewElements, None, redrawQuick, redraw, Some(0))
        }
    }
}
