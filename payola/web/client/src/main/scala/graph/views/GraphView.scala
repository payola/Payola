package cz.payola.web.client.graph.views

import cz.payola.web.client.graph.Constants._
import cz.payola.web.client.Point
import cz.payola.web.client.graph.model.Graph

class GraphView(val graphModel: Graph, var vertices: List[VertexView], var edges: List[EdgeView])
{
    private var selectedVertexCount = 0

    def init() {
        vertices = createVertices(graphModel)
        edges = createEdges(graphModel, vertices)
    }

    /**
      * Constructs a list of vertexViews based on the graphModel parameter.
      */
    def createVertices(graphModel: Graph): List[VertexView] = {
        val graphWidth = 200 //TODO size of the viewed space

        var verticesView = List[VertexView](/*new VertexView(0, Point.Zero, "plk")*/) //TODO add mutable list to s2js
        var counter = 0
        graphModel.vertices.foreach{ vertexModel =>
            val point = Point((counter*10) % graphWidth, counter/graphWidth)
            val vertexView = new VertexView(counter, point, vertexModel.uri)

            verticesView = verticesView ::: List[VertexView](vertexView)
            counter += 1
        }

        verticesView
    }

    /**
      * Constructs a list of edgeViews based on the graphModel and verticesView parameters.
      */
    def createEdges(graphModel: Graph, verticesView: List[VertexView]): List[EdgeView] = {
        var edgesView = List[EdgeView](/*new EdgeView(null, null, "plk")*/) //TODO add mutable list to s2js
        graphModel.edges.foreach{ edgeModel =>

            edgesView = edgesView ::: List[EdgeView](new EdgeView(
                lookup(edgeModel.from.uri), lookup(edgeModel.to.uri), edgeModel.uri))
        }

        edgesView
    }
    /**
      * DANGEROUS!!! requires the vertices container to be already constructed
      * Searches in the vertices container and returns a vertex with first appearance of
      * the uri in its information.text value.
      */
    def lookup(uri: String): VertexView = {
        //TODO remove when init() is improved
        var result = vertices.head
        vertices.foreach{ vertex =>
            if(vertex.information.text == uri) {
                result = vertex
            }
        }
        result
    }

    def setVertexSelection(vertex: VertexView, selected: Boolean): Boolean = {
        if (vertex.selected != selected) {
            selectedVertexCount += (if (selected) 1 else -1)
            vertex.selected = selected
            true
        } else {
            false
        }
    }

    def selectVertex(vertex: VertexView): Boolean = {
        setVertexSelection(vertex, true)
    }

    def deselectVertex(vertex: VertexView): Boolean = {
        setVertexSelection(vertex, false)
    }

    def invertVertexSelection(vertex: VertexView): Boolean = {
        setVertexSelection(vertex, !vertex.selected)
    }

    def deselectAll(graph: GraphView): Boolean = {
        var somethingChanged = false
        if (selectedVertexCount > 0) {
            vertices.foreach{ vertex =>
                somethingChanged = deselectVertex(vertex) || somethingChanged
            }
        }
        somethingChanged
    }

    def containsSelectedVertex: Boolean = {
        selectedVertexCount > 0
    }

    def getTouchedVertex(p: Point): Option[VertexView] = {
        vertices.find(v => isPointInRect(p, v.position + (VertexSize / -2), v.position + (VertexSize / 2)))
    }

    def isPointInRect(p: Point, topLeft: Point, bottomRight: Point): Boolean = {
        p >= topLeft && p <= bottomRight
    }
}
