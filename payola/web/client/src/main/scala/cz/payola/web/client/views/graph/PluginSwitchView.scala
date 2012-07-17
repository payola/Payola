package cz.payola.web.client.views.graph

import cz.payola.common.rdf.Graph
import cz.payola.web.client.views._
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap.Icon
import cz.payola.web.client.views.graph.textual.TripleTablePluginView
import cz.payola.web.client.views.graph.visual.settings.components.visualsetup.VisualSetup
import cz.payola.web.client.views.graph.visual.settings._

class PluginSwitchView extends GraphView with ComposedView
{
    // TODO
    val visualSetup = new VisualSetup(new VertexSettingsModel, new EdgeSettingsModel, new TextSettingsModel)

    private val plugins = List[PluginView](
        new TripleTablePluginView(null)/*,
            new CircleTechnique(visualSetup),
            new TreeTechnique(visualSetup),
            new MinimalizationTechnique(visualSetup),
            new GravityTechnique(visualSetup)*/
    )

    private var currentPlugin = plugins.head

    private var currentGraph: Option[Graph] = None

    private val pluginSpace = new Div(Nil, "row-fluid")

    // Re-trigger all events when the corresponding events are triggered in the plugins.
    plugins.foreach { plugin =>
        plugin.vertexSelected += { e => vertexSelected.trigger(new VertexEventArgs[this.type](this, e.vertex)) }
        plugin.vertexBrowsing += { e => vertexBrowsing.trigger(new VertexEventArgs[this.type](this, e.vertex)) }
    }

    // Display the first plugin.
    currentPlugin.render(pluginSpace.domElement)

    def updateGraph(graph: Option[Graph]) {
        currentGraph = graph
        currentPlugin.updateGraph(graph)
    }

    def createSubViews = {
        val pluginChangeAnchor = new Anchor(
            List(new Icon(Icon.cog), new Text("Change Visualisation Plugin"), new Span(Nil, "caret")),
            "#", "btn dropdown-toggle"
        )
        pluginChangeAnchor.setAttribute("data-toggle", "dropdown")

        val pluginListItems = plugins.map { plugin =>
            val pluginAnchor = new Anchor(List(new Text(plugin.name)))
            pluginAnchor.mouseClicked += { e =>
                changePlugin(plugin)
                false
            }
            new ListItem(List(pluginAnchor))
        }
        val pluginList = new UnorderedList(pluginListItems, "dropdown-menu")
        val controls = new Div(List(pluginChangeAnchor, pluginList), "btn-group")
        controls.setAttribute("style", "padding-bottom: 20px;")

        List(controls, pluginSpace)
    }

    private def changePlugin(plugin: PluginView) {
        if (currentPlugin != plugin) {
            // Destroy the current plugin.
            currentPlugin.destroy()
            currentPlugin.updateGraph(None)

            // Switch to the new plugin.
            currentPlugin = plugin
            currentPlugin.render(pluginSpace.domElement)
            currentPlugin.updateGraph(currentGraph)
        }
    }
}
