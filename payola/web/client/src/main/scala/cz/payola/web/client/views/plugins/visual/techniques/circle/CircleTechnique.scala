package cz.payola.web.client.views.plugins.visual.techniques.circle

import cz.payola.web.client.views.plugins.visual.techniques.BaseTechnique

/**
  * Visual plug-in technique that places the vertices into a circled tree structure.
  */
class CircleTechnique extends BaseTechnique
{
    def performTechnique() {
        basicTreeCircledStructure(graphView.get.vertexViews)
        moveGraphToUpperLeftCorner(graphView.get.vertexViews)
        flip(graphView.get.vertexViews)
    }

    override def clean() {
        super.clean()
    }

    def getName:String = {
        "circle visualisation"
    }
}
