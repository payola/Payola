package cz.payola.web.client.views.plugins.visual.techniques.circle

import cz.payola.web.client.views.plugins.visual.techniques.BaseTechnique

class CircleTechnique extends BaseTechnique
{
    def performModel() {
        basicTreeCircledStructure(graphView.get.vertexViews)
        moveGraphToUpperLeftCorner(graphView.get.vertexViews)
        flip(graphView.get.vertexViews)
    }
}
