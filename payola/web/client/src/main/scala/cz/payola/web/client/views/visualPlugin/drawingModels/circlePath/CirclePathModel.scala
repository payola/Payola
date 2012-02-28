package cz.payola.web.client.views.visualPlugin.graph.algorithms.circlePath

import cz.payola.web.client.views.visualPlugin.drawingModels.ModelBase
import s2js.adapters.js.dom.Element
import cz.payola.common.rdf.Graph

class CirclePathModel extends ModelBase
{
    def performModel() {
        basicTreeCircledStructure(graphView.get.vertexViews)
        moveGraphToUpperLeftCorner(graphView.get.vertexViews)
        flip(graphView.get.vertexViews)
    }
}
