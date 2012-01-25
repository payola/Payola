package cz.payola.web.client.views.graph

import cz.payola.web.client.views.Constants._
import collection.mutable.ListBuffer
import cz.payola.web.client.model.graph.{Vertex, Graph}
import s2js.adapters.js.dom.{Canvas, Element, CanvasRenderingContext2D}
import s2js.adapters.js.browser.document
import cz.payola.web.client.views.{Vector, Layer, Color, Point}

class GraphView(val graphModel: Graph, val container: Element) extends View {

    /*The order in which are layers created determines their "z coordinate"
    (first created layer is on the bottom and last created one covers all the others).*/
    private val edgesDeselectedLayer = createLayer(container)
    private val edgesDeselectedTextLayer = createLayer(container)

    private val edgesSelectedLayer = createLayer(container)
    private val edgesSelectedTextLayer = createLayer(container)

    private val verticesDeselectedLayer = createLayer(container)
    private val verticesDeselectedTextLayer = createLayer(container)

    private val verticesSelectedLayer = createLayer(container)
    private val verticesSelectedTextLayer = createLayer(container)

    private val topBlankLayer = createLayer(container)

    private val layers = List(
        edgesDeselectedLayer, edgesDeselectedTextLayer, edgesSelectedLayer, edgesSelectedTextLayer,
        verticesDeselectedLayer, verticesDeselectedTextLayer, verticesSelectedLayer, verticesSelectedTextLayer,
        topBlankLayer)

    private val controlsLayer = new Controls(this, topBlankLayer)

    val vertexViews = createVertexViews()

    val edgeViews = createEdges()

    def initControls() {
        controlsLayer.init()
    }
    /**
      * Constructs a list of vertexViews based on the graphModel parameter.
      */
    def createVertexViews(): ListBuffer[VertexView] = {
        //TODO write here some fine algorithm
        val graphWidth = 500//verticesDeselectedLayer.getSize.x

        val buffer = ListBuffer[VertexView]()
        var counter = 0
        val spaceBetweenVertices = 80
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
            if (vertexView.selected) {
                if (verticesSelectedLayer.cleared) {
                    vertexView.draw(verticesSelectedLayer.context, colorToUse, position)
                }
                if (verticesSelectedTextLayer.cleared) {
                    vertexView.information.draw(verticesSelectedTextLayer.context, color,
                        LocationDescriptor.getVertexInformationPosition(vertexView.position) + positionCorrection)
                }
            } else {
                if (verticesDeselectedLayer.cleared) {
                    vertexView.draw(verticesDeselectedLayer.context, colorToUse, position)
                }
                if (verticesDeselectedTextLayer.cleared) {
                    vertexView.information.draw(verticesDeselectedTextLayer.context, color,
                        LocationDescriptor.getVertexInformationPosition(vertexView.position) + positionCorrection)
                }
            }
        }

        edgeViews.foreach { edgeView =>
            
            val positionToUse = LocationDescriptor.getEdgeInformationPosition(
                edgeView.originView.position, edgeView.destinationView.position) + positionCorrection
            val colorToUse = if (color != null) {
                color
            } else if (edgeView.isSelected) {
                ColorEdgeSelect
            } else {
                ColorEdge
            }

            if (edgeView.isSelected) {
                if (edgesSelectedLayer.cleared) {
                    edgeView.draw(edgesSelectedLayer.context, color, position)
                }
                if (edgesSelectedTextLayer.cleared) {
                    edgeView.information.draw(edgesSelectedTextLayer.context, colorToUse, positionToUse)
                }
            } else {
                if (edgesDeselectedLayer.cleared) {
                    edgeView.draw(edgesDeselectedLayer.context, color, position)
                }
                if (edgesDeselectedTextLayer.cleared) {
                    edgeView.information.draw(edgesDeselectedTextLayer.context, colorToUse, positionToUse)
                }
            }
        }

        layers.foreach(layer => layer.cleared = false)
    }

    def redraw(graphOperation: Int) {
        graphOperation match {
            case RedrawOperation.Movement =>
                clear(edgesSelectedLayer.context, Point.Zero, edgesSelectedLayer.getSize)
                edgesSelectedLayer.cleared = true
                clear(edgesSelectedTextLayer.context, Point.Zero, edgesSelectedTextLayer.getSize)
                edgesSelectedTextLayer.cleared = true
                clear(verticesSelectedLayer.context, Point.Zero, verticesSelectedLayer.getSize)
                verticesSelectedLayer.cleared = true
                clear(verticesSelectedTextLayer.context, Point.Zero, verticesSelectedTextLayer.getSize)
                verticesSelectedTextLayer.cleared = true

                draw(null, null, null)
            case RedrawOperation.Selection =>
                redrawAll()
            case _ =>
                redrawAll()
        }
    }
    def redrawAll() {
        layers.foreach { layer =>
            clear(layer.context, Point.Zero, layer.getSize)
            layer.cleared = true
        }
        draw(null, null, null)
    }

    protected def clear(context: CanvasRenderingContext2D, topLeft: Point, size: Vector) {
        //TODO move to View trait, when GraphView extends View works
        val bottomRight = topLeft + size
        context.clearRect(topLeft.x, topLeft.y, bottomRight.x, bottomRight.y)
    }

    protected def createLayer(container: Element): Layer = {
        //TODO move to View trait, when GraphView extends View works
        val canvas = document.createElement[Canvas]("canvas")
        val context = canvas.getContext[CanvasRenderingContext2D]("2d")
        val layer = new Layer(canvas, context)

        container.appendChild(canvas)
        layer.setSize(Vector(600, 500)) //TODO take it from the "created element"
        layer
    }
}
