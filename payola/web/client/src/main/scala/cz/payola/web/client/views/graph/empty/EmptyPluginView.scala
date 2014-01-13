package cz.payola.web.client.views.graph.empty

import cz.payola.web.client.views.graph.PluginView
import cz.payola.web.client.views.elements._
import cz.payola.common.rdf.Graph

/**
 * A plugin view to show after analysis is finished and no pluginView is selected yet.
 */
class EmptyPluginView extends PluginView[Graph]("Empty", None)
{
    def createSubViews = List(new Div(List(new Text("Select a visualization plugin...")),
        "plugin-message large").setAttribute("style", "height: 300px;"))
}
