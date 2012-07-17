package cz.payola.web.client.presenters

import s2js.adapters.js.dom
import cz.payola.web.shared.DataSourceBrowser
import cz.payola.web.client.views.entity.DataSourceView
import cz.payola.web.client.views.graph.PluginSwitchView

class DataSourcePresenter(
    viewElement: dom.Element,
    val dataSourceId: String,
    val dataSourceName: String,
    val initialVertexUri: String = "")
{
    val view = new DataSourceView(dataSourceName)

    val graphView = new PluginSwitchView

    graphView.vertexBrowsing += { e => fetchNeighbourhood(e.vertex.uri)}

    def initialize() {
        // Compose the views and render the main view.
        graphView.render(view.graphViewSpace.domElement)
        view.render(viewElement)

        if (initialVertexUri == "") {
            DataSourceBrowser.getInitialGraph(dataSourceId)(graphView.updateGraph(_)) { error =>
                graphView.updateGraph(None)
                view.unblock()
            }
        } else {
            fetchNeighbourhood(initialVertexUri)
        }
    }

    private def fetchNeighbourhood(uri: String) {
        view.block()
        DataSourceBrowser.getNeighbourhood(dataSourceId, uri) { graph =>
            graphView.updateGraph(graph)
            view.unblock()
        } { error =>
            graphView.updateGraph(None)
            view.unblock()
        }
    }
}
