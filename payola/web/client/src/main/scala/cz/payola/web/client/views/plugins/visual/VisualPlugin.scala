package cz.payola.web.client.views.plugins.visual

import animation.Animation
import cz.payola.web.client.views.plugins.Plugin
import graph.{InformationView, VertexView, GraphView}
import s2js.adapters.js.dom.Element
import collection.mutable.ListBuffer
import settings.components.visualsetup.VisualSetup
import s2js.adapters.js.browser.document
import cz.payola.web.client.mvvm.element.CanvasPack
import cz.payola.web.client.events._
import cz.payola.common.rdf._
import s2js.adapters.js.browser.window

/**
  * Representation of visual based output drawing plugin
  */
abstract class VisualPlugin(settings: VisualSetup) extends Plugin
{

    private var mousePressedVertex = false
    private var mouseDragged = false
    private var mouseDownPosition = Point(0, 0)

    val vertexUpdate = new VertexUpdateEvent

    /**
      * Contained graph in visualisation packing.
      */
    var graphView: Option[GraphView] = None

    def init(container: Element) {

        graphView = Some(new GraphView(container, settings))

        graphView.get.canvasPack.mouseDown += { event => //selection
            mouseDragged = false
            mouseDownPosition = getPosition(event)
            onMouseDown(event)

            false
        }

        graphView.get.canvasPack.mouseUp += {event => //deselect all
            onMouseUp(event)

            false
        }

        graphView.get.canvasPack.mouseDragged += { event => //vertices move
            mouseDragged = true
            onMouseDrag(event)
            false
        }

        graphView.get.canvasPack.mouseDblClicked += { event => //update graph
            val vertex = graphView.get.getTouchedVertex(getPosition(event))
            if(vertex.isDefined) {
                graphView.get.selectVertex(vertex.get)
                val eventArgs = new VertexUpdateEventArgs(vertex.get.vertexModel)
                vertexUpdate.trigger(eventArgs)
            }
            false
        }

        graphView.get.canvasPack.mouseWheel += { event => //zoom
            onMouseWheel(event)
            true
        }

        graphView.get.canvasPack.windowResize += { event => //fitting canvas on window resize
            graphView.get.fitCanvas()
            redraw()
            true
        }
    }

    def update(graph: Graph) {
        graphView.get.update(graph)
    }

