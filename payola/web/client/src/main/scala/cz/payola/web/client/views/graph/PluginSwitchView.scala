package cz.payola.web.client.views.graph

import cz.payola.common.rdf._
import cz.payola.common.entities.settings.OntologyCustomization
import cz.payola.web.client.views._
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap._
import cz.payola.web.client.views.graph.table._
import cz.payola.web.client.views.graph.visual.ColumnChartPluginView
import cz.payola.web.client.views.graph.visual.techniques.circle.CircleTechnique
import cz.payola.web.client.views.graph.visual.techniques.tree.TreeTechnique
import cz.payola.web.client.views.graph.visual.techniques.gravity.GravityTechnique
import cz.payola.web.shared.managers._
import cz.payola.web.client.events._
import cz.payola.web.client.views.elements.lists.ListItem
import cz.payola.web.client.views.graph.datacube.TimeHeatmap

class PluginSwitchView extends GraphView with ComposedView
{
    /**
     * Event triggered when ontology customization is created.
     */
    val ontologyCustomizationCreateClicked = new SimpleUnitEvent[this.type]

    /**
     * Event triggered when ontology customization is selected.
     */
    val ontologyCustomizationSelected = new SimpleUnitEvent[OntologyCustomization]

    /**
     * Event triggered when ontology customization is edited.
     */
    val ontologyCustomizationEditClicked = new SimpleUnitEvent[OntologyCustomization]

    /**
     * List of available visualization plugins.
     */
    private val plugins = List[PluginView](
        new TripleTablePluginView,
        new SelectResultPluginView,
        new CircleTechnique,
        new TreeTechnique,
        new GravityTechnique,
        new ColumnChartPluginView
//        new TimeHeatmap
    )

    /**
     * Currently used visualization plugin.
     */
    private var currentPlugin = plugins.head

    /**
     * Parent to the visualization plugin View object.
     */
    private val pluginSpace = new Div(Nil, "plugin-space")

    /**
     * Drop down button for selection of graph visualization.
     */
    val pluginChangeButton: DropDownButton = new DropDownButton(List(
        new Icon(Icon.eye_open),
        new Text("Change visualization plugin")),
        plugins.map { plugin =>
            val pluginAnchor = new Anchor(List(new Text(plugin.name)))
            val listItem = new ListItem(List(pluginAnchor))
            pluginAnchor.mouseClicked += { e =>
                pluginChangeButton.setActiveItem(listItem)
                changePlugin(plugin)
                false
            }
            listItem
        }
    )

    /**
     * Drop down button for selection of ontology.
     */
    val ontologyCustomizationsButton = new DropDownButton(List(
        new Icon(Icon.wrench),
        new Text("Change appearance using ontologies")),
        Nil
    )

    /**
     * Toolbar containing  pluginChange ontology customization buttons
     */
    val toolbar = new Div(List(pluginChangeButton, ontologyCustomizationsButton), "btn-toolbar").setAttribute(
        "style", "margin-bottom: 15px;")

    // Re-trigger all events when the corresponding events are triggered in the plugins.
    plugins.foreach { plugin =>
        plugin.vertexSelected += { e => vertexSelected.trigger(createVertexEventArgs(e.vertex))}
        plugin.vertexBrowsing += { e => vertexBrowsing.trigger(createVertexEventArgs(e.vertex))}
        plugin.vertexBrowsingDataSource += { e => vertexBrowsingDataSource.trigger(createVertexEventArgs(e.vertex))}
    }

    // Display the first plugin.
    pluginChangeButton.setActiveItem(pluginChangeButton.items.head)
    currentPlugin.render(pluginSpace.htmlElement)
    currentPlugin.renderControls(toolbar.htmlElement)

    def createSubViews = List(toolbar, pluginSpace)

    override def update(graph: Option[Graph], customization: Option[OntologyCustomization]) {
        super.update(graph, customization)
        currentPlugin.update(graph, customization)
    }

    override def updateGraph(graph: Option[Graph], contractLiterals: Boolean) {
        super.updateGraph(graph, contractLiterals)
        currentPlugin.updateGraph(graph, contractLiterals)
    }

    override def updateOntologyCustomization(customization: Option[OntologyCustomization]) {
        super.updateOntologyCustomization(customization)
        currentPlugin.updateOntologyCustomization(customization)
    }

    /**
     * Updates the list of ontology customizations showed in the ontologyCustomizationButton drop-down button.
     * @param customizations customizations to set to the drop-down button
     */
    def updateOntologyCustomizations(customizations: OntologyCustomizationsByOwnership) {
        // The owned customizations that are editable.
        val owned = customizations.ownedCustomizations.getOrElse(Nil).map(createCustomizationListItem(_, true))

        // The customizations of other users.
        val others = customizations.othersCustomizations.map(createCustomizationListItem(_, false))

        // A separator between owned and others customizations.
        val separator1 = if (owned.nonEmpty && others.nonEmpty) List(new ListItem(Nil, "divider")) else Nil

        // The create new button.
        val createButton = new Anchor(List(new Icon(Icon.plus), new Text("Create New Customization")))
        createButton.mouseClicked += { e =>
            ontologyCustomizationCreateClicked.triggerDirectly(this)
            false
        }
        val createNew = customizations.ownedCustomizations.map { _ =>
            val separator2 = if (owned.nonEmpty || others.nonEmpty) List(new ListItem(Nil, "divider")) else Nil
            separator2 ++ List(new ListItem(List(createButton)))
        }.getOrElse(Nil)

        // All the items merged together.
        val allItems = owned ++ separator1 ++ others ++ createNew
        val items = if (allItems.nonEmpty) {
            allItems
        } else {
            val listItem = new ListItem(List(new Text("No settings available")))
            listItem.setAttribute("style", "padding-left: 10px;")
            List(listItem)
        }

        ontologyCustomizationsButton.items = items
    }

    /**
     * Creates a single item for the ontologyCustomizationButton drop-down button.
     * @param customization that the created listItem will represent
     * @param isEditable if true the Edit button will be added to the created listItem
     * @return listItem representing the ontology customization
     */
    private def createCustomizationListItem(customization: OntologyCustomization, isEditable: Boolean): ListItem = {
        val editButton = new Button(new Text("Edit"), "btn-mini", new Icon(Icon.pencil)).setAttribute(
            "style", "position: absolute; right: 5px;")
        editButton.mouseClicked += { e =>
            ontologyCustomizationEditClicked.triggerDirectly(customization)
            false
        }

        val anchor = new Anchor(List(new Text(customization.name)) ++ (if (isEditable) List(editButton) else Nil))
        anchor.mouseClicked += { e =>
            ontologyCustomizationSelected.triggerDirectly(customization)
            false
        }

        val listItem = new ListItem(List(anchor))
        if (currentCustomization.exists(_ == customization)) {
            listItem.addCssClass("active")
        }
        listItem
    }

    /**
     * Visualization plugin setter.
     * @param plugin plugin to set
     */
    private def changePlugin(plugin: PluginView) {
        if (currentPlugin != plugin) {
            // Destroy the current plugin.
            currentPlugin.update(None, None)
            currentPlugin.destroyControls()
            currentPlugin.destroy()

            // Switch to the new plugin.
            currentPlugin = plugin
            currentPlugin.render(pluginSpace.htmlElement)
            currentPlugin.renderControls(toolbar.htmlElement)
            currentPlugin.update(currentGraph, currentCustomization)
        }
    }

    private def createVertexEventArgs(vertex: Vertex): VertexEventArgs[this.type] = {
        new VertexEventArgs[this.type](this, vertex)
    }
}
