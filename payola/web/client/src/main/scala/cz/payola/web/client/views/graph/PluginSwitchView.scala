package cz.payola.web.client.views.graph

import cz.payola.common.rdf._
import cz.payola.web.client.views._
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap._
import cz.payola.web.client.views.graph.textual.TripleTablePluginView
import cz.payola.web.client.views.graph.visual.settings.components.visualsetup.VisualSetup
import cz.payola.web.client.views.graph.visual.settings._
import cz.payola.web.client.views.graph.visual.techniques.circle.CircleTechnique
import cz.payola.web.client.views.graph.visual.techniques.tree.TreeTechnique
import cz.payola.web.client.views.graph.visual.techniques.gravity.GravityTechnique
import scala.collection.mutable.ListBuffer
import cz.payola.web.shared.managers.OntologyCustomizationManager
import cz.payola.web.client.events._

class PluginSwitchView extends GraphView with ComposedView
{
    // TODO
    private val visualSetup = new VisualSetup(new VertexSettingsModel, new EdgeSettingsModel, new TextSettingsModel)

    private val plugins = List[PluginView](
        new TripleTablePluginView(null),
        new CircleTechnique(visualSetup),
        new GravityTechnique(visualSetup),
        new TreeTechnique(visualSetup)/*,
        new MinimalizationTechnique(visualSetup),*/
    )

    private var currentPlugin = plugins.head

    private var currentGraph: Option[Graph] = None

    private val pluginSpace = new Div(Nil, "row position-relative")

    val createOntologyCustomizationButton = new Anchor(List(new Icon(Icon.plus), new Text("Create new settings")))

    val pluginListItems = plugins.map { plugin =>
        val pluginAnchor = new Anchor(List(new Text(plugin.name)))
        pluginAnchor.mouseClicked += { e =>
            changePlugin(plugin)
            false
        }
        new ListItem(List(pluginAnchor))
    }

    val ontologiesGotLoaded = new SimpleUnitEvent[this.type]
    val ontologyCustomizationEditButtons = new ListBuffer[Span]
    val ontologyCustomizationListItems = new ListBuffer[ListItem]()
    createOntologyCustomizationItems()
    val ontologyCustomizationsButton = new DropDownButton(List(new Text("Change appearance using ontologies")), ontologyCustomizationListItems)

    private def createOntologyCustomizationItems() {
        ontologyCustomizationListItems += new ListItem(List(createOntologyCustomizationButton))
        OntologyCustomizationManager.getUsersCustomizations(){ customizations =>
            customizations.foreach({ custom =>
                val text = new Text(custom.name)
                val editButton = new Span(List(new Icon(Icon.pencil), new Text(" Edit")), "btn btn-mini btn-info ontology-customization-edit-button")
                editButton.setAttribute("name", custom.id)
                ontologyCustomizationEditButtons += editButton
                val listItem = new ListItem(List(new Anchor(List(text, editButton))), "ontology-customization-menu-item")
                ontologyCustomizationListItems += listItem
                listItem.render(ontologyCustomizationsButton.menu.domElement)
            })
            ontologiesGotLoaded.trigger(new EventArgs[PluginSwitchView.this.type](this))
        }{ t: Throwable =>
            // TODO - couldn't load ontology customizations
        }
    }

    val toolbar = new Div(List(
        new DropDownButton(List(new Icon(Icon.cog), new Text("Change visualisation plugin")), pluginListItems),
        ontologyCustomizationsButton),
        "btn-toolbar"
    )

    toolbar.setAttribute("style", "margin-bottom: 15px;")

    // Re-trigger all events when the corresponding events are triggered in the plugins.
    plugins.foreach { plugin =>
        plugin.vertexSelected += { e => vertexSelected.trigger(createVertexEventArgs(e.vertex)) }
        plugin.vertexBrowsing += { e => vertexBrowsing.trigger(createVertexEventArgs(e.vertex)) }
        plugin.vertexBrowsingDataSource += { e => vertexBrowsingDataSource.trigger(createVertexEventArgs(e.vertex)) }
    }

    // Display the first plugin.
    currentPlugin.render(pluginSpace.domElement)
    currentPlugin.renderControls(toolbar.domElement)

    def updateGraph(graph: Option[Graph]) {
        currentGraph = graph
        currentPlugin.updateGraph(graph)
    }

    def createSubViews = List(toolbar, pluginSpace)

    private def changePlugin(plugin: PluginView) {
        if (currentPlugin != plugin) {
            // Destroy the current plugin.
            currentPlugin.updateGraph(None)
            currentPlugin.destroyControls()
            currentPlugin.destroy()

            // Switch to the new plugin.
            currentPlugin = plugin
            currentPlugin.render(pluginSpace.domElement)
            currentPlugin.renderControls(toolbar.domElement)
            currentPlugin.updateGraph(currentGraph)
        }
    }

    private def createVertexEventArgs(vertex: IdentifiedVertex): VertexEventArgs[this.type] = {
        new VertexEventArgs[this.type](this, vertex)
    }
}
