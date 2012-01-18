package cz.payola.web.client.views.graph

import cz.payola.web.client.views.Constants._
import collection.mutable.ListBuffer
import cz.payola.web.client.model.graph.{Vertex, Graph}
import cz.payola.web.client.views.{Layer, Point, Vector}
import s2js.adapters.js.dom.{Element, CanvasRenderingContext2D, Canvas}
import s2js.adapters.js.browser._

class GraphView(val graphModel: Graph, val container: Element)
{
    private var selectedVertexCount = 0

    private val vertexViews: ListBuffer[VertexView] = createVertexViews()

    private val edgeViews: ListBuffer[EdgeView] = createEdges()

    private val edgesLayer = createLayer()

    private val verticesLayer = createLayer()

    private val textLayer = createLayer()
    
    private val layers = List(edgesLayer, verticesLayer, textLayer)

    /**
      * Constructs a list of vertexViews based on the graphModel parameter.
      */
    def createVertexViews(): ListBuffer[VertexView] = {
        val graphWidth = 200 //TODO size of the viewed space

        val buffer = ListBuffer[VertexView]()
        var counter = 0
        graphModel.vertices.foreach {vertexModel =>
            val point = Point((counter * 10) % graphWidth, counter / graphWidth)
            buffer += new VertexView(vertexModel, point)
            counter += 1
        }

        buffer
    }

    /**
      * Constructs a list of edgeViews based on the graphModel and verticesView parameters.
      */
    def createEdges(): ListBuffer[EdgeView] = {
        val buffer = ListBuffer[EdgeView]()
        graphModel.edges.foreach {edgeModel =>
            buffer += new EdgeView(edgeModel, findVertexView(edgeModel.origin), findVertexView(edgeModel.destination))
        }

        buffer
    }

    private def findVertexView(vertexModel: Vertex): VertexView = {
        vertexViews.find(_.vertexModel.eq(vertexModel)).get
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
            vertexViews.foreach {vertex =>
                somethingChanged = deselectVertex(vertex) || somethingChanged
            }
        }
        somethingChanged
    }

    def getTouchedVertex(p: Point): Option[VertexView] = {
        vertexViews.find(v => isPointInRect(p, v.position + (VertexSize / -2), v.position + (VertexSize / 2)))
    }

    def isPointInRect(p: Point, topLeft: Point, bottomRight: Point): Boolean = {
        p >= topLeft && p <= bottomRight
    }

    def createLayer(): Layer = {
        val canvas = document.createElement[Canvas]("canvas")
        val context = canvas.getContext[CanvasRenderingContext2D]("2d")
        val layer = new Layer(canvas, context)

        container.appendChild(canvas)
        layer.setSize(Vector(window.innerWidth, window.innerHeight))
        layer
    }

    def draw() {
        vertexViews.foreach { vertexView =>
            val color =
                if (vertexView.selected) {
                    ColorVertexHigh
                } else if (edgeViews.exists(edgeView =>
                    (edgeView.originView.eq(vertexView) && edgeView.destinationView.selected) || 
                    (edgeView.destinationView.eq(vertexView) && edgeView.originView.selected))) {
                    ColorVertexMedium
                } else if (selectedVertexCount > 0) {
                    ColorVertexDefault
                } else {
                    ColorVertexLow
                }

            vertexView.draw(verticesLayer.context, color)
            vertexView.information.draw(textLayer.context)
        }

        edgeViews.foreach(_.draw(edgesLayer.context))
    }

    def clear(context: CanvasRenderingContext2D, topLeft: Point, size: Vector) {
        val bottomRight = topLeft + size
        context.clearRect(topLeft.x, topLeft.y, bottomRight.x, bottomRight.y)
    }

    def redraw() {
        layers.foreach(layer => clear(layer.context, Point.Zero, layer.getSize))
        draw()
    }
}
