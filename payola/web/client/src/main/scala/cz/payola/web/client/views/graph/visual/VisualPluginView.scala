package cz.payola.web.client.views.graph.visual

import scala.collection._
import s2js.adapters.browser._
import s2js.adapters.html
import s2js.compiler.javascript
import animation.Animation
import cz.payola.web.client.views.graph.PluginView
import cz.payola.web.client.events._
import cz.payola.common.rdf._
import cz.payola.web.client.presenters.components.ZoomControls
import cz.payola.web.client.views.elements._
import cz.payola.web.client._
import cz.payola.web.client.views.algebra._
import cz.payola.web.client.views.graph.visual.graph._
import cz.payola.web.client.views._
import cz.payola.web.client.views.bootstrap.Icon
import cz.payola.common.entities.settings.OntologyCustomization

/**
 * Representation of visual based output drawing plugin
 */
abstract class VisualPluginView(name: String) extends PluginView(name)
{
    protected var mouseIsPressed = false

    private var mousePressedVertex = false

    private var mouseIsDragging = false

    private var mouseDownPosition = Point2D(0, 0)

    val mouseDragged = new UnitEvent[Canvas, MouseEventArgs[Canvas]]

    var graphView: Option[views.graph.visual.graph.GraphView] = None

    protected val topLayer = new Canvas()

    private val layerPack = new CanvasPack(topLayer, new Canvas(), new Canvas(), new Canvas(), new Canvas())

    /**
     * A way to end the main animation. Has to be set show(..) in the visual technique.
     */
    protected val animationStopButton = new Button(new Text("Stop animation"), "pull-right",
        new Icon(Icon.stop)).setAttribute("style", "margin: 0 5px;")

    /**
     * This is set to true if the animationStopButton is pressed.
     */
    protected var animationStopForced = false


    animationStopButton.mouseClicked += { e =>
        animationStopForced = true
        Animation.clearCurrentTimeout()
        animationStopButton.setIsEnabled(false)
        false
    }

    private val layers = List(
        layerPack.edgesDeselected,
        layerPack.edgesSelected,
        layerPack.verticesDeselected,
        layerPack.verticesSelected,
        topLayer
    )

    private var currentInfoTable : Option[VertexInfoTable] = None

    private val zoomControls = new ZoomControls(100)

    private val pngDownloadButton = new Button(new Text("Download as PNG"), "pull-right",
        new Icon(Icon.download)).setAttribute("style", "margin: 0 5px;")

    window.onresize = { e =>
        fitCanvas()
        redraw()
        true
    }

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

    mouseDragged += { e =>
        triggerDestroyVertexInfo()
        mouseIsDragging = true
        onMouseDrag(e)
        false
    }

    topLayer.mouseDoubleClicked += { event =>
        triggerDestroyVertexInfo()
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
        triggerDestroyVertexInfo()
        val mousePosition = getPosition(event)
        val scrolled = event.wheelDelta

        if (scrolled < 0) {
            if (zoomControls.canZoomIn) {
                zoomIn(mousePosition)
                zoomControls.increaseZoomInfo()
            }
        } else {
            if (zoomControls.canZoomOut) {
                zoomOut(mousePosition)
                zoomControls.decreaseZoomInfo()
            }
        }
        false
    }

    zoomControls.zoomDecreased += { e =>
        if (graphView.isDefined && zoomControls.canZoomOut) {
            triggerDestroyVertexInfo()
            zoomOut(graphView.get.getGraphCenter) //zooming from the center of the graph
            zoomControls.decreaseZoomInfo()
        }
        false
    }

    zoomControls.zoomIncreased += { event => //zoom - invoked by zoom control button
        if (graphView.isDefined && zoomControls.canZoomIn) {
            triggerDestroyVertexInfo()
            zoomIn(graphView.get.getGraphCenter) //zooming to the center of the graph
            zoomControls.increaseZoomInfo()
        }
        false
    }

    pngDownloadButton.mouseClicked += { e =>
        val c = new Canvas()
        c.htmlElement.width = topLayer.htmlElement.width
        c.htmlElement.height = topLayer.htmlElement.height

        c.htmlElement.getContext[html.elements.CanvasContext]("2d")
            .drawImage(layerPack.edgesDeselected.htmlElement, 0, 0)
        c.htmlElement.getContext[html.elements.CanvasContext]("2d")
            .drawImage(layerPack.edgesSelected.htmlElement, 0, 0)
        c.htmlElement.getContext[html.elements.CanvasContext]("2d")
            .drawImage(layerPack.verticesDeselected.htmlElement, 0, 0)
        c.htmlElement.getContext[html.elements.CanvasContext]("2d")
            .drawImage(layerPack.verticesSelected.htmlElement, 0, 0)
        c.htmlElement.getContext[html.elements.CanvasContext]("2d").drawImage(topLayer.htmlElement, 0, 0)

        window.open(c.htmlElement.toDataURL("image/png"))
        false
    }

    private def triggerDestroyVertexInfo() {
        if(currentInfoTable.isDefined) {
            currentInfoTable.get.destroy()
            currentInfoTable = None
        }
    }

