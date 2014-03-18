package cz.payola.web.client.views.graph

import cz.payola.common.rdf._
import cz.payola.common.entities.settings._
import cz.payola.web.client.views._
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap._
import cz.payola.web.client.views.graph.table._
import cz.payola.web.client.views.graph.visual._
import cz.payola.web.client.views.graph.visual.techniques.circle.CircleTechnique
import cz.payola.web.client.views.graph.visual.techniques.tree.TreeTechnique
import cz.payola.web.client.views.graph.visual.techniques.gravity.GravityTechnique
import cz.payola.web.shared.managers._
import cz.payola.web.client.events._
import cz.payola.web.client.views.elements.lists.ListItem
import cz.payola.web.client.views.graph.sigma.GraphSigmaPluginView
import cz.payola.web.client.views.graph.datacube._
import cz.payola.web.client.models.PrefixApplier
import s2js.compiler.javascript
import cz.payola.web.client.views.map._
import cz.payola.web.client.views.map.facets.GroupingMapFacet
import cz.payola.web.client.views.graph.empty.EmptyPluginView
import cz.payola.web.client.util.UriHashTools
import cz.payola.web.client.views.d3.packLayout._
import cz.payola.web.client.views.datacube.DataCubeVisualizer

class PluginSwitchView(prefixApplier: PrefixApplier, startEvaluationId: Option[String] = None, analysisId: Option[String]) extends GraphView with ComposedView
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
    val userCustomizationSelected = new SimpleUnitEvent[UserCustomization]

    /**
     * Event triggered when user customization is cleared (switched to "do not use any customization")
     */
    val userCustomizationCleared = new SimpleUnitEvent[UserCustomization]

    /**
     * Event triggered when user customization is created.
     */
    val userCustomizationCreateClicked = new SimpleUnitEvent[this.type]

    /**
     * Event triggered when user customization is edited.
     */
    val userCustomizationEditClicked = new SimpleUnitEvent[UserCustomization]

    /**
     * List of available visualization plugins.
     */
    private val plugins = List[PluginView[_]](
        new TripleTablePluginView(Some(prefixApplier)),
        new SelectResultPluginView(Some(prefixApplier)),
        new CircleTechnique(Some(prefixApplier)),
        new TreeTechnique(Some(prefixApplier)),
        new GravityTechnique(Some(prefixApplier)),
        new ColumnChartPluginView(Some(prefixApplier)),
        new GraphSigmaPluginView(Some(prefixApplier)),
        new TimeHeatmap(Some(prefixApplier)),
        new Generic(Some(prefixApplier)),
        new GoogleMapView(Some(prefixApplier)),
        new GoogleHeatMapView(Some(prefixApplier)),
        new ArcGisMapView(Some(prefixApplier)),
        new PackLayout(Some(prefixApplier)),
        new Sunburst(Some(prefixApplier)),
        new ZoomableSunburst(Some(prefixApplier)),
        new ZoomableTreemap(Some(prefixApplier)),
        new DataCubeVisualizer(Some(prefixApplier))
    )

    /**
     * Currently used visualization plugin.
     */
    private var currentPlugin: PluginView[_] = getInitialPlugin

    private def getInitialPlugin: PluginView[_] = {
        if(startEvaluationId.isDefined) {
            setEvaluationId(startEvaluationId)
        }
        plugins.find{ plugin =>
            evaluationId.isEmpty && normalizeClassName(plugin.getClass.getName) == UriHashTools.getUriParameter("viewPlugin")
        }.getOrElse(new EmptyPluginView())
    }

    /**
     * Parent to the visualization plugin View object.
     */
    private val pluginSpace = new Div(Nil, "plugin-space")

    def normalizeClassName(x: String) = x.replaceAll(".", "_")

    /**
     * Drop down button for selection of graph visualization.
     */
    val pluginChangeButton: DropDownButton = new DropDownButton(List(
        new Icon(Icon.eye_open),
        new Text("Change visualization plugin")),
        //GoogleMap should not be available for browsing mode
        plugins.map { plugin =>
            createPluginSwitchButtonItem(plugin)

        } /*++ plugins.takeRight(1).map { googleMapPlugin =>
            val pluginAnchor = new Anchor(List(new Text(googleMapPlugin.name))).setAttribute(
                "title", "Available only in analysis mode").setAttribute("style", "color: black; background-color: white;")
            val listItem = new ListItem(List(pluginAnchor), normalizeClassName(googleMapPlugin.getClass.getName))
            listItem //TODO which plugins are not available for browsing???
        }*/
    )

    /**
     * Drop down button for selection of customization.
     */
    val customizationsButton = new DropDownButton(List(
        new Icon(Icon.wrench),
        new Text("Change appearance")),
        Nil
    )

    val languagesButton = new DropDownButton(
        List(new Icon(Icon.globe), new Text("Language")),
        Nil,
        "", "pull-right"
    ).setAttribute("style", "margin: 0 5px;")

    /**
     * Toolbar containing pluginChange, customization buttons
     */
    val toolbar = new Div(List(pluginChangeButton, customizationsButton), "btn-toolbar").setAttribute(
        "style", "margin-left: 0; margin-top: 15px; margin-bottom: 15px;")

    // Re-trigger all events when the corresponding events are triggered in the plugins.
    plugins.foreach { plugin =>
        plugin.vertexSelected += { e => vertexSelected.trigger(createVertexEventArgs(e.vertex))}
        plugin.vertexBrowsing += { e => vertexBrowsing.trigger(createVertexEventArgs(e.vertex))}
        plugin.vertexSetMain += { e => vertexSetMain.trigger(createVertexEventArgs(e.vertex))}
        plugin.vertexBrowsingDataSource += { e => vertexBrowsingDataSource.trigger(createVertexEventArgs(e.vertex))}
    }

    // Display the plugin.
    currentPlugin.render(pluginSpace.htmlElement)
    languagesButton.render(toolbar.htmlElement)
    currentPlugin.renderControls(toolbar.htmlElement)

    def createSubViews = List(toolbar, pluginSpace)

    override def update(graph: Option[Graph], customization: Option[DefinedCustomization], serializedGraph: Option[Any]) {
        super.update(graph, customization, serializedGraph)
        currentPlugin.setEvaluationId(evaluationId)
        currentPlugin.setBrowsingURI(browsingURI)
        currentPlugin.update(graph, customization, serializedGraph)
    }

    override def updateGraph(graph: Option[Graph], contractLiterals: Boolean) {

        super.updateGraph(graph, contractLiterals)
        currentPlugin.setEvaluationId(evaluationId)
        currentPlugin.setBrowsingURI(browsingURI)
        currentPlugin.updateGraph(graph, contractLiterals)

        drawGraph()
    }

    override def updateSerializedGraph(serializedGraph: Option[Any]) {

        super.updateSerializedGraph(serializedGraph)
        currentPlugin.setEvaluationId(evaluationId)
        currentPlugin.setBrowsingURI(browsingURI)
        currentPlugin.updateSerializedGraph(serializedGraph)
    }

    override def updateCustomization(customization: Option[DefinedCustomization]) {
        super.updateCustomization(customization)
        currentPlugin.updateCustomization(customization)
    }

    override def drawGraph() {
        currentPlugin.drawGraph()
    }

    override def setMainVertex(vertex: Vertex) {
        currentPlugin.setMainVertex(vertex)
    }

    override def setLanguage(language: Option[String]) {}


    /**
     * Updates the list of ontology customizations showed in the ontologyCustomizationButton drop-down button.
     * @param customizations customizations to set to the drop-down button
     */
    def updateAvailableCustomizations(userCustomizations: UserCustomizationsByOwnership,
        ontoCustomizations: OntologyCustomizationsByOwnership) {

        // The owned customizations that are editable.
        val ownedOnto = ontoCustomizations.ownedCustomizations.getOrElse(Nil).map(oo =>
            createOntologyCustomizationListItem(oo, true))
        val ownedUser = userCustomizations.ownedCustomizations.getOrElse(Nil).map(ou =>
            createUserCustomizationListItem(ou, true))

        // The customizations of other users.
        val othersOnto = ontoCustomizations.othersCustomizations.map(oo =>
            createOntologyCustomizationListItem(oo, false))
        val othersUser = userCustomizations.othersCustomizations.map(ou =>
            createUserCustomizationListItem(ou, false))

        // A separator between owned and others customizations.
        val separator1 = if ((ownedOnto.nonEmpty || ownedUser.nonEmpty) && (othersOnto.nonEmpty || othersUser.nonEmpty))
            List(new ListItem(Nil, "divider")) else Nil

        val emptyCustomization =
            if (ownedOnto.nonEmpty || ownedUser.nonEmpty || othersOnto.nonEmpty || othersUser.nonEmpty)
                List(new ListItem(Nil, "divider"), createEmptyCustomization())
            else Nil

        // The create new ontology based button.
        val createButtonByOntologyCustomization =
            new Anchor(List(new Icon(Icon.plus), new Text("Create New Ontology Customization")))
        createButtonByOntologyCustomization.mouseClicked += { e =>
            ontologyCustomizationCreateClicked.triggerDirectly(this)
            false
        }

        // The create new user defined customization button.
        val createButtonByUserCustomization =
            new Anchor(List(new Icon(Icon.plus), new Text("Create New User Customization")))
        createButtonByUserCustomization.mouseClicked += { e =>
            userCustomizationCreateClicked.triggerDirectly(this)
            false
        }

        val createNewCustomization =
            if(userCustomizations.ownedCustomizations.isDefined || ontoCustomizations.ownedCustomizations.isDefined) {
                val separator2 =
                    if ((ownedOnto.nonEmpty || ownedUser.nonEmpty) && (othersOnto.nonEmpty || othersUser.nonEmpty))
                        List(new ListItem(Nil, "divider")) else Nil
                separator2 ++ List(
                    new ListItem(List(createButtonByOntologyCustomization)),
                    new ListItem(List(createButtonByUserCustomization)))
            } else { Nil }

        // All the items merged together.
        val allItems = ownedOnto ++ ownedUser ++ separator1 ++ othersOnto ++ othersUser ++
            emptyCustomization ++ createNewCustomization
        val items = if (allItems.nonEmpty) {
            allItems
        } else {
            val listItem = new ListItem(List(new Text("No settings available")))
            listItem.setAttribute("style", "padding-left: 10px;")
            List(listItem)
        }

        customizationsButton.items = items
    }

    def updateLanguages(languagesList: Seq[String]) {
        val listItems = languagesList.map { language =>
            val langText = new Text(language)
            val langListItem = new ListItem(List(new Anchor(List(langText))))
            langListItem.mouseClicked += { _ =>
                setLanguage(Some(langText.text)) //TODO
                languagesButton.setActiveItem(langListItem)
                false
            }
            langListItem
        }
        languagesButton.items = listItems
    }

    /**
     * Creates a single item for the ontologyCustomizationButton drop-down button.
     * @param customization that the created listItem will represent
     * @param isEditable if true the Edit button will be added to the created listItem
     * @return listItem representing the ontology customization
     */
    private def createOntologyCustomizationListItem(customization: OntologyCustomization, isEditable: Boolean): ListItem = {
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
            ontologyCustomizationEditClicked.triggerDirectly(customization)
            false
        }

        listItem
    }

    private def createUserCustomizationListItem(customization: UserCustomization, isEditable: Boolean): ListItem = {
        val editButton = new Button(new Text("Edit"), "btn-mini", new Icon(Icon.pencil)).setAttribute(
            "style", "position: absolute; right: 5px;")
        val anchor = new Anchor(List(new Text(customization.name)) ++ (if (isEditable) List(editButton) else Nil))

        val listItem = new ListItem(List(anchor))
        if (currentCustomization.exists(_ == customization)) {
            listItem.addCssClass("active")
        }

        anchor.mouseClicked += { e =>
            userCustomizationSelected.triggerDirectly(customization)
            customizationsButton.setActiveItem(listItem)
            false
        }

        editButton.mouseClicked += { e =>
            userCustomizationEditClicked.triggerDirectly(customization)
            false
        }

        listItem
    }

    private def createEmptyCustomization(): ListItem = {
        val anchor = new Anchor(List(new Text("Disable customization")))
        val listItem = new ListItem(List(anchor))

        anchor.mouseClicked += { e =>
            userCustomizationCleared.triggerDirectly(null)
            customizationsButton.setActiveItem(listItem)
            false
        }

        listItem
    }

    @javascript("""console.log(x)""")
    def log (x: Any) {}

    /**
     * Visualization plugin setter.
     * @param plugin plugin to set
     */
    private def changePlugin(plugin: PluginView[_]) {
        if (currentPlugin != plugin) {
            // Destroy the current plugin.
            currentPlugin.update(None, None, None)
            currentPlugin.destroyControls()
            currentPlugin.destroy()
            languagesButton.destroy()

            // Switch to the new plugin.

            currentPlugin = plugin
            currentPlugin.setEvaluationId(None)
            currentPlugin.setBrowsingURI(None)

            //the default visualization is TripleTableView, which has implemented a server-side caching, support for other visualizations will be added with transformation layer
            //now the whole graph has to fetched, this will be taken care of in transformation layer in next cache release iteration
            if(evaluationId.isDefined) {
                currentPlugin.setEvaluationId(evaluationId)
                currentPlugin.setBrowsingURI(browsingURI)
                currentPlugin.loadDefaultCachedGraph(evaluationId.get, {toUpdate =>
                    toUpdate match {
                        case smth: Some[_] =>
                            smth.get match {
                                case graph: Graph =>
                                    currentGraph = Some(graph)
                                    update(currentGraph, currentCustomization, None)

                                case str =>
                                    currentGraph = None
                                    currentSerializedGraph = Some(str)
                                    update(None, currentCustomization, currentSerializedGraph)
                            }
                        case _ =>
                            currentGraph = None
                            currentSerializedGraph = None
                            update(None, currentCustomization, None)
                    }
                    languagesButton.render(toolbar.htmlElement)
                    currentPlugin.renderControls(toolbar.htmlElement)
                    currentPlugin.render(pluginSpace.htmlElement)
                })
            } else {
                //this is correct, since googleMap (which uses serialized graph) plugin is not available in browsing mode
                currentSerializedGraph = None
                languagesButton.render(toolbar.htmlElement)
                currentPlugin.renderControls(toolbar.htmlElement)
                currentPlugin.render(pluginSpace.htmlElement)
                update(currentGraph, currentCustomization, None)
                currentPlugin.drawGraph()
            }
        }
    }

    private def createVertexEventArgs(vertex: Vertex): VertexEventArgs[this.type] = {
        new VertexEventArgs[this.type](this, vertex)
    }

    def getCurrentGraph = this.currentGraph

    def getCurrentGraphView = currentPlugin match {
        case visual: VisualPluginView =>
            visual.getGraphView
        case _ => None
    }


    @javascript(""" ga('send', 'event', 'Visualization', 'Show', visualizationName); """)
    def analyticsHit(visualizationName: String) {}

    /**
     * Each pluginView checks for its pair transformator in availableTransformations and if it is present in the list
     * and the pluginView is able to visualize the transformed graph (check by plugin.isAvailable)
     * a link to that pluginView is added to the PluginSwitchButton (thus the plugin is available for user).
     *
     * Additionally if preferredPlugin is available (transformator is available and the plugin can process
     * the transformation result), the pluginSwitchView switches to it.
     * @param availableTransformations
     * @param evaluationId
     * @param preferedPlugin
     */
    def setAvailablePlugins(availableTransformations: List[String], preferredPlugin: String) {

        if(evaluationId.isDefined) {
            pluginChangeButton.items = plugins.map { plugin =>
                createPluginSwitchButtonItem(plugin)
            }

            plugins.foreach{ plugin =>
                plugin.isAvailable(availableTransformations, evaluationId.get, { () =>
                    if(normalizeClassName(plugin.getClass.getName) == preferredPlugin) {
                        autoSwitchPlugin(normalizeClassName(plugin.getClass.getName))
                    }
                }, { () =>
                    pluginChangeButton.items.find(_.subViews(0).asInstanceOf[Anchor].subViews(0).asInstanceOf[Text].text == plugin.name).map(
                        _.hide())
                })}
        } else {
            new Div(List(new Text("No visualization plugin is available...")), "plugin-message large").render(
                pluginSpace.htmlElement)
        }
    }

    private def createPluginSwitchButtonItem(plugin: PluginView[_]): ListItem = {
        val pluginAnchor = new Anchor(List(new Text(plugin.name)))
        val listItem = new ListItem(List(pluginAnchor), normalizeClassName(plugin.getClass.getName))
        pluginAnchor.mouseClicked += { e =>
            pluginChangeButton.setActiveItem(listItem)
            changePlugin(plugin)
            UriHashTools.setUriParameter("viewPlugin", normalizeClassName(plugin.getClass.getName))
            false
        }
        listItem
    }

    @javascript("""
          if (normalizedPluginName.length > 0){
            jQuery(".dropdown-menu ."+normalizedPluginName+" a").click();
          }
                """)
    private def autoSwitchPlugin(normalizedPluginName: String) {}
}
