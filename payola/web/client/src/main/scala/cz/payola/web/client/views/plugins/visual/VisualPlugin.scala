package cz.payola.web.client.views.plugins.visual

import animation.Animation
import cz.payola.web.client.views.plugins.Plugin
import graph.{InformationView, VertexView, GraphView}
import s2js.adapters.js.dom.Element
import collection.mutable.ListBuffer
import settings.components.visualsetup.VisualSetup
import s2js.adapters.js.browser.document
import cz.payola.web.client.views.elements.CanvasPack
import cz.payola.web.client.views.elements.CanvasPack
import cz.payola.web.client.events._
import cz.payola.common.rdf._
import s2js.adapters.js.browser.window
import cz.payola.web.client.presenters.components.ZoomControls
import cz.payola.web.client.views.events._
import scala.Some

/**
  * Representation of visual based output drawing plugin
  */
abstract class VisualPlugin(settings: VisualSetup) extends Plugin
{

    private var mousePressedVertex = false
    private var mouseDragged = false
    private var mouseDownPosition = Point(0, 0)

    var zoomTool: Option[ZoomControls] = None

    val vertexUpdate = new VertexUpdateEvent

    /**
      * Contained graph in visualisation packing.
      */
    var graphView: Option[GraphView] = None

    def init(container: Element) {

        graphView = Some(new GraphView(container, settings))
        zoomTool = Some(new ZoomControls(100))
        zoomTool.get.render(document.getElementById("btn-stripe"))

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

        graphView.get.canvasPack.mouseWheel += { event => //zoom - invoked by mouse
            val mousePosition = getPosition(event)
            val scrolled = event.wheelDelta

            if(scrolled < 0) {
                if(zoomTool.get.canZoomIn) {
                    zoomIn(mousePosition)
                    zoomTool.get.increaseZoomInfo()
                }
            } else {
                if(zoomTool.get.canZoomOut) {
                    zoomOut(mousePosition)
                    zoomTool.get.decreaseZoomInfo()
                }
            }
            false
        }

        zoomTool.get.zoomDecreased += { event => //zoom - invoked by zoom control button
            if(graphView.isDefined && zoomTool.get.canZoomOut) {
                zoomOut(graphView.get.getGraphCenter) //zooming from the center of the graph
                zoomTool.get.decreaseZoomInfo()
            }
            false
        }

        zoomTool.get.zoomIncreased += { event => //zoom - invoked by zoom control button
            if(graphView.isDefined && zoomTool.get.canZoomIn) {
                zoomIn(graphView.get.getGraphCenter) //zooming to the center of the graph
                zoomTool.get.increaseZoomInfo()
            }
            false
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

    def clear() {
        if(graphView.isDefined) {
            graphView.get.canvasPack.clear()
            graphView = None
            mouseDragged = false
            mousePressedVertex = false
            mouseDownPosition = Point(0, 0)
        }

        if(zoomTool.isDefined) {
            zoomTool.get.reset()
        }
    }

    def destroy() {
        if(graphView.isDefined) {
            graphView.get.destroy()
            graphView = None
            mouseDragged = false
            mousePressedVertex = false
            mouseDownPosition = Point(0, 0)
        }

        if(zoomTool.isDefined) {
            zoomTool.get.destroy()
            zoomTool = None
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


    private def zoomIn(mousePosition: Point) {

        var needToRedraw = false
        graphView.get.getAllVertices.foreach{ vv =>
            if(vv.position != mousePosition) {

                val points = getZoomPointCandidates(vv, mousePosition)
                val p1Distance = points._1.distance(mousePosition)
                val p2Distance = points._2.distance(mousePosition)
                if(p1Distance < p2Distance) {
                    vv.position = points._2
                } else {
                    vv.position = points._1
                }
                needToRedraw = true
            }
        }

        if(needToRedraw) {
            redraw()
        }
    }

    private def zoomOut(mousePosition: Point) {

        var needToRedraw = false
        graphView.get.getAllVertices.foreach{ vv =>
            if(vv.position != mousePosition) {

                val points = getZoomPointCandidates(vv, mousePosition)
                val p1Distance = points._1.distance(mousePosition)
                val p2Distance = points._2.distance(mousePosition)
                if(p1Distance < p2Distance) {
                    vv.position = points._1
                } else {
                    vv.position = points._2
                }
                needToRedraw = true
            }
        }

        if(needToRedraw) {
            redraw()
        }
    }

    private def getZoomPointCandidates(vv: VertexView, position: Point): (Point, Point) = {

        val distance = vv.position.distance(position) * zoomTool.get.zoomStep
        if(distance == 0) {
            //window.alert("distance == 0")
        }
        var p1 = vv.position
        var p2 = vv.position

        if(math.round(vv.position.y) != math.round(position.y)) {
            /*this comparison might look strange, the rounding is here, because JavaScript does not have that
            precise double type*/

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
            //window.alert("vertex.position.y == mousePosition.y")
            val y = vv.position.y

            val discrim = math.pow(distance, 2) - math.pow(y - vv.position.y, 2)
            if(discrim > 1) {
                val discrimSqrt = math.sqrt(discrim)

                val x1 = vv.position.x + discrimSqrt
                val x2 = vv.position.x - discrimSqrt
                p1 = Point(x1, y)
                p2 = Point(x2, y)
            } else {
                //window.alert("vertex.position.y == mousePosition.y && vertex is in the center of the zoom operation")
            }
        }

        (p1, p2)
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
