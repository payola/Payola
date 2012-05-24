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
import cz.payola.web.client.views.plugins.textual.TextPlugin
import cz.payola.web.client.views.plugins.visual.settings.components.visualsetup.VisualSetup
import cz.payola.web.client.views.plugins.visual._
import cz.payola.web.client.mvvm_api.element.{Anchor, Li, Text}
import settings.{VertexSettingsModel, TextSettingsModel, EdgeSettingsModel}

// TODO remove after classloading is done
@dependency("cz.payola.common.rdf.IdentifiedVertex")
@dependency("cz.payola.common.rdf.LiteralVertex")
@dependency("cz.payola.common.rdf.Graph")
@dependency("cz.payola.common.rdf.Edge")
class Index(val elementToDrawIn: String = "graph-plugin-draw-space")
{
    var graph: Option[Graph] = None

    val vertexSettings = new VertexSettingsModel
    val edgesSettings = new EdgeSettingsModel
    val textSettings = new TextSettingsModel

    val visualSetup = new VisualSetup(vertexSettings, edgesSettings, textSettings)
    visualSetup.settingsChanged += {
        evt =>
            currentPlugin.get.redraw()
            false
    }

    val plugins = List[Plugin](
        new CircleTechnique(visualSetup),
        new TableTechnique(visualSetup),
        new TreeTechnique(visualSetup),
        new MinimalizationTechnique(visualSetup),
        new GravityTechnique(visualSetup)
    )

    var currentPlugin: Option[Plugin] = None

    plugins.foreach{ plugin =>

        val pluginBtn = new Anchor(List(new Text(plugin.getName)), "#")
        new Li(List(pluginBtn)).render(document.getElementById("settings"))

        pluginBtn.clicked += {
            event =>
                val newPlugin = plugins.find(_.getName == plugin.getName)
                if(newPlugin.isDefined) {
                    changePlugin(newPlugin.get)
                }
                false
        }
    }

    def init() {
        visualSetup.render(document.getElementById("settings"))
        changePlugin(plugins.head)
    }

    def updateSettings() {
        currentPlugin.get match {
            case i: VisualPlugin =>
                currentPlugin.get.redraw()
        }
    }
    
    def resetSettings() {
        currentPlugin.get match {
            case i: VisualPlugin =>
                currentPlugin.get.redraw()
        }
    }

    def changePlugin(plugin: Plugin) {
        currentPlugin.foreach(_.clean())

        // Switch to the new one.
        currentPlugin = Some(plugin)
        plugin.init(document.getElementById(elementToDrawIn))
        plugin.update(graph.get)

        currentPlugin.get match {
            case i: TextPlugin =>
                i.redraw()
        }
    }
}
