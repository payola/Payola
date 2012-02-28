package cz.payola.web.client.presenters

import s2js.adapters.js.browser._
import cz.payola.common.rdf.Graph
import cz.payola.web.client.views.visualPlugin.drawingModels.treePath.TreePathModel
import cz.payola.web.shared.GraphFetcher
import s2js.compiler.NativeJsDependency

class Index
{

    //TODO (remove after #9 is done)
    @NativeJsDependency("cz.payola.common.rdf.IdentifiedVertex")
    val ___a = null
    @NativeJsDependency("cz.payola.common.rdf.generic.Graph")
    val ___b = null
    @NativeJsDependency("cz.payola.common.rdf.generic.Edge")
    val ___c = null

    val graphModel = initGraph()

    val treePathModel = new TreePathModel(graphModel, document.getElementById("canvas-holder"))

    def init() {
        treePathModel.init()
        treePathModel.performModel()
        treePathModel.redraw()
    }

    def initGraph(): Graph = {
        GraphFetcher.getInitialGraph
    }
}
