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
    var graph: Option[Graph] = None

    val plugins = List[Plugin](
        new TreePathModel()
        // ...
    )

    var currentPlugin: Option[Plugin] = None

    def init() {
        try {
            graph = Option(GraphFetcher.getInitialGraph)
        } catch {
            case e: RPCException => {
                window.alert("Failed to call RPC. " + e.message)
                graph = None
            }
            case e => {
                window.alert("Graph fetch exception. " + e.toString)
                graph = None
            }
        }

        changePlugin(plugins.head)
    }

    def changePlugin(plugin: Plugin) {
        currentPlugin.foreach(_.clean())

        // Switch to the new one.
        currentPlugin = Some(plugin)
        // TODO rename canvas-holder to something else.
        // TODO don't init the plugin with the graph. Rather init it blank (so it may show something like
        // "loading graph") and when the graph is successfully fetched, call update on the plugin.
        plugin.init(graph.get, document.getElementById("canvas-holder"))
        plugin.redraw()
    }
}
