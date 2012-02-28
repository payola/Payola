package cz.payola.web.client.presenters

import s2js.adapters.js.browser._
import cz.payola.common.rdf.{ListItem, Graph}
import cz.payola.web.client.model.graph.{SimpleGraph, SimpleEdge, SimpleIdentifiedVertex}
import cz.payola.web.client.views.visualPlugin.drawingModels.treePath.TreePathModel
import cz.payola.web.client.RpcTestClient

class Index
{
    val graphModel = initGraph()

    val treePathModel = new TreePathModel(graphModel, document.getElementById("canvas-holder"))

    def init() {
        treePathModel.init()
        treePathModel.performModel()
        treePathModel.redraw()
    }

    def initGraph(): Graph = {
        // TODO retrieve the graph from the server using following call when RPC and server side is done.
        // GraphFetcher.getInitialGraph
        RpcTestClient.getGraph
    }
}
