package cz.payola.web.client.presenters

import cz.payola.web.shared.DatasourceBrowser


class Datasource(elementToDrawIn: String, dataSourceId: String) extends Index(elementToDrawIn)
{
    graph = DatasourceBrowser.getInitialGraph(dataSourceId)
    changePlugin(currentPlugin.get)
}
