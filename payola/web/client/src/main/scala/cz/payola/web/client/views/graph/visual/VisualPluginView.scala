package cz.payola.web.client.views.graph.visual

import scala.collection._
import s2js.adapters.html
import s2js.adapters.browser._
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

    private var controlsAvailable = false

    protected def allowControlsAvailable() { controlsAvailable = true }

    /**
     * Value used during vertex selection process.
     */
    private var mousePressedVertex = false

    /**
     * Value used during vertex selection process.
     */
    private var mouseIsDragging = false

    /**
     * Value used during mouse dragging process
     */
    private var mouseDownPosition = Point2D(0, 0)

    /**
     * Graph visualized by the pligin.
     */
    var graphView: Option[views.graph.visual.graph.GraphView] = None

    /**
     * Canvas with binded event listeners.
     */
    protected val topLayer = new Canvas()
    topLayer.setAttribute("style", "z-index: 500;")
    //^THANKS to this glyphs of VertexViews are visible but are hidden under the topLayer

    /**
     * Container of all canvases.
     */
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
        if(controlsAvailable) {
            animationStopForced = true
            Animation.clearCurrentTimeout()
            animationStopButton.setIsEnabled(false)
        }
        false
    }

    /**
     * Vertex info table currently rendered over the visual plugin view
     */
    private var currentInfoTable : Option[VertexInfoTable] = None

    /**
     * Object responsible for graph visualization zooming.
     */
    private val zoomControls = new ZoomControls(100)

    /**
     * Download as PNG image button for graphical visualizations.
     */
    private val pngDownloadButton = new Button(new Text("Download as PNG"), "pull-right",
        new Icon(Icon.download)).setAttribute("style", "margin: 0 5px;")

    topLayer.mousePressed += { e =>
        if(controlsAvailable) {
            mouseIsDragging = false
            mouseDownPosition = getPosition(e)
            onMouseDown(e)
        }
        false
    }

    topLayer.mouseReleased += { e =>
        if(controlsAvailable) { onMouseUp(e) }
        false
    }

    topLayer.mouseDragged += { e =>
        if(controlsAvailable) {
            triggerDestroyVertexInfo()
            mouseIsDragging = true
            onMouseDrag(e)
        }
        false
    }

    topLayer.mouseDoubleClicked += { event =>
        if(controlsAvailable) {
            triggerDestroyVertexInfo()
            graphView.foreach { g =>
                val vertex = g.getTouchedVertex(getPosition(event))
                vertex.foreach { v =>
                    g.selectVertex(vertex.get)
                    vertexBrowsing.trigger(new VertexEventArgs[this.type](this, v.vertexModel))
                }
            }
        }
        false
    }

    topLayer.mouseWheelRotated += { event => //zoom - invoked by mouse
        if(controlsAvailable) {
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
        }
        false
    }

    zoomControls.zoomDecreased += { e =>
        if (controlsAvailable && graphView.isDefined && zoomControls.canZoomOut) {
            triggerDestroyVertexInfo()
            zoomOut(graphView.get.getGraphCenter) //zooming from the center of the graph
            zoomControls.decreaseZoomInfo()
        }
        false
    }

    zoomControls.zoomIncreased += { event => //zoom - invoked by zoom control button
        if (controlsAvailable && graphView.isDefined && zoomControls.canZoomIn) {
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

    /**
     * Function for destroying the current vertex info table.
     */
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

    /**
     * Container of the parent HTML element, required for rendering of the vertex info table.
     */
    private var _parentHtmlElement: Option[html.Element] = None

    override def render(parent: html.Element) {
        super.render(parent)

        controlsAvailable = false

        window.onresize = { _ =>
            updateCanvasSize()
            redraw()
        }

        _parentHtmlElement = Some(parent)
        updateCanvasSize()
    }

    override def destroy() {
        super.destroy()
        window.onresize = { _ => }

        if(graphView.isDefined) {
            graphView.get.destroy()
        }

        controlsAvailable = false
        graphView = None
        mouseIsDragging = false
        mousePressedVertex = false
        mouseDownPosition = Point2D(0, 0)

        currentInfoTable.foreach(_.destroy())
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
                    layerPack.getLayers.foreach(_.clear())
                    graphView.get.destroy()
                    graphView = None
                    mouseIsDragging = false
                    mousePressedVertex = false
                    mouseDownPosition = Point2D(0, 0)
                }
            }
        }

        super.updateGraph(graph)
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

    /**
     * Redraw method for during-animation-redrawing.
     */
    protected def redrawQuick() {
        if (!graphView.isEmpty) {
            graphView.get.redraw(layerPack, RedrawOperation.Animation)
        }
    }

    /**
     * Full graph redraw.
     */
    def redraw() {
        if (!graphView.isEmpty) {
            graphView.get.redrawAll(layerPack)
        }
    }

    /**
     * Specific redraw method for vertex selection.
     */
    def redrawSelection() {
        if (graphView.isDefined) {
            graphView.get.redraw(layerPack, RedrawOperation.Selection)
        }
    }

    /**
     * Size of the canvases getter.
     * @return size of the top canvas (all canvases have the same size)
     */
    def size: Vector2D = topLayer.size

    /**
     * Size of the canvases setter.
     * @param size to set
     */
    def size_=(size: Vector2D) {
        layerPack.getLayers.foreach(_.size = size)
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

                        var position = v.position + Vector2D(v.radius, 0)
                        position = if(position.x - 506 < 0) {
                            if(position.y - 330 < 0) {
                                v.position + Vector2D(v.radius, 0)
                            } else {
                                v.position + Vector2D(v.radius, -v.radius - 335)
                            }
                        } else {
                            if(position.y - 330 < 0) {
                                v.position + Vector2D(-521 - v.radius, 0)
                            } else {
                                v.position + Vector2D(-521 - v.radius, -v.radius - 335)
                            }
                        }

                        val infoTable = new VertexInfoTable(v.vertexModel, v.getLiteralVertices,position)

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

                        infoTable.render(_parentHtmlElement.getOrElse(document.body))

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

    /**
     * Description of mouse-released event.
     */
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

    /**
     * Description of zoom in event.
     * @param mousePosition to which the zoom is performed
     */
    private def zoomIn(mousePosition: Point2D) {
        alterVertexPositions(1 + zoomControls.zoomStep, (-mousePosition.toVector) * zoomControls.zoomStep)
    }

    /**
     * Description of zoom out event.
     * @param mousePosition from which the zoom is performed
     */
    private def zoomOut(mousePosition: Point2D) {
        alterVertexPositions(1 - zoomControls.zoomStep, (mousePosition.toVector) * zoomControls.zoomStep)
    }

    /**
     * Vertices moving routine for zooming.
     * @param positionMultiplier how much to zoom
     * @param positionCorrection corresponding to the position of the mouse
     */
    private def alterVertexPositions(positionMultiplier: Double, positionCorrection: Vector2D) {
        graphView.get.getAllVertices.foreach { vv =>
            vv.position = (vv.position * positionMultiplier) + positionCorrection
        }
        redraw()
    }

    /**
     * Mouse position getter according to the top left corner of the canvas.
     * @param eventArgs
     * @return
     */
    private def getPosition(eventArgs: MouseEventArgs[Canvas]): Point2D = {
        Point2D(eventArgs.clientX - layerPack.offset.x, eventArgs.clientY - layerPack.offset.y)
    }

    /**
     * Routine called on window resize event.
     */
    private def updateCanvasSize() {
        layerPack.size = Vector2D(window.innerWidth, window.innerHeight) - topLayer.offset
    }
}
