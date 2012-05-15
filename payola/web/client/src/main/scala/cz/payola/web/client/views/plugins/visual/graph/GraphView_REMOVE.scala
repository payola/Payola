package cz.payola.web.client.views.plugins.visual.graph

import collection.mutable.ListBuffer
import cz.payola.web.client.views.plugins.visual.Point
import cz.payola.common.rdf.{Vertex, Graph}

class GraphView_REMOVE /*extends View*/ {
    /*val components = ListBuffer[Component]()

    /**
     * Replaces the current graph with new one and marks all new vertices as selected.
     * @param graph to replace the current graph representation with
     */
    def update(graph: Graph) {

        val vertexViewsCache = vertexViews
        val newVertexViews = createVertexViews(graph)
        val oldVertexViews =
            if(vertexViews.isEmpty) { ListBuffer[VertexView]() } else { vertexViews.diff(newVertexViews) }

        oldVertexViews.foreach{ oldVertexView =>
            oldVertexView.increaseCurrentAge()
        }

        val oldFilteredVertexViews = oldVertexViews.filter(_.getCurrentAge <= vertexHighestAge)

        newVertexViews.foreach{ newVertexView =>
            newVertexView.resetCurrentAge()
        }

        newVertexViews ++= oldFilteredVertexViews
        vertexViews = newVertexViews


        val oldFilteredEdges =
            if(edgeViews.isEmpty) { ListBuffer[EdgeView]() } else {
                edgeViews.filter(oldEdge => (oldEdge.destinationView.getCurrentAge <= vertexHighestAge)
                    || (oldEdge.originView.getCurrentAge <= vertexHighestAge))
            }
        //TODO how to solve this?? the server may not send all edges, that are in the database
        edgeViews = createEdgeViews(graph, oldFilteredEdges, vertexViews)
        if(!vertexViews.isEmpty && !vertexViewsCache.isEmpty) {
            vertexViews.diff(vertexViewsCache).foreach{ vertexView =>
                setVertexSelection(vertexView, true)
            }
        } else {
            selectVertex(vertexViews.head)
        }
    }

    /**
     * Empty graph indication function.
     * @return true if no vertices are present in this.vertexViews variable
     */
    def isEmpty: Boolean = {
        var result = false
        components.foreach{ component =>
            result = result || component.isEmpty
        }
    }

    /**
     * Constructs a list of vertexViews based on the _graphModel parameter.
     * @param _graphModel to build from
     * @return container with packed Vertex objects in VertexView objects
     */
    private def createVertexViews(_graphModel: Graph): ListBuffer[VertexView] = {
        val buffer = ListBuffer[VertexView]()
        var counter = 0

        _graphModel.vertices.foreach {vertexModel =>

            buffer += new VertexView(vertexModel, Point(300, 300), settings.vertexModel) //TODO should be center of the canvas or something like that
            counter += 1
        }

        buffer
    }

    /**
     * Constructs a list of edgeViews based on the _graphModel and verticesView variables.
     * Also modifies the vertexViews and sets the constructed edges to their vertexView.edges
     * attributes.
     * @param newGraphModel to build from
     * @param oldEdges edges from the previous graph, that should appear in the updated version
     * @param _vertexViews list of vertexViews in which to search for vertexViews,
     * that are supposed to be connected by the created edgeViews
     * @return container with packed
     */
    private def createEdgeViews(newGraphModel: Graph, oldEdges: ListBuffer[EdgeView], _vertexViews: ListBuffer[VertexView]):
    ListBuffer[EdgeView] = {

        val buffer = ListBuffer[EdgeView]()
        if(_vertexViews.length != 0) {
            newGraphModel.edges.foreach {edgeModel =>
                buffer += new EdgeView(edgeModel, findVertexView(edgeModel.origin),
                    findVertexView(edgeModel.destination), settings.edgesModel)
            }

            _vertexViews.foreach {vertexView: VertexView =>
                vertexView.edges = getEdgesOfVertex(vertexView, buffer ++ oldEdges)
            }
        }

        buffer ++ oldEdges
    }

    /**
     * Searches for all edges in the _edgeViews parameter, that have the vertexView parameter as its origin or
     * destination and returns all these edges in a container.
     * @param vertexView to searche the edges container for
     * @param _edgeViews container of edges to search in
     * @return container fith found edges
     */
    private def getEdgesOfVertex(vertexView: VertexView, _edgeViews: ListBuffer[EdgeView]): ListBuffer[EdgeView] = {

        _edgeViews.filter { _edgeView: EdgeView =>
            ((_edgeView.originView.vertexModel eq vertexView.vertexModel) ||
                (_edgeView.destinationView.vertexModel eq vertexView.vertexModel))
        }
    }

    /**
     * Finds a vertexView that is a representation of the vertexModel object.
     * @param vertexModel to be searched
     * @return found graphica; representation of the input vertex from the model
     */
    private def findVertexView(vertexModel: Vertex): VertexView = {
        vertexViews.find(_.vertexModel.eq(vertexModel)).get
    }*/
}
