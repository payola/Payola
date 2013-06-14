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
import cz.payola.web.client.views.graph.sigma.GraphSigmaPluginView
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
     * Event triggered when user customization is selected.
     */
    val userCustomizationSelected = new SimpleUnitEvent[OntologyCustomization]

    /**
     * Event triggered when user customization is created.
     */
    val userCustomizationCreateClicked = new SimpleUnitEvent[this.type]

    /**
     * Event triggered when user customization is edited.
     */
    val userCustomizationEditClicked = new SimpleUnitEvent[OntologyCustomization]

    /**
     * List of available visualization plugins.
     */
    private val plugins = List[PluginView](
        new TripleTablePluginView,
        new SelectResultPluginView,
        new CircleTechnique,
        new TreeTechnique,
        new GravityTechnique,
        new ColumnChartPluginView,
        new GraphSigmaPluginView,
        new TimeHeatmap
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
     * Drop down button for selection of customization.
     */
    val customizationsButton = new DropDownButton(List(
        new Icon(Icon.wrench),
        new Text("Change appearance")),
        Nil
    )

    /**
     * Toolbar containing  pluginChange ontology customization buttons
     */
    val toolbar = new Div(List(pluginChangeButton, customizationsButton), "btn-toolbar").setAttribute(
        "style", "margin-bottom: 15px;")

    // Re-trigger all events when the corresponding events are triggered in the plugins.
    plugins.foreach { plugin =>
        plugin.vertexSelected += { e => vertexSelected.trigger(createVertexEventArgs(e.vertex))}
        plugin.vertexBrowsing += { e => vertexBrowsing.trigger(createVertexEventArgs(e.vertex))}
        plugin.vertexSetMain += { e => vertexSetMain.trigger(createVertexEventArgs(e.vertex))}
        plugin.vertexBrowsingDataSource += { e => vertexBrowsingDataSource.trigger(createVertexEventArgs(e.vertex))}
    }

    // Display the first plugin.
    pluginChangeButton.setActiveItem(pluginChangeButton.items.head)
    currentPlugin.render(pluginSpace.htmlElement)
    currentPlugin.renderControls(toolbar.htmlElement)

    def createSubViews = List(toolbar, pluginSpace)

    override def update(graph: Option[Graph], customization: Option[OntologyCustomization], resultsCount: Option[Int]) {
        super.update(graph, customization, resultsCount)
        currentPlugin.update(graph, customization, resultsCount)
    }

    override def updateGraph(graph: Option[Graph], contractLiterals: Boolean, resultsCount: Option[Int]) {
        super.updateGraph(graph, contractLiterals, resultsCount)
        currentPlugin.updateGraph(graph, contractLiterals, resultsCount)
    }

    override def updateOntologyCustomization(customization: Option[OntologyCustomization]) {
        super.updateOntologyCustomization(customization)
        currentPlugin.updateOntologyCustomization(customization)
    }

    override def setMainVertex(vertex: Vertex) {
        currentPlugin.setMainVertex(vertex)
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

        // The create new ontology based button.
        val createButtonByOntology = new Anchor(List(new Icon(Icon.plus), new Text("Create New Ontology Customization")))
        createButtonByOntology.mouseClicked += { e =>
            ontologyCustomizationCreateClicked.triggerDirectly(this)
            false
        }

        // The create new user defined customization button.
        val createButtonCustom = new Anchor(List(new Icon(Icon.plus), new Text("Create New User Customization")))
        createButtonCustom.mouseClicked += { e =>
            userCustomizationCreateClicked.triggerDirectly(this)
            false
        }

        val createNewCustomization = customizations.ownedCustomizations.map { _ =>
            val separator2 = if (owned.nonEmpty || others.nonEmpty) List(new ListItem(Nil, "divider")) else Nil
            separator2 ++ List(new ListItem(List(createButtonByOntology)), new ListItem(List(createButtonCustom)))
        }.getOrElse(Nil)

        // All the items merged together.
        val allItems = owned ++ separator1 ++ others ++ createNewCustomization
        val items = if (allItems.nonEmpty) {
            allItems
        } else {
            val listItem = new ListItem(List(new Text("No settings available")))
            listItem.setAttribute("style", "padding-left: 10px;")
            List(listItem)
        }

        customizationsButton.items = items
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

        val anchor = new Anchor(List(new Text(customization.name)) ++ (if (isEditable) List(editButton) else Nil))

        val listItem = new ListItem(List(anchor))
        if (currentCustomization.exists(_ == customization)) {
            listItem.addCssClass("active")
        }

        anchor.mouseClicked += { e =>
            ontologyCustomizationSelected.triggerDirectly(customization)
            customizationsButton.setActiveItem(listItem)
            false
        }

        editButton.mouseClicked += { e =>
            if (customization.isUserDefined) {
                userCustomizationEditClicked.triggerDirectly(customization)
            } else {
                ontologyCustomizationEditClicked.triggerDirectly(customization)
            }
            false
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
            currentPlugin.update(None, None, None)
            currentPlugin.destroyControls()
            currentPlugin.destroy()

            // Switch to the new plugin.
            currentPlugin = plugin
            currentPlugin.render(pluginSpace.htmlElement)
            currentPlugin.renderControls(toolbar.htmlElement)
            currentPlugin.update(currentGraph, currentCustomization, currentResultsCount)
            currentPlugin.drawGraph()
        }
    }

    private def createVertexEventArgs(vertex: Vertex): VertexEventArgs[this.type] = {
        new VertexEventArgs[this.type](this, vertex)
    }

    def getCurrentGraph = this.currentGraph
}
