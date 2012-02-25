package cz.payola.web.client.views.graph

import algorithms.circlePath.CirclePathModel
import algorithms.gravity.GravityModel
import algorithms.pathLength.PathLengthModel
import cz.payola.web.client.views.Constants._
import collection.mutable.ListBuffer
import s2js.adapters.js.dom.{Element, CanvasRenderingContext2D}
import cz.payola.web.client.views.{Color, Point}
import cz.payola.common.rdf.{Vertex, Graph}

class GraphView(val graphModel: Graph, val container: Element) extends View
{
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

    def init() {
        controlsLayer.init()
        val model = new CirclePathModel()
        model.perform(vertexViews, edgeViews)
    }

    /**
      * Constructs a list of vertexViews based on the graphModel parameter.
      */
    def createVertexViews(): ListBuffer[VertexView] = {
        val buffer = ListBuffer[VertexView]()
        var counter = 0

        graphModel.vertices.foreach {vertexModel =>

            buffer += new VertexView(vertexModel, Point(0, 0))
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

        vertexViews.foreach {vertexView: VertexView =>
            vertexView.edges = getEdgesOfVertex(vertexView, buffer)
        }

        buffer
    }

    def getEdgesOfVertex(vertexView: VertexView, _edgeViews: ListBuffer[EdgeView]): ListBuffer[EdgeView] = {
        var edgeViewsBuffer = ListBuffer[EdgeView]()
        var i = 0
        _edgeViews.foreach {_edgeView: EdgeView =>
            if ((_edgeView.originView.vertexModel eq vertexView.vertexModel) ||
                (_edgeView.destinationView.vertexModel eq vertexView.vertexModel)) {

                edgeViewsBuffer += _edgeView
                i += 1
            }
        }
        edgeViewsBuffer
    }

    private def findVertexView(vertexModel: Vertex): VertexView = {
        vertexViews.find(_.vertexModel.eq(vertexModel)).get
    }

    def draw(context: CanvasRenderingContext2D, color: Option[Color], position: Option[Point]) {
        val positionCorrection = position match {
            case None => Point.Zero
            case _ => position.get
        }

        vertexViews.foreach {vertexView =>

            var colorToUseVertex = Color.Black
            var colorToUseText = Color.Black
            if (color != None) {
                colorToUseVertex = color.get
                colorToUseText = color.get
            } else if (vertexView.selected) {
                colorToUseVertex = ColorVertexHigh
                colorToUseText = ColorTextHigh
            } else if (edgeViews.exists(edgeView =>
                (edgeView.originView.eq(vertexView) && edgeView.destinationView.selected) ||
                    (edgeView.destinationView.eq(vertexView) && edgeView.originView.selected))) {
                colorToUseVertex = ColorVertexMedium
                colorToUseText = ColorTextHigh
            } else if (controlsLayer.selectedCount == 0) {
                colorToUseVertex = ColorVertexDefault
                colorToUseText = ColorTextHigh
            } else {
                colorToUseVertex = ColorVertexLow
                colorToUseText = Color.Transparent
            }

            if (vertexView.selected) {
                if (verticesSelectedLayer.cleared) {
                    vertexView.draw(verticesSelectedLayer.context, Some(colorToUseVertex), Some(positionCorrection))
                }
                if (verticesSelectedTextLayer.cleared && vertexView.information != None) {
                    vertexView.information.get.draw(verticesSelectedTextLayer.context, Some(colorToUseText),
                        Some(LocationDescriptor.getVertexInformationPosition(vertexView.position) +
                            positionCorrection.toVector))
                }
            } else {
                if (verticesDeselectedLayer.cleared) {
                    vertexView.draw(verticesDeselectedLayer.context, Some(colorToUseVertex), Some(positionCorrection))
                }
                if (verticesDeselectedTextLayer.cleared && vertexView.information != None) {
                    vertexView.information.get.draw(verticesDeselectedTextLayer.context, Some(colorToUseText),
                        Some(LocationDescriptor.getVertexInformationPosition(vertexView.position) +
                            positionCorrection.toVector))
                }
            }
        }

        edgeViews.foreach {edgeView =>

            val positionToUse = LocationDescriptor.getEdgeInformationPosition(
                edgeView.originView.position, edgeView.destinationView.position) + positionCorrection.toVector
            val colorToUseEdge = if (color != None) {
                color.get
            } else if (edgeView.isSelected) {
                ColorEdgeSelect
            } else {
                ColorEdge
            }
            val colorToUseText = if (color != None) {
                color.get
            } else if (edgeView.isSelected) {
                ColorTextHigh
            } else {
                ColorText
            }


            if(edgeView.isSelected) {
                edgeView.information.setSelectedForDrawing()
            }

            if (edgeView.isSelected) {
                if (edgesSelectedLayer.cleared) {
                    edgeView.draw(edgesSelectedLayer.context, Some(colorToUseEdge), Some(positionCorrection))
                }
                if (edgesSelectedTextLayer.cleared) {
                    edgeView.information.draw(edgesSelectedTextLayer.context, Some(colorToUseText), Some(positionToUse))
                }
            } else {
                if (edgesDeselectedLayer.cleared) {
                    edgeView.draw(edgesDeselectedLayer.context, Some(colorToUseEdge), Some(positionCorrection))
                }
                if (edgesDeselectedTextLayer.cleared) {
                    edgeView.information.draw(edgesDeselectedTextLayer.context, Some(colorToUseText), Some(positionToUse))
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

                draw(null, None, None)
                //^because elements are drawn into separate layers, redraw(..) does not know to which context to draw
            case RedrawOperation.Selection =>
                redrawAll()
            case _ =>
                redrawAll()
        }
    }

    def redrawAll() {
        layers.foreach {layer =>
            clear(layer.context, Point.Zero, layer.getSize)
            layer.cleared = true
        }
        draw(null, None, None)
        //^because elements are drawn into separate layers, redraw(..) does not know to which context to draw
    }
}
