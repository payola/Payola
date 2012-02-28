package cz.payola.web.client.presenters

import s2js.adapters.js.browser._
import cz.payola.common.rdf.Graph
import cz.payola.web.client.views.Plugin
import cz.payola.web.client.views.visualPlugin.drawingModels.treePath.TreePathModel
import cz.payola.web.shared.GraphFetcher
import s2js.compiler.NativeJsDependency
import s2js.runtime.s2js.RPCException

// TODO remove after classloading is done
@NativeJsDependency("cz.payola.common.rdf.IdentifiedVertex")
@NativeJsDependency("cz.payola.common.rdf.generic.Graph")
@NativeJsDependency("cz.payola.common.rdf.generic.Edge")
class Index
{
    var graphModel = initGraph()

    // TODO rename canvas-holder to something else.
    val pluginContainer = document.getElementById("canvas-holder")

    val plugins = List[Plugin](
        new TreePathModel()
        // ...
    )
    
    var currentPlugin: Option[Plugin] = None

    def init() {
        changePlugin(plugins.head)
    }
    
    def changePlugin(plugin: Plugin) {
        currentPlugin.foreach(_.clean())

        // Switch to the new one.
        currentPlugin = Some(plugin)
        plugin.init(graphModel, pluginContainer)
        plugin.redraw()
    }

    def initGraph(): Graph = {
        try {
            GraphFetcher.getInitialGraph()
        } catch {
            case e: RPCException => {
                window.alert("Failed to call RPC. " + e.message)
                null
            }
            case e => {
                window.alert("Graph fetch exception. " + e.toString)
                null
            }
        }
    }
}
