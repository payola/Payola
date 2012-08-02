package cz.payola.web.client.presenters.entity.plugins

import scala.collection.mutable
import s2js.adapters.js.html
import cz.payola.web.shared.managers._
import cz.payola.web.client.events.BrowserEventArgs
import cz.payola.web.client.views.VertexEventArgs
import cz.payola.web.client.Presenter
import cz.payola.web.client.presenters.graph.GraphPresenter
import cz.payola.web.client.views.entity.plugins._
import cz.payola.common.ValidationException
import cz.payola.web.client.views.bootstrap.modals.AlertModal

class DataSourceBrowser(
    val viewElement: html.Element,
    val dataSourceId: String,
    val dataSourceName: String,
    val initialVertexUri: String = "")
    extends Presenter
{
    private val view = new DataSourceBrowserView(dataSourceName)

    private val graphPresenter = new GraphPresenter(view.graphViewSpace.htmlElement)

    private var history = mutable.ListBuffer.empty[String]

    private var historyPosition = -1

    def initialize() {
        // Initialize the sub presenters.
        graphPresenter.initialize()

        // Register the event handlers.
        view.backButton.mouseClicked += onBackButtonClicked _
        view.nextButton.mouseClicked += onNextButtonClicked _
        view.goButton.mouseClicked += onGoButtonClicked _
        view.sparqlQueryButton.mouseClicked += onSparqlQueryButtonClicked _
        view.nodeUriInput.keyPressed += onNodeUriKeyPressed _
        graphPresenter.view.vertexBrowsing += onVertexBrowsing _

        view.render(viewElement)

        // If the default URI isn't specified, display the initial graph.
        if (initialVertexUri == "") {
            blockPage("Fetching the initial graph.")
            DataSourceManager.getInitialGraph(dataSourceId) {
                graph =>
                    graphPresenter.view.updateGraph(graph)
                    updateNavigationView()
                    unblockPage()
            }(fatalErrorHandler(_))
        } else {
            addToHistoryAndGo(initialVertexUri)
        }
    }

    private def onVertexBrowsing(e: VertexEventArgs[_]) {
        addToHistoryAndGo(e.vertex.uri)
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
        graphPresenter.view.updateGraph(None)
        addToHistoryAndGo(view.nodeUriInput.value)
        false
    }

    private def onSparqlQueryButtonClicked(e: BrowserEventArgs[_]): Boolean = {
        val modal = new SparqlQueryModal
        modal.confirming += {
            e =>
                modal.block("Executing the SPARQL query.")
                DataSourceManager.executeSparqlQuery(dataSourceId, modal.sparqlQueryInput.value) {
                    g =>
                        modal.unblock()
                        modal.destroy()

                        history = mutable.ListBuffer.empty[String]
                        historyPosition = -1
                        updateNavigationView()

                        graphPresenter.view.updateGraph(g)
                } {
                    e =>
                        modal.unblock()
                        e match {
                            case v: ValidationException => AlertModal.display("Error", v.message)
                            case t => fatalErrorHandler(t)
                        }
                }
                false
        }
        modal.render()
        false
    }

    private def onNodeUriKeyPressed(e: BrowserEventArgs[_]): Boolean = {
        if (e.keyCode == 13) {
            onGoButtonClicked(e)
        } else {
            true
        }
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
        view.nodeUriInput.value = uri
        view.nodeUriInput.setIsEnabled(false)

        blockPage("Fetching the node neighbourhood.")
        DataSourceManager.getNeighbourhood(dataSourceId, uri) {
            graph =>
                graphPresenter.view.updateGraph(graph)
                updateNavigationView()

                view.nodeUriInput.setIsEnabled(true)
                unblockPage()
        }(fatalErrorHandler(_))
    }

    private def updateNavigationView() {
        view.backButton.setIsEnabled(canGoBack)
        view.nextButton.setIsEnabled(canGoNext)
        if (historyPosition < 0) {
            view.nodeUriInput.value = ""
        }
    }

    private def canGoBack = history.nonEmpty && historyPosition > 0

    private def canGoNext = history.nonEmpty && historyPosition < (history.length - 1)
}
