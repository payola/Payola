package cz.payola.web.client.graph

import cz.payola.web.client.{Layer, Vector, Point}
import s2js.adapters.js.dom.CanvasRenderingContext2D
import Constants._

class Drawer(val layerEdges: Layer, val layerVertices: Layer, val layerText: Layer)
{
    val layers = List(layerEdges, layerVertices, layerText)

    /*def drawSelectionByRect(origin: Point, direction: Point, colorToUse: Color) {
        layerEdges.context.strokeStyle = colorToUse.toString
        layerEdges.context.lineWidth = SelectLineWidth

        layerEdges.context.rect(scala.math.min(origin.x, direction.x), scala.math.min(origin.y, direction.y),
            scala.math.abs(origin.x - direction.x), scala.math.abs(origin.y - direction.y))

        layerEdges.context.stroke()
    }*/

    def drawGraph(graph: Graph) {
        graph.vertices.foreach { vertex =>
            val color =
                if (vertex.selected) {
                    ColorVertexHigh
                } else if (graph.edges.exists(e =>
                    (e.vertexA.eq(vertex) && e.vertexB.selected) || (e.vertexB.eq(vertex) && e.vertexA.selected))) {
                    ColorVertexMedium
                } else if (graph.containsSelectedVertex) {
                    ColorVertexDefault
                } else {
                    ColorVertexLow
                }

            vertex.draw(layerVertices.context, color)
            vertex.information.draw(layerText.context)
        }

        graph.edges.foreach(_.draw(layerEdges.context))


        /*
        // Need to draw edges first, so that edges would not be drawn "over" vertices.
        graph.getVertices.foreach { (vertex: Vertex) =>
            vertex.neighbours.foreach { neighbourVertex =>
                // If the edge isn't already drawn, then draw it.
                if (vertex.id < neighbourVertex.id) {
                    val edgeColor = if (vertex.selected || neighbourVertex.selected) ColorEdgeSelect else ColorEdge
                    drawEdge(vertex, neighbourVertex, edgeColor)
                }
            }
        }

        val somethingSelected = graph.getVertices.exists(_.selected)
        graph.getVertices.foreach { vertex: Vertex =>
            val neighbourSelected = vertex.neighbours.exists(_.selected);
            if (vertex.selected) {
                drawVertex(vertex, vertex.text, ColorVertexHigh)
            } else if (neighbourSelected) {
                drawVertex(vertex, "", ColorVertexMedium)
            } else if (!somethingSelected) {
                drawVertex(vertex, vertex.text, ColorVertexDefault)
            } else {
                // No neighbour nor this vertex is selected, but something else is.
                drawVertex(vertex, "", ColorVertexLow)
            }
        }*/
    }

    def clear(context: CanvasRenderingContext2D, topLeft: Point, size: Vector) {
        val bottomRight = topLeft + size
        context.clearRect(topLeft.x, topLeft.y, bottomRight.x, bottomRight.y)
    }

    def redraw(graph: Graph) {
        // TODO conditional redrawing of "redraw-required-sections"
        layers.foreach(layer => clear(layer.context, Point.Zero, layer.getSize))
        drawGraph(graph)
    }
}
