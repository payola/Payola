package cz.payola.web.client.views.plugins.visual

import animation.Animation
import cz.payola.web.client.views.plugins.Plugin
import graph.{InformationView, VertexView, GraphView}
import s2js.adapters.js.dom.Element
import s2js.adapters.goog.events._
import cz.payola.common.rdf.Graph
import s2js.adapters.js.browser.document
import s2js.adapters.goog._
import collection.mutable.ListBuffer
import settings.components.visualsetup.VisualSetup
import s2js.adapters.js.browser.window

/**
  * Representation of visual based output drawing plugin
  */
abstract class VisualPlugin(settings: VisualSetup) extends Plugin
{

    /**
      * Variable helping during movement of vertices. Contains position where the movement of a vertex tarted.
      */
    private var moveStart: Option[Point] = None

    /**
      * Contained graph in visualisation packing.
      */
    var graphView: Option[GraphView] = None

    var animation: Option[Animation[VertexView]] = None

    var animationNumber = -1

    def init(container: Element) {

        graphView = Some(new GraphView(container, settings))

        listen[BrowserEvent](graphView.get.controlsLayer.canvas, EventType.MOUSEDOWN, onMouseDown _)
        listen[BrowserEvent](graphView.get.controlsLayer.canvas, EventType.MOUSEMOVE, onMouseMove _)
        listen[BrowserEvent](graphView.get.controlsLayer.canvas, EventType.MOUSEUP, onMouseUp _)
    }
    
    def update(graph: Graph) {
        graphView.get.update(graph)
    }

    def clean() {
        if(graphView.isDefined) {
            graphView.get.clean()
            graphView = None
            moveStart = None
        }
    }

    protected def redrawQuick() { //TODO rename or move somewhere else
        if(!graphView.isEmpty) {
            graphView.get.redraw(RedrawOperation.Animation)
        }
    }
    
    def redraw() {
        if(!graphView.isEmpty) {
            graphView.get.redrawAll()
        }
    }

    def redrawSelection() {
        if(graphView.isDefined) {
            graphView.get.redraw(RedrawOperation.Selection)
        }
    }

    /**
      * Description of mouse-button-down event. Is called from the layer (canvas) binded to it in the initialization.
      * @param event
      */
    private def onMouseDown(event: BrowserEvent) {

        val position = getPosition(event)

        var resultedAnimation: Option[Animation[ListBuffer[InformationView]]] = None

        val vertex = graphView.get.getTouchedVertex(position)

        if (vertex.isDefined) { // Mouse down near a vertex.
            if (event.shiftKey) { //change selection of the pressed one
                graphView.get.invertVertexSelection(vertex.get)
                if(vertex.get.selected) {
                    val toAnimate = ListBuffer[InformationView]()
                    if(vertex.get.information.isDefined) {
                        toAnimate += vertex.get.information.get
                    }
                    toAnimate ++= getEdgesInformations(vertex.get)
                    resultedAnimation = Some(
                        new Animation(Animation.showText, toAnimate, None, redrawSelection, redrawSelection, Some(0)))
                } else {
                    redrawSelection()
                }
            } else { //deselect all and select the pressed one
                if (!vertex.get.selected) {
                    graphView.get.deselectAll()
                }
                moveStart = Some(position)
                if(graphView.get.selectVertex(vertex.get)) {

                    val toAnimate = ListBuffer[InformationView]()
                    if(vertex.get.information.isDefined) {
                        toAnimate += vertex.get.information.get
                    }
                    toAnimate ++= getEdgesInformations(vertex.get)
                    resultedAnimation = Some(new Animation(Animation.showText, toAnimate, None,
                        redrawSelection, redrawSelection, Some(0)))
                } else {
                    redrawSelection()
                }
            }

        } else { // Mouse down somewhere between-vertex space.
            if (!event.shiftKey) { //deselect all
                graphView.get.deselectAll()
                redrawSelection()
            }
        }

        if(resultedAnimation.isDefined) {
            resultedAnimation.get.run()
        }

    }

    /**
     * goes through all edges of the vertex and returns informations of those,
     * which have selected both of their vertices
     * @param vertexView
     * @return
     */
    private def getEdgesInformations(vertexView: VertexView): ListBuffer[InformationView] = {
        val result = ListBuffer[InformationView]()
        vertexView.edges.foreach{ edgeView =>
            if(edgeView.originView.selected && edgeView.destinationView.selected) {
                result += edgeView.information
            }
        }
        result
    }

    /**
      * Description of mouse-move event. Is called from the layer (canvas) binded to it in the initialization.
      * @param event
      */
    private def onMouseMove(event: BrowserEvent) {
        if (moveStart.isDefined) { //mouse drag
            Animation.clearCurrentTimeout()
            val end = getPosition(event)
            val difference = end - moveStart.get

            graphView.get.moveAllSelectedVertices(difference)

            moveStart = Some(end)
            graphView.get.redraw(RedrawOperation.Movement)
        }
    }

    /**
      * Description of mouse-button-up event. Is called from the layer (canvas) binded to it in the initialization.
      * @param event
      */
    private def onMouseUp(event: BrowserEvent) {
        moveStart = None
    }

    private def getPosition(event: BrowserEvent): Point = {

        val positionCorrection =
            Vector(graphView.get.controlsLayer.canvas.offsetLeft, graphView.get.controlsLayer.canvas.offsetTop)

        if (typeOf(event.pageX) != "undefined" && typeOf(event.pageY) != "undefined") {
            Point(event.pageX, event.pageY) + positionCorrection
        }
        else {
            Point(event.clientX + document.body.scrollLeft + document.documentElement.scrollLeft,
                event.clientY + document.body.scrollTop + document.documentElement.scrollTop) +
            positionCorrection
        }
    }
}
