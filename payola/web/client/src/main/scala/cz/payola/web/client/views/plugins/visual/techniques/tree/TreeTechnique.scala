package cz.payola.web.client.views.plugins.visual.techniques.tree

import cz.payola.web.client.views.plugins.visual.techniques.BaseTechnique

/**
  * Visual plug-in technique that places the vertices into a tree structure.
  */
class TreeTechnique extends BaseTechnique
{
    def performTechnique() {
        basicTreeStructure(graphView.get.vertexViews)
        moveGraphToUpperLeftCorner(graphView.get.vertexViews)
    }
}
