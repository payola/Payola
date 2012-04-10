package cz.payola.web.client.views.plugins.visual.techniques.tree

import cz.payola.web.client.views.plugins.visual.techniques.{Animation, BaseTechnique}

/**
  * Visual plug-in technique that places the vertices into a tree structure.
  */
class TreeTechnique extends BaseTechnique
{
    def performTechnique() {

        val moveToCorner2 = new Animation(Animation.moveGraphToUpperLeftCorner, graphView.get.vertexViews,
            None, redrawQuick, redraw, None)
        val flip = new Animation(Animation.flipGraph, graphView.get.vertexViews, Some(moveToCorner2),
            redrawQuick, redraw, None)
        val moveToCorner1 = new Animation(Animation.moveGraphToUpperLeftCorner, graphView.get.vertexViews,
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