    override def updateOntologyCustomization(newCustomization: Option[OntologyCustomization]) {
        currentCustomization = newCustomization

        if (graphView.isDefined) {
            graphView.get.setConfiguration(newCustomization)
        }

        redraw()
    }

    def createSubViews = layerPack.getLayers

    private var _parentHtmlElement: Option[html.Element] = None
    protected def parentHtmlElement_=(value: Option[html.Element]) {_parentHtmlElement = value}
    protected def parentHtmlElement: Option[html.Element] = _parentHtmlElement

    override def render(parent: html.Element) {
        super.render(parent)

        parentHtmlElement = Some(parent)
        fitCanvas()
    }

    override def updateGraph(graph: Option[Graph]) {
        // If the graph has changed, update the graph view.
        zoomControls.reset()
        if (graph != currentGraph) {
            if (graph.isDefined) {
                if (graphView.isEmpty) {
                    graphView = Some(new views.graph.visual.graph.GraphView)
                }
                graphView.get.update(graph.get, topLayer.getCenter)
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

        super.updateGraph(graph)
    }

    override def destroy() {
        super.destroy()

        graphView = None
        mouseIsDragging = false
        mousePressedVertex = false
        mouseDownPosition = Point2D(0, 0)

        currentInfoTable.foreach(_.destroy())
    }

    override def renderControls(toolbar: html.Element) {
        zoomControls.render(toolbar)
        animationStopButton.render(toolbar)
        animationStopButton.setIsEnabled(false)
        pngDownloadButton.render(toolbar)
    }

    override def destroyControls() {
        zoomControls.destroy()
        animationStopButton.destroy()
        pngDownloadButton.destroy()
    }

    protected def redrawQuick() {
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
    private def onMouseDown(eventArgs: MouseEventArgs[Canvas]) {
        val position = getPosition(eventArgs)
        val vertex = graphView.get.getTouchedVertex(position)
        triggerDestroyVertexInfo()

        if (vertex.isDefined) {
            // Mouse down near a vertex.
            if (eventArgs.shiftKey) {
                //change selection of the pressed one
                graphView.get.invertVertexSelection(vertex.get)

                redrawSelection()
            } else {

                //deselect all and select the pressed one
                if (!vertex.get.selected) {
                    graphView.get.deselectAll()
                }
                graphView.get.selectVertex(vertex.get)

                vertex.foreach { v =>
                    if (v.selected) {
                        val infoTable = new VertexInfoTable(v.vertexModel, v.getLiteralVertices,
                            v.position + Vector2D(v.radius, 0))
                        infoTable.vertexBrowsing += { a =>
                            triggerDestroyVertexInfo()
                            vertexBrowsing.trigger(new VertexEventArgs[this.type](this, vertex.get.vertexModel))
                        }
                        infoTable.vertexBrowsingDataSource += { a =>
                            triggerDestroyVertexInfo()
                            vertexBrowsingDataSource
                                .trigger(new VertexEventArgs[this.type](this, vertex.get.vertexModel))
                        }

                        currentInfoTable = Some(infoTable)

                        infoTable.render(parentHtmlElement.getOrElse(document.body))

                        vertexSelected.trigger(new VertexEventArgs[this.type](this, v.vertexModel))
                    }
                }


                redrawSelection()
            }
            mousePressedVertex = true
        } else {
            mousePressedVertex = false
        }
    }

    /**
     * goes through all edges of the vertex and returns informations of those,
     * which have selected both of their vertices
     * @return
     */
    private def getEdgesInformations(vertexView: VertexView): mutable.ListBuffer[InformationView] = {
        val result = mutable.ListBuffer[InformationView]()
        vertexView.edges.foreach { edgeView =>
            if (edgeView.originView.selected && edgeView.destinationView.selected) {
                result += edgeView.information
            }
        }
        result
    }

    private def onMouseUp(eventArgs: MouseEventArgs[Canvas]) {
        if (!mouseIsDragging && !mousePressedVertex && !eventArgs.shiftKey) {
            //deselect all

            graphView.get.deselectAll()
            redrawSelection()
        }
    }

    /**
     * Description of mouse-move event. Is called from the layer (canvas) binded to it in the initialization.
     */
    private def onMouseDrag(eventArgs: MouseEventArgs[Canvas]) {
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
        alterVertexPositions(1 + zoomControls.zoomStep, (-mousePosition.toVector) * zoomControls.zoomStep)
    }

    private def zoomOut(mousePosition: Point2D) {
        alterVertexPositions(1 - zoomControls.zoomStep, (mousePosition.toVector) * zoomControls.zoomStep)
    }

    private def alterVertexPositions(positionMultiplier: Double, positionCorrection: Vector2D) {
        graphView.get.getAllVertices.foreach { vv =>
            vv.position = (vv.position * positionMultiplier) + positionCorrection
        }
        redraw()
    }

    private def getPosition(eventArgs: MouseEventArgs[Canvas]): Point2D = {
        Point2D(eventArgs.clientX - layerPack.offset.x, eventArgs.clientY - layerPack.offset.y)
    }

    def fitCanvas() {
        parentHtmlElement.foreach { e =>
            layerPack.size = Vector2D(window.innerWidth, window.innerHeight) - topLayer.offset
        }
    }
}
