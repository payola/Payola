package cz.payola.web.client.views

import cz.payola.common.rdf._
import cz.payola.web.client.View
import cz.payola.web.client.events._

class VertexEventArgs[+A](target: A, val vertex: IdentifiedVertex) extends EventArgs[A](target)

/**
  * A view that displays RDF graphs.
  */
abstract class GraphView
{
    /**
      * Triggered when a vertex is selected.
      */
    val vertexSelected = new UnitEvent[this.type, VertexEventArgs[this.type]]

    /**
      * Triggered when the vertex is selected as a start of browsing.
      */
    val vertexBrowsing = new UnitEvent[this.type, VertexEventArgs[this.type]]

    /**
      * Triggered when the vertex is selected as a start of browsing in a particular data source.
      */
    val vertexBrowsingDataSource = new UnitEvent[this.type, VertexEventArgs[this.type]]

    /**
      * Updates the current graph of the view and re-runs the visualisation.
      * @param graph The graph to add to the current graph.
      */
    def updateGraph(graph: Option[Graph])

    /**
      * Removes the current graph from the view memory and resets the visualisation.
      */
    def clear() {
        updateGraph(None)
    }
}
