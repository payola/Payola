package cz.payola.web.client.views.graph.visual.techniques.tree

import cz.payola.web.client.views.graph.visual.animation.Animation
import cz.payola.web.client.views.graph.visual.techniques.BaseTechnique
import cz.payola.web.client.views.graph.visual.settings.components.visualsetup.VisualSetup
import cz.payola.web.client.views.graph.visual.graph._
import collection.mutable.ListBuffer
import cz.payola.web.client.views.algebra.Point2D

/**
 * Visual plug-in technique that places the vertices into a tree structure.
 */
class TreeTechnique(settings: VisualSetup) extends BaseTechnique(settings, "Tree Visualization")
{
    protected def getTechniquePerformer(component: Component,
        animate: Boolean): Animation[ListBuffer[(VertexView, Point2D)]] = {
        if (animate) {
            basicTreeStructure(component.vertexViews, None, redrawQuick, redraw, None)
        } else {
            basicTreeStructure(component.vertexViews, None, redrawQuick, redraw, Some(0))
        }
    }
}
