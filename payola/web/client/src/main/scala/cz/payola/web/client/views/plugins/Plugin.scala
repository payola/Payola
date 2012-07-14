package cz.payola.web.client.views.plugins

import cz.payola.web.client.views.ComposedComponent
import cz.payola.common.rdf.Graph

/**
  * A visualisation plug-in.
  * @param name Name of the plugin.
  */
abstract class Plugin(val name: String) extends ComposedComponent
{
    /**
      * Updates the current graph of the plugin and re-runs the visualisation.
      * @param graph The graph to add to the current graph.
      */
    def updateGraph(graph: Option[Graph])

    /**
      * Removes the current graph from the plugins memory and resets the visualisation.
      */
    def clear() {
        updateGraph(None)
    }
}