    def clean() {
        if(graphView.isDefined) {
            graphView.get.clean()
            graphView = None
            mouseDragged = false
            mousePressedVertex = false
            mouseDownPosition = Point(0, 0)
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

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //mouse event handler routines///////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
      * Description of mouse-button-down event. Is called from the layer (canvas) binded to it in the initialization.
      */
    private def onMouseDown(eventArgs: MouseDownEventArgs[CanvasPack]) {

        val position = getPosition(eventArgs)
        var resultedAnimation: Option[Animation[ListBuffer[InformationView]]] = None
        val vertex = graphView.get.getTouchedVertex(position)

        if (vertex.isDefined) { // Mouse down near a vertex.
            if (eventArgs.shiftKey) { //change selection of the pressed one
                graphView.get.invertVertexSelection(vertex.get)
                if(vertex.get.selected) {
                    val toAnimate = ListBuffer[InformationView]()
                    if(vertex.get.information.isDefined) {
                        toAnimate += vertex.get.information.get
                    }
                    toAnimate ++= getEdgesInformations(vertex.get)
                    resultedAnimation = Some(new Animation(Animation.showText, toAnimate, None,
                        redrawSelection, redrawSelection, None))
                } else {
                    redrawSelection()
                }
            } else { //deselect all and select the pressed one
                if (!vertex.get.selected) {
                    graphView.get.deselectAll()
                }
                if(graphView.get.selectVertex(vertex.get)) {
                    val toAnimate = ListBuffer[InformationView]()
                    if(vertex.get.information.isDefined) {
                        toAnimate += vertex.get.information.get
                    }
                    toAnimate ++= getEdgesInformations(vertex.get)
                    resultedAnimation = Some(new Animation(Animation.showText, toAnimate, None,
                        redrawSelection, redrawSelection, None))
                } else {
                    redrawSelection()
                }
            }
            mousePressedVertex = true

        } else {
            mousePressedVertex = false
        }


        if(resultedAnimation.isDefined) {
            resultedAnimation.get.run()
        }
    }

    /**
     * goes through all edges of the vertex and returns informations of those,
     * which have selected both of their vertices
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

    private def onMouseUp(eventArgs: MouseUpEventArgs[CanvasPack]) {
        if (!mouseDragged && !mousePressedVertex && !eventArgs.shiftKey) { //deselect all

            graphView.get.deselectAll()
            redrawSelection()
        }
    }

    /**
      * Description of mouse-move event. Is called from the layer (canvas) binded to it in the initialization.
      */
    private def onMouseDrag(eventArgs: DraggedEventArgs[CanvasPack]) {

        val end = getPosition(eventArgs)
        if(mousePressedVertex) {
            Animation.clearCurrentTimeout()
            val difference = end - mouseDownPosition

            graphView.get.moveAllSelectedVertices(difference)

            graphView.get.redraw(RedrawOperation.Movement)
        } else {
            Animation.clearCurrentTimeout()
            val difference = end - mouseDownPosition

            graphView.get.moveAllVertices(difference)

            graphView.get.redraw(RedrawOperation.All)
        }
        mouseDownPosition = end
    }

    private def onMouseWheel(eventArgs: MouseWheelEventArgs[CanvasPack]) {
        val mousePosition = getPosition(eventArgs)
        val scrolled = eventArgs.wheelDelta
        zoom(mousePosition, scrolled < 0)
    }

    private def zoom(position: Point, zoomIn: Boolean) {
        if(graphView.isEmpty) {
            return
        }

        var needToRedraw = false

        graphView.get.getAllVertices.foreach{ vv =>
            if(vv.position != position) {

                val distance = vv.position.distance(position) * 0.09 /*zoom step*/
                var p1 = vv.position
                var p2 = vv.position

                if(vv.position.y != position.y) {
                    val v = vv.position.x
                    val w = vv.position.y
                    val m = position.x
                    val n = position.y

                    val A = (n-w)*v+(v-m)*w

                    val a = 1 + (math.pow(m-v, 2)/math.pow(n-w, 2))
                    val b = 2*(((m-v)/(n-w))*((A/(n-w))-v)-w)
                    val c = A/(n-w)*((A/(n-w))-2*v)+math.pow(v, 2)+math.pow(w, 2)-math.pow(distance, 2)

                    val discrim = math.pow(b, 2) - 4*a*c
                    if(discrim > 1) {
                        val discrimSqrt = math.sqrt(discrim)

                        val y1 = (-b + discrimSqrt)/(2*a)
                        val x1 = (A + (m-v)*y1)/(n-w)

                        val y2 = (-b - discrimSqrt)/(2*a)
                        val x2 = (A + (m-v)*y2)/(n-w)

                        p1 = Point(x1, y1)
                        p2 = Point(x2, y2)
                    } else {
                        //window.alert("vertex is in the center of the zoom operation")
                    }

                } else {
                    val y = vv.position.y

                    val x1 = vv.position.x + distance
                    val x2 = vv.position.x - distance
                    p1 = Point(x1, y)
                    p2 = Point(x2, y)
                }
                val p1Distance = p1.distance(position)
                val p2Distance = p2.distance(position)

                if(zoomIn) {
                    if(p1Distance < p2Distance) {
                        vv.position = p2
                    } else {
                        vv.position = p1
                    }
                } else {
                    if(p1Distance < p2Distance) {
                        vv.position = p1
                    } else {
                        vv.position = p2
                    }
                }


                needToRedraw = true
            }
        }

        if(needToRedraw) {
            redraw()
        }
    }

    private def getPosition(eventArgs: EventArgs[CanvasPack]): Point = {

        val positionCorrection = Vector(- graphView.get.canvasPack.offsetLeft, - graphView.get.canvasPack.offsetTop)

        /*if (typeOf(event.clientX) != "undefined" && typeOf(event.clientY) != "undefined") { TODO this check was fine
            Point(event.clientX, event.clientX) + positionCorrection
        }
        else {*/
            Point(eventArgs.clientX /*+ document.body.scrollLeft*/ + document.documentElement.scrollLeft,
                eventArgs.clientY /*+ document.body.scrollTop*/ + document.documentElement.scrollTop) + positionCorrection
        //}
    }
}
