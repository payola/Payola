package cz.payola.web.client.presenters

import s2js.adapters.browser._
import cz.payola.web.client.views.graph.PluginView
import cz.payola.web.client.views.graph.visual.techniques.tree.TreeTechnique
import cz.payola.web.client.views.graph.visual.techniques.circle.CircleTechnique
import cz.payola.web.client.views.graph.visual.techniques.gravity.GravityTechnique
import cz.payola.web.client.views.graph.visual.techniques.minimalization.MinimalizationTechnique
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.graph.table.TripleTablePluginView
import cz.payola.web.client.views.graph.visual.VisualPluginView
import cz.payola.web.client.views.elements.lists.ListItem

class Index(val elementToDrawIn: String = "graph-plugin-draw-space")
{
    var graph: Option[cz.payola.common.rdf.Graph] = None

    val plugins = List[PluginView](
        new CircleTechnique,
        new TripleTablePluginView,
        new TreeTechnique,
        new MinimalizationTechnique,
        new GravityTechnique
    )

    var currentPlugin: Option[PluginView] = None

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

    def updateSettings() {
        currentPlugin.get match {
            case i: VisualPluginView =>
                // TODO currentPlugin.get.redraw()
        }
    }

    def resetSettings() {
        currentPlugin.get match {
            case i: VisualPluginView =>
                // TODO currentPlugin.get.redraw()
        }
    }

    def changePluginByNumber(number: Int) {
        if (0 <= number && number < plugins.length) {
            changePlugin(plugins(number))
        }
    }

    def changePlugin(plugin: PluginView) {
        currentPlugin.foreach(_.destroy())

        // Switch to the new one.
        currentPlugin = Some(plugin)
        plugin.render(document.getElementById(elementToDrawIn))

        plugin.updateGraph(graph)

        /*currentPlugin.get match {
            case i: VisualPluginView =>
                i.vertexUpdate += { event =>
                    event.target match {
                        case ve: IdentifiedVertex =>
                            val neighborhood = GraphFetcher.getNeighborhoodOfVertex(ve.uri)
                            i.updateGraph(Some(neighborhood))
                        case _ =>
                    }
                    false
                }
        }*/
    }
}
