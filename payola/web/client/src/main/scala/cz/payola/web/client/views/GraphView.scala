package cz.payola.web.client.views

import cz.payola.common.rdf._
import cz.payola.web.client.events._
import cz.payola.common.entities.settings.OntologyCustomization

class VertexEventArgs[+A](target: A, val vertex: IdentifiedVertex) extends EventArgs[A](target)

/**
 * A view that displays RDF graphs.
 */
abstract class GraphView
{
    /**The graph that is currently being visualised. */
    protected var currentGraph: Option[Graph] = None

    /**The ontology customization that is currently used during visualisation. */
    protected var currentCustomization: Option[OntologyCustomization] = None

    /**Triggered when a vertex is selected. */
    val vertexSelected = new UnitEvent[this.type, VertexEventArgs[this.type]]

    /**Triggered when the vertex is selected as a start of browsing. */
    val vertexBrowsing = new UnitEvent[this.type, VertexEventArgs[this.type]]

    /**Triggered when the vertex is selected as a start of browsing in a particular data source. */
    val vertexBrowsingDataSource = new UnitEvent[this.type, VertexEventArgs[this.type]]

    /**
     * Updates both the graph that should be visualized and the customization that should be used.
     * @param graph The graph to visualize.
     * @param customization The ontology customization that should be used during visualization.
     */
    def update(graph: Option[Graph], customization: Option[OntologyCustomization]) {
        updateOntologyCustomization(customization)
        updateGraph(graph)
    }

    /**
     * Updates the current graph of the view and re-runs the visualisation.
     * @param graph The graph to add to the current graph.
     */
    def updateGraph(graph: Option[Graph]) {
        currentGraph = graph
    }

    /**
     * Updates the ontology customization that should be used during graph visualisation and re-runs the visualisation.
     * @param customization The ontology customization that should be used.
     */
    def updateOntologyCustomization(customization: Option[OntologyCustomization]) {
        currentCustomization = customization
    }

    /**
     * Removes the current graph from the view memory and resets the visualisation.
     */
    def clear() {
        updateGraph(None)
    }
}
