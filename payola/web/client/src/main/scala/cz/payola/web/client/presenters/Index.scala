package cz.payola.web.client.presenters

import s2js.adapters.js.browser._
import cz.payola.common.rdf.Graph
import cz.payola.web.client.views.plugins.Plugin
import cz.payola.web.client.views.plugins.visual.techniques.tree.TreeTechnique
import cz.payola.web.shared.GraphFetcher
import s2js.compiler.dependency
import s2js.runtime.shared.rpc.Exception
import cz.payola.web.client.views.plugins.visual.techniques.circle.CircleTechnique
import cz.payola.web.client.views.plugins.visual.techniques.gravity.GravityTechnique
import cz.payola.web.client.views.plugins.visual.techniques.minimalization.MinimalizationTechnique
import cz.payola.web.client.views.plugins.textual.techniques.table.TableTechnique
import cz.payola.web.client.views.plugins.visual.{SetupLoader, VisualPlugin}
import s2js.adapters.js.dom.{Anchor, Image, Element}
import s2js.adapters.goog
import goog.events.BrowserEvent
import cz.payola.web.client.views.plugins.textual.TextPlugin
import cz.payola.web.client.views.plugins.visual.components.visualsetup.VisualSetup

// TODO remove after classloading is done
@dependency("cz.payola.common.rdf.IdentifiedVertex")
@dependency("cz.payola.common.rdf.LiteralVertex")
@dependency("cz.payola.common.rdf.Graph")
@dependency("cz.payola.common.rdf.Edge")
class Index
{
    var graph: Option[Graph] = None

    val plugins = List[Plugin](
        new CircleTechnique(),
        new TableTechnique(),
        new TreeTechnique(),
        new MinimalizationTechnique(),
        new GravityTechnique()

        // ...
    )

    var currentPlugin: Option[Plugin] = None

    val visualSetup = new VisualSetup(plugins)
    val visualPluginSetup = new SetupLoader()

    visualSetup.pluginChanged += {
        event => changePlugin(event.target.currentPlugin)
            false
    }

    def init() {
        try {
            visualSetup.render(document.getElementById("settings"))
            //TODO show "asking the server for the data"
            graph = Option(GraphFetcher.getInitialGraph)
            //TODO show "preparing visualisation"
            changePlugin(visualSetup.currentPlugin)
            //TODO hide info
        } catch {
            case e: Exception => {
                window.alert("Failed to call RPC. " + e.message)
                graph = None
            }
            case e => {
                window.alert("Graph fetch exception. " + e.toString)
                graph = None
            }

            //TODO show error
        }
    }

    def preloadImages() {
        val imgLoaderElement = document.createElement[Image]("img")
        imgLoaderElement.src = visualPluginSetup.getValue(visualPluginSetup.VertexIconIdentified).getOrElse("")

        imgLoaderElement.src = visualPluginSetup.getValue(visualPluginSetup.VertexIconLiteral).getOrElse("")

        imgLoaderElement.src = visualPluginSetup.getValue(visualPluginSetup.VertexIconUnknown).getOrElse("")
    }

    def buildPluginSwitch() {

    }


    def updateSettings() {
        currentPlugin.get match {
            case i: VisualPlugin =>
                i.updateSettings(visualPluginSetup)
                currentPlugin.get.redraw()
        }
    }
    
    def resetSettings() {
        visualPluginSetup.reset()
        currentPlugin.get match {
            case i: VisualPlugin =>
                i.updateSettings(visualPluginSetup)
                currentPlugin.get.redraw()
        }
    }
    
    def changePluginByNumber(number: Int) {
        if(0 <= number && number < plugins.length) {
            changePlugin(plugins(number))
        }
    }
    def changePlugin(plugin: Plugin) {
        currentPlugin.foreach(_.clean())

        // Switch to the new one.
        currentPlugin = Some(plugin)
        plugin.init(document.getElementById("graph-plugin-draw-space"))
        plugin.update(graph.get)


        currentPlugin.get match {
            case i: VisualPlugin =>
                document.getElementById("settingsHideButton").removeAttribute("disabled")
                i.updateSettings(visualPluginSetup)
            case i: TextPlugin =>
                document.getElementById("settingsHideButton").setAttribute("disabled", "disabled")
                document.getElementById("visualPluginSettings").setAttribute("style", "visibility:hidden")
                i.redraw()
        }
    }
}
