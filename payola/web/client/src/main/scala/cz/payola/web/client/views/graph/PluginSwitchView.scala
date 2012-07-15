package cz.payola.web.client.views.graph

import cz.payola.common.rdf.Graph
import cz.payola.web.client.views._
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap.Icon

class PluginSwitchView(val plugins: Seq[PluginView]) extends GraphView with ComposedView
{
    private var currentPlugin = plugins.head

    private var currentGraph: Option[Graph] = None

    private val pluginSpace = new Div()

    private val pluginSpaceElement = pluginSpace.domElement

    // Re-trigger all events when the corresponding events are triggered in the plugins.
    plugins.foreach { plugin =>
        plugin.vertexSelected += { e => vertexSelected.trigger(new VertexEventArgs[this.type](this, e.vertex)) }
        plugin.vertexBrowsing += { e => vertexBrowsing.trigger(new VertexEventArgs[this.type](this, e.vertex)) }
    }

    // Display the first plugin.
    currentPlugin.render(pluginSpaceElement)

    def updateGraph(graph: Option[Graph]) {
        currentGraph = graph
        currentPlugin.updateGraph(graph)
    }

    def createSubViews = {
        val dropDownAnchor = new Anchor(
            List(new Icon(Icon.cog), new Text("Change Visualisation Plugin"), new Span(Nil, "caret")),
            "#", "btn dropdown-toggle"
        )
        dropDownAnchor.setAttribute("data-toggle", "dropdown")

        val pluginListItems = plugins.map { plugin =>
            val pluginAnchor = new Anchor(List(new Text(plugin.name)))
            pluginAnchor.mouseClicked += { e =>
                changePlugin(plugin)
                false
            }
            new ListItem(List(pluginAnchor))
        }

        val pluginList = new UnorderedList(pluginListItems, "dropdown-menu")
        List(new Div(List(dropDownAnchor, pluginList), "btn-group"), pluginSpace)
    }

    private def changePlugin(plugin: PluginView) {
        if (currentPlugin != plugin) {
            // Destroy the current plugin.
            currentPlugin.destroy()
            currentPlugin.updateGraph(None)

            // Switch to the new plugin.
            currentPlugin = plugin
            currentPlugin.render(pluginSpaceElement)
            currentPlugin.updateGraph(currentGraph)
        }
    }
}
