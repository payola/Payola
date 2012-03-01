package cz.payola.web.client.views.plugins.visual.techniques.circle

import cz.payola.web.client.views.plugins.visual.techniques.BaseTechnique
import cz.payola.common.rdf.Graph
import s2js.adapters.js.dom.Element

class CircleTechnique extends BaseTechnique
{
    override def init(graph: Graph, container: Element) {
        super.init(graph, container)
        performTechnique()
    }

    def performTechnique() {
        basicTreeCircledStructure(graphView.get.vertexViews)
        moveGraphToUpperLeftCorner(graphView.get.vertexViews)
        flip(graphView.get.vertexViews)
    }
}
