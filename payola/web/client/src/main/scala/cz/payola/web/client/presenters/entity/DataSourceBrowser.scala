package cz.payola.web.client.presenters.entity

import scala.collection.mutable
import s2js.adapters.js.dom
import cz.payola.web.shared.managers._
import cz.payola.web.client.events.BrowserEventArgs
import cz.payola.web.client.views.VertexEventArgs
import cz.payola.web.client.Presenter
import cz.payola.web.client.presenters.graph.GraphPresenter
import cz.payola.web.client.views.entity.DataSourceView

class DataSourceBrowser(
    val viewElement: dom.Element,
    val dataSourceId: String,
    val dataSourceName: String,
    val initialVertexUri: String = "")
    extends Presenter
{
    private val view = new DataSourceView(dataSourceName)

    private val graphPresenter = new GraphPresenter(view.graphViewSpace.domElement)

    private val history = mutable.ListBuffer.empty[String]

    private var historyPosition = -1

    def initialize() {
        // Initialize the sub presenters.
        graphPresenter.initialize()

        // Register the event handlers.
        view.backButton.mouseClicked += onBackButtonClicked _
        view.nextButton.mouseClicked += onNextButtonClicked _
        view.goButton.mouseClicked += onGoButtonClicked _
        view.nodeUriInput.keyPressed += onNodeUriKeyPressed _
        graphPresenter.view.vertexBrowsing += onVertexBrowsing _

        view.render(viewElement)

        // If the default URI isn't specified, display the initial graph.
        if (initialVertexUri == "") {
            blockPage("Fetching the initial graph.")
            DataSourceManager.getInitialGraph(dataSourceId) { graph =>
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
        DataSourceManager.getNeighbourhood(dataSourceId, uri) { graph =>
            graphPresenter.view.updateGraph(graph)
            updateNavigationView()

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
