package cz.payola.web.client.views.graph.visual

import animation.Animation
import cz.payola.web.client.views.graph.PluginView
import graph.{InformationView, VertexView}
import collection.mutable.ListBuffer
import settings.components.visualsetup.VisualSetup
import s2js.adapters.js.browser.document
import cz.payola.web.client.events._
import cz.payola.common.rdf._
import cz.payola.web.client.presenters.components.ZoomControls
import cz.payola.web.client.views.elements._
import cz.payola.web.client._
import cz.payola.web.client.views.algebra._
import scala.Some
import s2js.adapters.js.browser.window
import s2js.adapters.js._
import s2js.compiler.javascript
import cz.payola.web.client.views.todo.CanvasPack
import cz.payola.web.client.views.VertexEventArgs

/**
  * Representation of visual based output drawing plugin
  */
abstract class VisualPluginView(settings: VisualSetup, name: String) extends PluginView(name)
{

    protected var mouseIsPressed = false

    private var mousePressedVertex = false

    private var mouseDragged = false

    private var mouseDownPosition = Point2D(0, 0)

    var zoomTool: Option[ZoomControls] = None

    val mouseDraggedEvent = new BrowserEvent[Canvas]

    val windowResizedEvent = new BrowserEvent[VisualPluginView]

    /**
      * Contained graph in visualisation packing.
      */
    var graphView: Option[views.graph.visual.graph.GraphView] = None

    private var parent: dom.Element = null

    protected val topLayer = new Canvas()

    private val edgesDeselectedLayer = new Canvas()

    private val edgesSelectedLayer = new Canvas()

    private val verticesDeselectedLayer = new Canvas()

    private val verticesSelectedLayer = new Canvas()

    private val layersPack = new CanvasPack(edgesDeselectedLayer, edgesSelectedLayer,
        verticesDeselectedLayer, verticesSelectedLayer)

    def layers = List(edgesDeselectedLayer, edgesSelectedLayer, verticesDeselectedLayer, verticesSelectedLayer,
        topLayer)

    def createSubViews = layers

    topLayer.mousePressed += { e =>
        mouseIsPressed = true
        true
    }

    topLayer.mouseReleased += { e =>
        mouseIsPressed = false
        true
    }

    topLayer.mouseMoved += { e =>
        if (mouseIsPressed) {
            mouseDraggedEvent.trigger(e)
        }
        true
    }

    window.onresize = { e => windowResizedEvent.triggerDirectly(this, e)}

    //on mouse wheel event work-around###################################################################################

    @javascript(
        """
           /* DOMMouseScroll is for mozilla. */
           self.topLayer.domElement.addEventListener('DOMMouseScroll', function(event) {
               return self.topLayer.mouseWheelRotated.triggerDirectly(self.topLayer, event);
           });
        """)
    private def setMouseWheelListener() {}

    //^TODO this calls the onMouseWheel function in window context; that results in error,
    // because window..mouseWheel does not exist


    private def offsetLeft: Double = {
        topLayer.domElement.offsetLeft
    }

    private def offsetTop: Double = {
        topLayer.domElement.offsetTop
    }

    override def render(container: dom.Element) {
        parent = container

        setMouseWheelListener()
        createSubViews.foreach(_.render(container))

        graphView = Some(new views.graph.visual.graph.GraphView(container, settings))
        zoomTool = Some(new ZoomControls(100))
        zoomTool.get.render(/* document.getElementById("btn-stripe")*/ new Div().domElement) // TODO

        topLayer.mousePressed += { event => //selection
            mouseDragged = false
            mouseDownPosition = getPosition(event)
            onMouseDown(event)

            false
        }

        topLayer.mouseReleased += { event => //deselect all
            onMouseUp(event)

            false
        }

        mouseDraggedEvent += { event => //vertices move
            mouseDragged = true
            onMouseDrag(event)
            false
        }

        topLayer.mouseDoubleClicked += { event => //update graph
            if(graphView.isDefined) {
                val vertex = graphView.get.getTouchedVertex(getPosition(event))
                if (vertex.isDefined) {
                    graphView.get.selectVertex(vertex.get)
                    vertexBrowsing.trigger(new VertexEventArgs[this.type](this, vertex.get.vertexModel))
                }
            }
            false
        }

        topLayer.mouseWheelRotated += { event => //zoom - invoked by mouse
            val mousePosition = getPosition(event)
            val scrolled = event.wheelDelta

            if (scrolled < 0) {
                if (zoomTool.get.canZoomIn) {
                    zoomIn(mousePosition)
                    zoomTool.get.increaseZoomInfo()
                }
            } else {
                if (zoomTool.get.canZoomOut) {
                    zoomOut(mousePosition)
                    zoomTool.get.decreaseZoomInfo()
                }
            }
            false
        }

        zoomTool.get.zoomDecreased += { event => //zoom - invoked by zoom control button
            if (graphView.isDefined && zoomTool.get.canZoomOut) {
                zoomOut(graphView.get.getGraphCenter) //zooming from the center of the graph
                zoomTool.get.decreaseZoomInfo()
            }
            false
        }

        zoomTool.get.zoomIncreased += { event => //zoom - invoked by zoom control button
            if (graphView.isDefined && zoomTool.get.canZoomIn) {
                zoomIn(graphView.get.getGraphCenter) //zooming to the center of the graph
                zoomTool.get.increaseZoomInfo()
            }
            false
        }

        windowResizedEvent += { event => //fitting canvas on window resize
            fitCanvas()
            redraw()
            true
        }
    }

