package cz.payola.web.client.presenters.entity

import scala.collection.mutable
import s2js.adapters.js.dom
import s2js.adapters.js.browser._
import cz.payola.web.shared.DataSourceBrowser
import cz.payola.web.client.views.entity._
import cz.payola.web.client.views.graph.PluginSwitchView
import cz.payola.web.client.events.BrowserEventArgs
import cz.payola.web.client.views.VertexEventArgs
import cz.payola.common.entities.plugins.DataSource

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
