package cz.payola.web.client.presenters

import cz.payola.web.shared.DataSourceBrowser
import s2js.adapters.js.browser.window

class DataSourcePresenter(viewParentElementId: String, val dataSourceId: String, val initialVertexUri: String = "")
    extends GraphPresenter(viewParentElementId)
{
    view.vertexBrowsing += { e => fetchNeighbourhood(e.vertex.uri)}

    override def initialize() {
        super.initialize()

        if (initialVertexUri == "") {
            DataSourceBrowser.getInitialGraph(dataSourceId)(view.updateGraph(_))(graphFetchErrorHandler)
        } else {
            fetchNeighbourhood(initialVertexUri)
        }
    }

    private def fetchNeighbourhood(uri: String) {
        view.block()
        DataSourceBrowser.getNeighbourhood(dataSourceId, uri) { graph =>
            view.updateGraph(graph)
            view.unblock()
        }(graphFetchErrorHandler)
    }

    private def graphFetchErrorHandler(t: Throwable) {
        view.updateGraph(None)
        view.unblock()
        // TODO
    }
}