    def updateGraph(graph: Option[Graph]) {
        if(graph.isDefined) {
            if(graphView.isDefined) {
                graphView.get.update(graph.get)
            } else {
                graphView = Some(new views.graph.visual.graph.GraphView(parent, settings))
            }
        } else {
            clear()
        }
    }

    override def clear() {
        if (graphView.isDefined) {
            createSubViews.foreach{_.clear()}
            graphView = None
            mouseDragged = false
            mousePressedVertex = false
            mouseDownPosition = Point2D(0, 0)
        }

        if (zoomTool.isDefined) {
            zoomTool.get.reset()
        }
    }



    override def destroy() {
        if (graphView.isDefined) {
            graphView.get.destroy()
            graphView = None
            mouseDragged = false
            mousePressedVertex = false
            mouseDownPosition = Point2D(0, 0)
        }

        if (zoomTool.isDefined) {
            zoomTool.get.destroy()
            zoomTool = None
        }
    }

    protected def redrawQuick() {
        //TODO rename or move somewhere else
        if (!graphView.isEmpty) {
            graphView.get.redraw(layersPack, RedrawOperation.Animation)
        }
    }

    def redraw() {
        if (!graphView.isEmpty) {
            graphView.get.redrawAll(layersPack)
        }
    }

    def redrawSelection() {
        if (graphView.isDefined) {
            graphView.get.redraw(layersPack, RedrawOperation.Selection)
        }
    }

    def size: Vector2D = topLayer.size

