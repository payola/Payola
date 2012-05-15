package cz.payola.web.client.views.plugins.visual.techniques.circle

import cz.payola.web.client.views.plugins.visual.animation.Animation
import cz.payola.web.client.views.plugins.visual.techniques.BaseTechnique
import cz.payola.web.client.views.plugins.visual.graph.VertexView
import collection.mutable.ListBuffer
import cz.payola.web.client.views.plugins.visual.components.visualsetup.VisualSetup

/**
  * Visual plug-in technique that places the vertices into a circled tree structure.
  */
class CircleTechnique(settings: VisualSetup) extends BaseTechnique(settings)
{
    def performTechnique() {

        val moveToCorner2 = new Animation[VertexView](Animation.moveGraphToUpperLeftCorner, graphView.get.vertexViews,
            None, redrawQuick, redraw, None)
        val flip = new Animation[VertexView](Animation.flipGraph, graphView.get.vertexViews, Some(moveToCorner2),
            redrawQuick, redraw, None)
        val moveToCorner1 = new Animation[VertexView](Animation.moveGraphToUpperLeftCorner, graphView.get.vertexViews,
            Some(flip), redrawQuick, redraw, None)

        basicTreeCircledStructure(graphView.get.vertexViews, true, Some(moveToCorner1))
    }

    override def clean() {
        super.clean()
    }

    def getName:String = {
        "circle visualisation"
    }
}
