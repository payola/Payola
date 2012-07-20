package cz.payola.web.client.presenters.entity

import scala.collection.mutable
import s2js.adapters.js.dom
import s2js.adapters.js.browser._
import cz.payola.web.shared._
import cz.payola.web.shared.managers.OntologyCustomizationManager
import cz.payola.web.client.events.BrowserEventArgs
import cz.payola.web.client.models.Model
import cz.payola.web.client.views.VertexEventArgs
import cz.payola.web.client.views.entity.dataSource._
import cz.payola.web.client.views.entity.customization.OntologyCustomizationCreateModal
import cz.payola.web.client.views.graph.PluginSwitchView
import cz.payola.web.client.Presenter
import cz.payola.web.client.presenters.OntologyCustomizationPresenter
import cz.payola.common.ValidationException

class DataSourcePresenter(
    viewElement: dom.Element,
    val dataSourceId: String,
    val dataSourceName: String,
    val initialVertexUri: String = "")
    extends Presenter
{
    private val view = new DataSourceView(dataSourceName)

    private val graphView = new PluginSwitchView

    private val history = mutable.ListBuffer.empty[String]

    private var historyPosition = -1

    def initialize() {
        // Register the event handlers.
        view.goButton.mouseClicked += onGoButtonClicked _
        view.backButton.mouseClicked += onBackButtonClicked _
        view.nextButton.mouseClicked += onNextButtonClicked _
        view.nodeUriInput.keyPressed += onNodeUriInputKeyPressed _
        graphView.vertexBrowsing += onVertexBrowsing _
        graphView.vertexBrowsingDataSource += onVertexBrowsingDataSource _
        graphView.createOntologyCustomizationButton.mouseClicked += onCreateOntologyCustomizationButtonClicked _

        // Compose the views and render the main view.
        graphView.render(view.graphViewSpace.domElement)
        view.render(viewElement)

        // If the default URI isn't specified, display the initial graph.
        if (initialVertexUri == "") {
            blockPage("Fetching the initial graph.")
            DataSourceBrowser.getInitialGraph(dataSourceId) { graph =>
                graphView.updateGraph(graph)
                updateNavigationView()
                unblockPage()
            }(fatalErrorHandler(_))

        // Otherwise display neighbourhood of the initial vertex.
        } else {
            addToHistoryAndGo(initialVertexUri)
        }
    }

    private def onVertexBrowsing(e: VertexEventArgs[_]) {
        addToHistoryAndGo(e.vertex.uri)
    }

    private def onVertexBrowsingDataSource(e: VertexEventArgs[_]) {
        blockPage("Fetching accessible data sources.")
        Model.accessibleDataSources { ds =>
            unblockPage()
            val selector = new DataSourceSelector("Browse in different data source: " + e.vertex.uri, ds)
            selector.dataSourceSelected += { d =>
                window.location.href = "/datasource/" + d.target.id + "?uri=" + encodeURI(e.vertex.uri)
            }
            selector.render()
        }(fatalErrorHandler(_))
    }

    private def onCreateOntologyCustomizationButtonClicked(e: BrowserEventArgs[_]): Boolean = {
        val createView = new OntologyCustomizationCreateModal
        createView.saving += { e =>
            createView.block("Creating the ontology customization.")
            OntologyCustomizationManager.create(createView.name.input.value, createView.url.input.value) { o =>
                createView.unblock()
                createView.destroy()
                new OntologyCustomizationPresenter(o).initialize()
            } { error =>
                createView.unblock()
                error match {
                    case v: ValidationException => {
                        createView.name.setState(v ,"name")
                        createView.url.setState(v, "ontologyURL")
                    }
                    case _ => {
                        createView.destroy()
                        fatalErrorHandler(error)
                    }
                }
            }
            false
        }

        createView.render()
        false
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

    private def onNodeUriInputKeyPressed(e: BrowserEventArgs[_]): Boolean = {
        // If it's enter.
        if (e.keyCode == 13) {
            addToHistoryAndGo(view.nodeUriInput.value)
            false
        } else {
            true
        }
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

        blockPage("Fetching the node neighbourhood.")
        view.nodeUriInput.setIsEnabled(false)

        DataSourceBrowser.getNeighbourhood(dataSourceId, uri) { graph =>
            graphView.updateGraph(graph)
            updateNavigationView()
            view.nodeUriInput.value = uri

            view.nodeUriInput.setIsEnabled(true)
            unblockPage()
        }(fatalErrorHandler(_))
    }

    private def updateNavigationView() {
        view.backButton.setIsEnabled(canGoBack)
        view.nextButton.setIsEnabled(canGoNext)
    }

    private def canGoBack = history.nonEmpty && historyPosition > 0

    private def canGoNext = history.nonEmpty && historyPosition < (history.length - 1)
}
