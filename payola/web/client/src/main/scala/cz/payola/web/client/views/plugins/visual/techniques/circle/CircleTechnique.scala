package cz.payola.web.client.views.plugins.visual.techniques.circle

import cz.payola.web.client.views.plugins.visual.techniques.{Animation, BaseTechnique}

/**
  * Visual plug-in technique that places the vertices into a circled tree structure.
  */
class CircleTechnique extends BaseTechnique
{
    def performTechnique() {
        basicTreeCircledStructure(graphView.get.vertexViews)

        val moveToCorner2 = new Animation(Animation.moveGraphToUpperLeftCorner, graphView.get.vertexViews,
            None, redrawQuick, redraw)
        val flip = new Animation(Animation.flipGraph, graphView.get.vertexViews, Some(moveToCorner2),
            redrawQuick, redraw)
        val moveToCorner1 = new Animation(Animation.moveGraphToUpperLeftCorner, graphView.get.vertexViews,
            Some(flip), redrawQuick, redraw)

        moveToCorner1.run()
    }

    override def clean() {
        super.clean()
    }

    def getName:String = {
        "circle visualisation"
    }
}
