package cz.payola.web.client.presenters

import s2js.adapters.js.browser._
import cz.payola.web.client.views.plugins.Plugin
import cz.payola.web.client.views.plugins.visual.techniques.tree.TreeTechnique
import cz.payola.web.client.views.plugins.visual.techniques.circle.CircleTechnique
import cz.payola.web.client.views.plugins.visual.techniques.gravity.GravityTechnique
import cz.payola.web.client.views.plugins.visual.techniques.minimalization.MinimalizationTechnique
import cz.payola.web.client.views.plugins.visual.settings.components.visualsetup.VisualSetup
import cz.payola.web.client.views.plugins.visual._
import settings._
import cz.payola.web.shared._
import cz.payola.common.rdf.IdentifiedVertex
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.plugins.textual.TripleTablePlugin

class Index(val elementToDrawIn: String = "graph-plugin-draw-space")
{
    var graph: Option[cz.payola.common.rdf.Graph] = None

    val vertexSettings = new VertexSettingsModel

    val edgesSettings = new EdgeSettingsModel

    val textSettings = new TextSettingsModel

    val visualSetup = new VisualSetup(vertexSettings, edgesSettings, textSettings)

    visualSetup.settingsChanged += {
        evt =>
            // TODO currentPlugin.get.redraw()
            false
    }

    val plugins = List[Plugin](
        new CircleTechnique(visualSetup),
        new TripleTablePlugin(visualSetup),
        new TreeTechnique(visualSetup),
        new MinimalizationTechnique(visualSetup),
        new GravityTechnique(visualSetup)
    )

    var currentPlugin: Option[Plugin] = None

    plugins.foreach { plugin =>

        val pluginBtn = new Anchor(List(new Text(plugin.name)), "#")
        new ListItem(List(pluginBtn)).render(document.getElementById("settings"))

        pluginBtn.mouseClicked += {
            event =>
                val newPlugin = plugins.find(_.name == plugin.name)
                if (newPlugin.isDefined) {
                    changePlugin(newPlugin.get)
                }
                false
        }
    }

    def init() {
        visualSetup.render(document.getElementById("settings"))
        graph = Some(GraphFetcher.getInitialGraph)
        changePlugin(plugins.head)
    }

    def updateSettings() {
        currentPlugin.get match {
            case i: VisualPlugin =>
                // TODO currentPlugin.get.redraw()
        }
    }

    def resetSettings() {
        currentPlugin.get match {
            case i: VisualPlugin =>
                // TODO currentPlugin.get.redraw()
        }
    }

    def changePluginByNumber(number: Int) {
        if (0 <= number && number < plugins.length) {
            changePlugin(plugins(number))
        }
    }

    def changePlugin(plugin: Plugin) {
        currentPlugin.foreach(_.destroy())

        // Switch to the new one.
        currentPlugin = Some(plugin)
        plugin.render(document.getElementById(elementToDrawIn))

        plugin.updateGraph(graph)

        currentPlugin.get match {
            case i: VisualPlugin =>
                i.vertexUpdate += { event =>
                    event.target match {
                        case ve: IdentifiedVertex =>
                            val neighborhood = GraphFetcher.getNeighborhoodOfVertex(ve.uri)
                            i.updateGraph(Some(neighborhood))
                        case _ =>
                    }
                    false
                }
        }
    }
}
