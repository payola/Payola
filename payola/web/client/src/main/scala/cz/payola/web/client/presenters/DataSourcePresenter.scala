package cz.payola.web.client.presenters

import cz.payola.web.shared.DataSourceBrowser

class DataSourcePresenter(viewParentElementId: String, val dataSourceId: String)
    extends GraphPresenter(viewParentElementId)
{
    view.vertexBrowsing += { e =>

    }

    override def initialize() {
        super.initialize()
        val initialGraph = DataSourceBrowser.getInitialGraph(dataSourceId)
        view.updateGraph(initialGraph)
    }
}
