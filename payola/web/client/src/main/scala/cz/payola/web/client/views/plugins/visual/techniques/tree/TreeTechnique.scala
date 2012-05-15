package cz.payola.web.client.views.plugins.visual.techniques.tree

import cz.payola.web.client.views.plugins.visual.animation.Animation
import cz.payola.web.client.views.plugins.visual.techniques.BaseTechnique
import cz.payola.web.client.views.plugins.visual.graph.VertexView
import cz.payola.web.client.views.plugins.visual.components.visualsetup.VisualSetup

/**
  * Visual plug-in technique that places the vertices into a tree structure.
  */
class TreeTechnique(settings: VisualSetup) extends BaseTechnique(settings)
{
    def performTechnique() {

        val moveToCorner2 = new Animation[VertexView](Animation.moveGraphToUpperLeftCorner, graphView.get.vertexViews,
            None, redrawQuick, redraw, None)
        val flip = new Animation[VertexView](Animation.flipGraph, graphView.get.vertexViews, Some(moveToCorner2),
            redrawQuick, redraw, None)
        val moveToCorner1 = new Animation[VertexView](Animation.moveGraphToUpperLeftCorner, graphView.get.vertexViews,
            Some(flip), redrawQuick, redraw, None)

        basicTreeStructure(graphView.get.vertexViews, true, Some(moveToCorner1))
    }

    override def clean() {
        super.clean()
    }

    def getName:String = {
        "tree visualisation"
    }
}
