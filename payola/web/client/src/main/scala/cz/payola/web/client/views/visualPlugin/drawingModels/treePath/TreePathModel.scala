package cz.payola.web.client.views.visualPlugin.drawingModels.treePath

import cz.payola.web.client.views.visualPlugin.drawingModels.ModelBase
import s2js.adapters.js.dom.Element
import cz.payola.common.rdf.Graph

class TreePathModel extends ModelBase
{
    def performModel() {
        basicTreeStructure(graphView.get.vertexViews)
        moveGraphToUpperLeftCorner(graphView.get.vertexViews)
    }
}
