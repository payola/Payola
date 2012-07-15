package cz.payola.web.client.presenters

import cz.payola.web.shared.DataSourceBrowser

class DataSourcePresenter(viewParentElementId: String, val dataSourceId: String, val initialVertexUri: String = "")
    extends GraphPresenter(viewParentElementId)
{
    view.vertexBrowsing += { e =>
        val graph = DataSourceBrowser.getNeighbourhood(dataSourceId, e.vertex.uri)
        view.updateGraph(graph)
    }

    override def initialize() {
        super.initialize()

        val initialGraph = if (initialVertexUri == "") {
            DataSourceBrowser.getInitialGraph(dataSourceId)
        } else {
            DataSourceBrowser.getNeighbourhood(dataSourceId, initialVertexUri)
        }
        view.updateGraph(initialGraph)
    }
}
