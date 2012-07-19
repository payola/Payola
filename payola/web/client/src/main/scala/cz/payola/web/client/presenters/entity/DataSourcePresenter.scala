package cz.payola.web.client.presenters.entity

import scala.collection.mutable
import s2js.adapters.js.dom
import s2js.adapters.js.browser._
import cz.payola.web.shared._
import cz.payola.web.client.views.entity._
import cz.payola.web.client.views.graph.PluginSwitchView
import cz.payola.web.client.events.BrowserEventArgs
import cz.payola.web.client.views.VertexEventArgs
import cz.payola.common.entities.plugins.DataSource
import cz.payola.web.client.views.bootstrap._
import cz.payola.common.entities.settings.OntologyCustomization
import cz.payola.web.client.presenters.OntologyCustomizationPresenter
import cz.payola.web.client.views.bootstrap.inputs.TextInputControl
import cz.payola.web.client.views.elements.Span

class DataSourcePresenter(
    viewElement: dom.Element,
    val dataSourceId: String,
    val dataSourceName: String,
    val initialVertexUri: String = "")
{
    private val view = new DataSourceView(dataSourceName)

    private val graphView = new PluginSwitchView

    private var dataSources: Option[Seq[DataSource]] = None

    private val history = mutable.ListBuffer.empty[String]

    private var historyPosition = -1

    def initialize() {
        // Register the event handlers.
        view.goButton.mouseClicked += onGoButtonClicked _
        view.backButton.mouseClicked += onBackButtonClicked _
        view.nextButton.mouseClicked += onNextButtonClicked _
        graphView.vertexBrowsing += onVertexBrowsing _
        graphView.vertexBrowsingDataSource += onVertexBrowsingDataSource _
        graphView.createOntologyCustomizationButton.mouseClicked += onCreateOntologyCustomizationButtonClicked _
        graphView.ontologyCustomizationEditButtons foreach { button =>
            button.mouseClicked += onEditOntologyCustomizationButtonClicked _
        }

        // Compose the views and render the main view.
        graphView.render(view.graphViewSpace.domElement)
        view.render(viewElement)

        if (initialVertexUri == "") {
            view.block() // TODO loading.
            DataSourceBrowser.getInitialGraph(dataSourceId) { graph =>
                graphView.updateGraph(graph)
                updateNavigationView()
                view.unblock()
            } { error =>
                // TODO
            }
        } else {
            addToHistoryAndGo(initialVertexUri)
        }
    }

    private def onVertexBrowsing(e: VertexEventArgs[_]) {
        addToHistoryAndGo(e.vertex.uri)
    }

    private def onVertexBrowsingDataSource(e: VertexEventArgs[_]) {
        fetchDataSources { ds =>
            val selector = new DataSourceSelector("Browse in different data source: " + e.vertex.uri, ds)
            selector.dataSourceSelected += { d =>
                window.location.href = "/datasource/" + d.target.id + "?uri=" + encodeURI(e.vertex.uri)
            }
            selector.render()
        }
    }

    private def presentCustomizationEditorWithCustomization(customization: OntologyCustomization) {
        val presenter = new OntologyCustomizationPresenter(customization)
        presenter.initialize()

        // TODO save on the server
    }

    private def onCreateOntologyCustomizationButtonClicked(e: BrowserEventArgs[_]): Boolean = {
        // First, show a modal asking for the Ontology URL
        // TODO remove default values which are added for testing purposes
        val nameInputField = new TextInputControl("Customization name", "ontologyCustomizationNameField", "My Ontology Customization", "")
        val urlInputField = new TextInputControl("Ontology URL", "ontologyURLField", "http://opendata.cz/pco/public-contracts.xml", "")
        val modal = new Modal("Create a new ontology customization.", List(nameInputField, urlInputField), Some("Create Customization"), Some("Cancel"), false)
        modal.saving += { e =>
            if (nameInputField.input.value == "") {
                window.alert("Name input mustn't be empty!")
            }else if (urlInputField.input.value == "") {
                window.alert("Ontology URL field mustn't be empty!")
            }else if (OntologyCustomizationManager.customizationExistsWithName(nameInputField.input.value)) {
                window.alert("You have already created a customization with this name!")
            }else{
                OntologyCustomizationManager.createNewOntologyCustomizationForURL(urlInputField.input.value, nameInputField.input.value) { custom =>
                    modal.destroy()
                    presentCustomizationEditorWithCustomization(custom)
                }
                { t =>
                    window.alert("Couldn't create an ontology customization with this URL.\n\n" + t.getMessage)
                }
            }
            false
        }

        modal.render()
        false
    }

    private def onEditOntologyCustomizationButtonClicked(e: BrowserEventArgs[Span]): Boolean = {
        val ontologyID = e.target.getAttribute("name")
        presentCustomizationEditorWithCustomization(OntologyCustomizationManager.getCustomizationByID(ontologyID))
        true
    }

    private def onBackButtonClicked(e: BrowserEventArgs[_]): Boolean = {
        if (canGoBack) {
            historyPosition -= 1
            updateView()
        }
        false
    }

    private def onNextButtonClicked(e: BrowserEventArgs[_]): Boolean = {
        if (canGoNext) {
            historyPosition += 1
            updateView()
        }
        false
    }

    private def onGoButtonClicked(e: BrowserEventArgs[_]): Boolean = {
        addToHistoryAndGo(view.nodeUriInput.value)
        false
    }

    private def addToHistoryAndGo(uri: String) {
        // Remove all next items from the history.
        while (historyPosition < history.length - 1) {
            history.remove(historyPosition + 1)
        }

        // Add the new item.
        history += uri
        historyPosition += 1

        updateView()
    }

    private def updateView() {
        val uri = history(historyPosition)

        view.block()
        DataSourceBrowser.getNeighbourhood(dataSourceId, uri) { graph =>
            graphView.updateGraph(graph)
            updateNavigationView()
            view.nodeUriInput.value = uri
            view.unblock()
        } { error =>
            // TODO
        }
    }

    private def updateNavigationView() {
        view.backButton.setIsEnabled(canGoBack)
        view.nextButton.setIsEnabled(canGoNext)
    }

    private def fetchDataSources(callback: Seq[DataSource] => Unit) {
        if (dataSources.isDefined) {
            callback(dataSources.get)
        } else {
            view.block()
            DataSourceBrowser.getDataSources() { ds =>
                dataSources = Some(ds)
                view.unblock()
                callback(ds)
            } { error =>

            }
        }
    }

    private def canGoBack = history.nonEmpty && historyPosition > 0

    private def canGoNext = history.nonEmpty && historyPosition < (history.length - 1)
}
