package cz.payola.web.client.views.visualPlugin.drawingModels.treePath

import cz.payola.web.client.views.visualPlugin.drawingModels.ModelBase
import s2js.adapters.js.dom.Element
import cz.payola.common.rdf.Graph

class TreePathModel(graph: Graph, element: Element) extends ModelBase(graph, element)
{
    def performModel() {
        basicTreeStructure(graphView.vertexViews)
        moveGraphToUpperLeftCorner(graphView.vertexViews)
    }
}
