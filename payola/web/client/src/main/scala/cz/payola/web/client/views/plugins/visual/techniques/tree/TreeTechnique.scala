package cz.payola.web.client.views.plugins.visual.techniques.tree

import cz.payola.web.client.views.plugins.visual.techniques.BaseTechnique
import cz.payola.common.rdf.Graph
import s2js.adapters.js.dom.Element

class TreeTechnique extends BaseTechnique
{
    override def init(graph: Graph, container: Element) {
        super.init(graph, container)
        performTechnique()
    }

    def performTechnique() {
        basicTreeStructure(graphView.get.vertexViews)
        moveGraphToUpperLeftCorner(graphView.get.vertexViews)
    }
}
