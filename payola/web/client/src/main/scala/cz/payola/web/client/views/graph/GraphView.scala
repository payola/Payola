package cz.payola.web.client.views.graph

import cz.payola.web.client.views.Constants._
import collection.mutable.ListBuffer
import cz.payola.web.client.model.graph.{Vertex, Graph}
import s2js.adapters.js.dom.{Element, CanvasRenderingContext2D, Canvas}
import s2js.adapters.js.browser._
import cz.payola.web.client.views.{Color, Layer, Point, Vector}

class GraphView(val graphModel: Graph, val container: Element) extends View
/* extends View...the draw routine does not require context, so implementing trait View may be counterproductive*/ {
    private val edgesLayer = createLayer()

    private val edgesTextLayer = createLayer()

    private val verticesLayer = createLayer()

    private val verticesTextLayer = createLayer()

    private val blankLayer = createLayer()

    private val layers = List(edgesLayer, edgesTextLayer, verticesLayer, verticesTextLayer, blankLayer)

    private val controlsLayer = new Controls(this, blankLayer)

    val vertexViews: ListBuffer[VertexView] = createVertexViews()

    val edgeViews: ListBuffer[EdgeView] = createEdges()

    def initControls() {
        controlsLayer.init()
    }
    /**
      * Constructs a list of vertexViews based on the graphModel parameter.
      */
    def createVertexViews(): ListBuffer[VertexView] = {
        val graphWidth = verticesLayer.getSize.x

        val buffer = ListBuffer[VertexView]()
        var counter = 0
        val spaceBetweenVertices = 50
        graphModel.vertices.foreach { vertexModel =>
            val point = Point((counter * spaceBetweenVertices) % graphWidth + 20/*correction*/,
                scala.math.floor(counter * spaceBetweenVertices / graphWidth)*spaceBetweenVertices + 20/*correction*/)

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

    def createLayer(): Layer = {
        val canvas = document.createElement[Canvas]("canvas")
        val context = canvas.getContext[CanvasRenderingContext2D]("2d")
        val layer = new Layer(canvas, context)

        container.appendChild(canvas)
        layer.setSize(Vector(400, 500)) //TODO take it from the "created element"
        layer
    }

    def draw(context: CanvasRenderingContext2D, color: Color, position: Point) {

        val positionCorrection = if (position != null) {
            position.toVector
        } else {
            Point.Zero.toVector
        }

        vertexViews.foreach { vertexView =>
            val colorToUse =
                if (color != null) {
                    color
                } else if (vertexView.selected) {
                    ColorVertexHigh
                } else if (edgeViews.exists(edgeView =>
                    (edgeView.originView.eq(vertexView) && edgeView.destinationView.selected) || 
                    (edgeView.destinationView.eq(vertexView) && edgeView.originView.selected))) {
                    ColorVertexMedium
                } else if (controlsLayer.selectedCount == 0) {
                    ColorVertexDefault
                } else {
                    ColorVertexLow
                }

            vertexView.draw(verticesLayer.context, colorToUse, position)
            vertexView.information.draw(verticesTextLayer.context, color,
                LocationDescriptor.getVertexInformationPosition(vertexView.position) + positionCorrection)
        }

        edgeViews.foreach { edgeView =>
            edgeView.draw(edgesLayer.context, color, position)
            edgeView.information.draw(edgesTextLayer.context, color,
                LocationDescriptor.getEdgeInformationPosition(
                    edgeView.originView.position, edgeView.destinationView.position) + positionCorrection)
        }
    }

    def clear(context: CanvasRenderingContext2D, topLeft: Point, size: Vector) {
        val bottomRight = topLeft + size
        context.clearRect(topLeft.x, topLeft.y, bottomRight.x, bottomRight.y)
    }

    def redraw() {
        layers.foreach(layer => clear(layer.context, Point.Zero, layer.getSize))
        draw(null, null, null)
    }
}
