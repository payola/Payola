package cz.payola.web.client.views.plugins.visual.techniques.tree

import cz.payola.web.client.views.plugins.visual.techniques.BaseTechnique

class TreeTechnique extends BaseTechnique
{
    def performModel() {
        basicTreeStructure(graphView.get.vertexViews)
        moveGraphToUpperLeftCorner(graphView.get.vertexViews)
    }
}