    def size_=(size: Vector2D) {

        layers.foreach(_.size = size)
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //mouse event handler routines///////////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
      * Description of mouse-button-down event. Is called from the layer (canvas) binded to it in the initialization.
      */
    private def onMouseDown(eventArgs: BrowserEventArgs[Canvas]) {
        val position = getPosition(eventArgs)
        var resultedAnimation: Option[Animation[ListBuffer[InformationView]]] = None
        val vertex = graphView.get.getTouchedVertex(position)

        if (vertex.isDefined) {
            // Mouse down near a vertex.
            if (eventArgs.shiftKey) {
                //change selection of the pressed one
                graphView.get.invertVertexSelection(vertex.get)
                if (vertex.get.selected) {
                    val toAnimate = ListBuffer[InformationView]()
                    if (vertex.get.information.isDefined) {
                        toAnimate += vertex.get.information.get
                    }
                    toAnimate ++= getEdgesInformations(vertex.get)
                    resultedAnimation = Some(new Animation(Animation.showText, toAnimate, None,
                        redrawSelection, redrawSelection, None))
                } else {
                    redrawSelection()
                }
            } else {
                //deselect all and select the pressed one
                if (!vertex.get.selected) {
                    graphView.get.deselectAll()
                }
                if (graphView.get.selectVertex(vertex.get)) {
                    val toAnimate = ListBuffer[InformationView]()
                    if (vertex.get.information.isDefined) {
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
            vertexSelected.trigger(new VertexEventArgs[this.type](this, vertex.get.vertexModel))
        } else {
            mousePressedVertex = false
        }


        if (resultedAnimation.isDefined) {
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
        vertexView.edges.foreach { edgeView =>
            if (edgeView.originView.selected && edgeView.destinationView.selected) {
                result += edgeView.information
            }
        }
        result
    }

    private def onMouseUp(eventArgs: BrowserEventArgs[Canvas]) {
        if (!mouseDragged && !mousePressedVertex && !eventArgs.shiftKey) {
            //deselect all

            graphView.get.deselectAll()
            redrawSelection()
        }
    }

    /**
      * Description of mouse-move event. Is called from the layer (canvas) binded to it in the initialization.
      */
    private def onMouseDrag(eventArgs: BrowserEventArgs[Canvas]) {
        val end = getPosition(eventArgs)
        if (mousePressedVertex) {
            Animation.clearCurrentTimeout()
            val difference = end - mouseDownPosition

            graphView.get.moveAllSelectedVertices(difference)

            graphView.get.redraw(layersPack, RedrawOperation.Movement)
        } else {
            Animation.clearCurrentTimeout()
            val difference = end - mouseDownPosition

            graphView.get.moveAllVertices(difference)

            graphView.get.redraw(layersPack, RedrawOperation.All)
        }
        mouseDownPosition = end
    }

    private def zoomIn(mousePosition: Point2D) {
        var needToRedraw = false
        graphView.get.getAllVertices.foreach { vv =>
            if (vv.position != mousePosition) {

                val points = getZoomPointCandidates(vv, mousePosition)
                val p1Distance = points._1.distance(mousePosition)
                val p2Distance = points._2.distance(mousePosition)
                if (p1Distance < p2Distance) {
                    vv.position = points._2
                } else {
                    vv.position = points._1
                }
                needToRedraw = true
            }
        }

        if (needToRedraw) {
            redraw()
        }
    }

    private def zoomOut(mousePosition: Point2D) {
        var needToRedraw = false
        graphView.get.getAllVertices.foreach { vv =>
            if (vv.position != mousePosition) {

                val points = getZoomPointCandidates(vv, mousePosition)
                val p1Distance = points._1.distance(mousePosition)
                val p2Distance = points._2.distance(mousePosition)
                if (p1Distance < p2Distance) {
                    vv.position = points._1
                } else {
                    vv.position = points._2
                }
                needToRedraw = true
            }
        }

        if (needToRedraw) {
            redraw()
        }
    }

    private def getZoomPointCandidates(vv: VertexView, position: Point2D): (Point2D, Point2D) = {
        val distance = vv.position.distance(position) * zoomTool.get.zoomStep
        if (distance == 0) {
            //window.alert("distance == 0")
        }
        var p1 = vv.position
        var p2 = vv.position

        if (scala.math.round(vv.position.y) != scala.math.round(position.y)) {
            /*this comparison might look strange, the rounding is here, because JavaScript does not have that
            precise double type*/

            val v = vv.position.x
            val w = vv.position.y
            val m = position.x
            val n = position.y

            val A = (n - w) * v + (v - m) * w

            val a = 1 + (math.pow(m - v, 2) / math.pow(n - w, 2))
            val b = 2 * (((m - v) / (n - w)) * ((A / (n - w)) - v) - w)
            val c = A / (n - w) * ((A / (n - w)) - 2 * v) + math.pow(v, 2) + math.pow(w, 2) - math.pow(distance, 2)

            val discrim = math.pow(b, 2) - 4 * a * c
            if (discrim > 1) {
                val discrimSqrt = math.sqrt(discrim)

                val y1 = (-b + discrimSqrt) / (2 * a)
                val x1 = (A + (m - v) * y1) / (n - w)

                val y2 = (-b - discrimSqrt) / (2 * a)
                val x2 = (A + (m - v) * y2) / (n - w)

                p1 = Point2D(x1, y1)
                p2 = Point2D(x2, y2)
            } else {
                //window.alert("vertex is in the center of the zoom operation")
            }
        } else {
            //window.alert("vertex.position.y == mousePosition.y")
            val y = vv.position.y

            val discrim = math.pow(distance, 2) - math.pow(y - vv.position.y, 2)
            if (discrim > 1) {
                val discrimSqrt = math.sqrt(discrim)

                val x1 = vv.position.x + discrimSqrt
                val x2 = vv.position.x - discrimSqrt
                p1 = Point2D(x1, y)
                p2 = Point2D(x2, y)
            } else {
                //window.alert("vertex.position.y == mousePosition.y && vertex is in the center of the zoom operation")
            }
        }

        (p1, p2)
    }

    private def getPosition(eventArgs: BrowserEventArgs[Canvas]): Point2D = {
        val positionCorrection = Vector2D(offsetLeft, offsetTop)

        /*if (typeOf(event.clientX) != "undefined" && typeOf(event.clientY) != "undefined") { TODO this check was fine
            Point2D(event.clientX, event.clientX) + positionCorrection
        }
        else {*/
        Point2D(eventArgs.clientX /*+ document.body.scrollLeft*/ + document.documentElement.scrollLeft,
            eventArgs.clientY /*+ document.body.scrollTop*/ + document.documentElement.scrollTop) + positionCorrection
        //}
    }

    /**
      * Constructs a canvas context element as a child of the input container ElementView object.
      * @param container parent of the created canvas context
      * @return Layer object with a new canvas context
      */
    /*protected def createCanvasPack(container: Element): CanvasPack = {
        val canvasPack = new CanvasPack(Vector2D(window.innerWidth - container.offsetLeft,
            window.innerHeight - container.offsetTop))
        canvasPack.render(container)

        canvasPack
        TODO remove
    }*/

    def fitCanvas() {
        //measure size of the graph and set dimensions of the canvasPack accordingly (max(sizeOfTheWindow,
        // sizeOfTheGraph))
        val maxBottomRight = Point2D(0, 0)
        graphView.get.components.foreach { component =>
            val componentBR = component.getBottomRight()
            if (maxBottomRight.x < componentBR.x) {
                maxBottomRight.x = componentBR.x
            }
            if (maxBottomRight.y < componentBR.y) {
                maxBottomRight.y = componentBR.y
            }
        }
        layers.foreach(_.size =
            Vector2D(window.innerWidth - offsetLeft, window.innerHeight - offsetTop))
    }
}
