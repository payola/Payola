package cz.payola.web.client.views.visualPlugin.graph.algorithms.circlePath

import cz.payola.web.client.views.visualPlugin.drawingModels.ModelBase
import s2js.adapters.js.dom.Element
import cz.payola.common.rdf.Graph

class CirclePathModel(graph: Graph, element: Element) extends ModelBase(graph, element)
{
    def performModel() {
        basicTreeCircledStructure(graphView.vertexViews)
        moveGraphToUpperLeftCorner(graphView.vertexViews)
        flip(graphView.vertexViews)
    }
}
