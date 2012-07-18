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

    private var mouseIsDragging = false

    private var mouseDownPosition = Point2D(0, 0)

    var zoomTool: Option[ZoomControls] = None

    val mouseDragged = new BrowserEvent[Canvas]

    var graphView: Option[views.graph.visual.graph.GraphView] = None

    protected val topLayer = new Canvas()

    private val layerPack = new CanvasPack(new Canvas(), new Canvas(), new Canvas(), new Canvas())

    private var topLayerOffset = Vector2D(0, 0)

    private val layers = List(
        layerPack.edgesDeselected,
        layerPack.edgesSelected,
        layerPack.verticesDeselected,
        layerPack.verticesSelected,
        topLayer
    )

    topLayer.mousePressed += { e =>
        mouseIsPressed = true
        mouseIsDragging = false
        mouseDownPosition = getPosition(e)
        onMouseDown(e)
        false
    }

    topLayer.mouseReleased += { e =>
        mouseIsPressed = false
        onMouseUp(e)
        false
    }

    topLayer.mouseMoved += { e =>
        if (mouseIsPressed) {
            mouseDragged.trigger(e)
        }
        true
    }

    mouseDragged += { event =>
        mouseIsDragging = true
        onMouseDrag(event)
        false
    }

    topLayer.mouseDoubleClicked += { event =>
        graphView.foreach { g =>
            val vertex = g.getTouchedVertex(getPosition(event))
            vertex.foreach { v =>
                g.selectVertex(vertex.get)
                vertexBrowsing.trigger(new VertexEventArgs[this.type](this, v.vertexModel))
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

    window.onresize = { e =>
        fitCanvas()
        redraw()
        true
    }

    //on mouse wheel event work-around###################################################################################

    @javascript(
        """
           /* DOMMouseScroll is for mozilla. */
           self.topLayer.domElement.addEventListener('DOMMouseScroll', function(event) {
               return self.topLayer.mouseWheelRotated.triggerDirectly(self.topLayer, event);
           });
        """)
    private def setMouseWheelListener() {}

    def createSubViews = layers

    override def render(parent: dom.Element) {
        super.render(parent)

        setMouseWheelListener()
        fitCanvas()

        graphView = Some(new views.graph.visual.graph.GraphView(settings))
        zoomTool = Some(new ZoomControls(100))
        zoomTool.get.render(/* document.getElementById("btn-stripe")*/ new Div().domElement) // TODO

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
    }

    def updateGraph(graph: Option[Graph]) {
        if(graph.isDefined) {
            if(graphView.isDefined) {
                graphView.get.update(graph.get)
            } else {
                graphView = Some(new views.graph.visual.graph.GraphView(settings))
            }
        } else {
            if (graphView.isDefined) {
                layers.foreach(_.clear())
                graphView = None
                mouseIsDragging = false
                mousePressedVertex = false
                mouseDownPosition = Point2D(0, 0)
            }
        }
    }

    override def destroy() {
        super.destroy()

        graphView.foreach { g =>
            graphView = None
            mouseIsDragging = false
            mousePressedVertex = false
            mouseDownPosition = Point2D(0, 0)
        }
    }

    protected def redrawQuick() {
        //TODO rename or move somewhere else
        if (!graphView.isEmpty) {
            graphView.get.redraw(layerPack, RedrawOperation.Animation)
        }
    }

    def redraw() {
        if (!graphView.isEmpty) {
            graphView.get.redrawAll(layerPack)
        }
    }

    def redrawSelection() {
        if (graphView.isDefined) {
            graphView.get.redraw(layerPack, RedrawOperation.Selection)
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
        if (!mouseIsDragging && !mousePressedVertex && !eventArgs.shiftKey) {
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

            graphView.get.redraw(layerPack, RedrawOperation.Movement)
        } else {
            Animation.clearCurrentTimeout()
            val difference = end - mouseDownPosition

            graphView.get.moveAllVertices(difference)

            graphView.get.redraw(layerPack, RedrawOperation.All)
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
        Point2D(eventArgs.clientX - topLayerOffset.x, eventArgs.clientY - topLayerOffset.y)
    }

    def fitCanvas() {
        topLayerOffset = calculateTopLayerOffset
        val layerSize = Vector2D(window.innerWidth, window.innerHeight) - topLayerOffset
        layers.foreach(_.size = layerSize)
    }

    @javascript("""
        var offsetTop = 0;
        var element = self.topLayer.domElement;
        while (element != null) {
            offsetTop += element.offsetTop;
            element = element.offsetParent;
        }
        return new cz.payola.web.client.views.algebra.Vector2D(0, offsetTop);
    """)
    private def calculateTopLayerOffset: Vector2D = Vector2D(0, 0)
}
