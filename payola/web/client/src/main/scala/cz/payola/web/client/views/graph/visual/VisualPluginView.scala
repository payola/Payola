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
import bootstrap._
import cz.payola.common.entities.settings.OntologyCustomization
import cz.payola.common.visual.Color
import lists.ListItem
import scala.Some

/**
 * Representation of visual based output drawing plugin
 */
abstract class VisualPluginView(name: String) extends PluginView(name)
{
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
     * Graph visualized by the plugin.
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

    private def endAnimation() {
        animationStopForced = true
        Animation.clearCurrentTimeout()
        animationStopButton.setIsEnabled(false)
    }

    animationStopButton.mouseClicked += {
        e =>
            endAnimation()
            false
    }

    /**
     * Vertex info table currently rendered over the visual plugin view
     */
    private var currentInfoTable: Option[VertexInfoTable] = None

    /**
     * Object responsible for graph visualization zooming.
     */
    private val zoomControls = new ZoomControls(100)

    /**
     * Download as PNG image button for graphical visualizations.
     */
    private val pngDownloadButton = new Anchor(List(new Icon(Icon.download), new Text("Download as PNG")))
    private val setMainVertexButton = new Anchor(List(new Icon(Icon.screenshot), new Text("Set main vertex")))


    /*private val languageMenu = new DropDownButton( TODO
        List(new Icon(Icon.globe), new Text("Language")),
        List(
            new ListItem(List(new Text("Test"))),
            new ListItem(List(new Text("Test1"))),
            new ListItem(List(new Text("Test3")))
        ),
        "", "pull-right"
    ).setAttribute("style", "margin: 0 5px;")*/

    private val visualTools = new DropDownButton(
        List(new Icon(Icon.eye_open), new Text("Visual tools")),
        List(
            new ListItem(List(pngDownloadButton)),
            new ListItem(List(setMainVertexButton))
            ),
        "", "pull-right"
    ).setAttribute("style", "margin: 0 5px;")

    topLayer.mousePressed += {
        e =>
            endAnimation()
            mouseIsDragging = false
            mouseDownPosition = getPosition(e)
            onMouseDown(e)
            false
    }

    topLayer.mouseReleased += {
        e =>
            endAnimation()
            onMouseUp(e)
            false
    }

    topLayer.mouseDragged += {
        e =>
            endAnimation()
            triggerDestroyVertexInfo()
            mouseIsDragging = true
            onMouseDrag(e)

            false
    }

    topLayer.mouseDoubleClicked += {
        event =>
            endAnimation()
            triggerDestroyVertexInfo()
            graphView.foreach {
                g =>
                    val vertex = g.getTouchedVertex(getPosition(event))
                    vertex.foreach {
                        v =>
                            g.selectVertex(v)
                            vertexBrowsing.trigger(new VertexEventArgs[this.type](this, v.vertexModel))
                    }
            }
            false
    }

    topLayer.mouseWheelRotated += {
        event => //zoom - invoked by mouse
            endAnimation()
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

    zoomControls.zoomDecreased += {
        e =>
            endAnimation()
            if (graphView.isDefined && zoomControls.canZoomOut) {
                triggerDestroyVertexInfo()
                zoomOut(graphView.get.getGraphCenter) //zooming from the center of the graph
                zoomControls.decreaseZoomInfo()
            }
            false
    }

    zoomControls.zoomIncreased += {
        event => //zoom - invoked by zoom control button
            endAnimation()
            if (graphView.isDefined && zoomControls.canZoomIn) {
                triggerDestroyVertexInfo()
                zoomIn(graphView.get.getGraphCenter) //zooming to the center of the graph
                zoomControls.increaseZoomInfo()
            }
            false
    }

    pngDownloadButton.mouseClicked += {
        e =>
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

    setMainVertexButton.mouseClicked += { e =>
        val selectedVertices = graphView.get.getAllSelectedVertices
        if(selectedVertices.size == 1) {
            zoomControls.reset()
            vertexSetMain.trigger(new VertexEventArgs[this.type](this, selectedVertices.head.vertexModel))
        }
        false
    }

    /**
     * Function for destroying the current vertex info table.
     */
    private def triggerDestroyVertexInfo() {
        if (currentInfoTable.isDefined) {
            currentInfoTable.get.destroy()
            currentInfoTable = None
        }
    }

    private def createInfoTable(vertexView: VertexView) {
        if (!vertexView.getLiteralVertices.isEmpty) {
            vertexView.vertexModel match {
                case vm: IdentifiedVertex => {
                    val infoTable =
                        new VertexInfoTable(vm, vertexView.getLiteralVertices, Point2D.Zero)

                    infoTable.vertexBrowsing += {
                        a =>
                            triggerDestroyVertexInfo()
                            vertexBrowsing.trigger(new VertexEventArgs[this.type](this, vertexView.vertexModel))
                    }
                    infoTable.vertexBrowsingDataSource += {
                        a =>
                            triggerDestroyVertexInfo()
                            vertexBrowsingDataSource
                                .trigger(new VertexEventArgs[this.type](this, vertexView.vertexModel))
                    }

                    currentInfoTable = Some(infoTable)
                    infoTable.render(_parentHtmlElement.getOrElse(document.body))

                    val tableSize = infoTable.getSize

                    var position = vertexView.position + Vector2D(vertexView.radius, 0)
                    position = if (position.x - tableSize.x < 0) {
                        if (position.y - tableSize.y < 0) {
                            vertexView.position + Vector2D(vertexView.radius, 0)
                        } else {
                            vertexView.position + Vector2D(vertexView.radius, -tableSize.y)
                        }
                    } else {
                        if (position.y - tableSize.y < 0) {
                            vertexView.position + Vector2D(-tableSize.x - vertexView.radius, 0)
                        } else {
                            vertexView.position + Vector2D(-tableSize.x - vertexView.radius, -tableSize.y)
                        }
                    }

                    infoTable.setPosition(position)
                }
            }
        }
    }

    override def updateOntologyCustomization(newCustomization: Option[OntologyCustomization]) {
        currentCustomization = newCustomization

        graphView.foreach {
            gV =>
                gV.setConfiguration(newCustomization)
                _parentHtmlElement.foreach(gV.render(_))
        }

        redraw()
    }

    override def updateVertexColor(vertex: Vertex, color: Option[Color]) {
        graphView.foreach {
            gV =>
                gV.setVertexColor(vertex, color)
                _parentHtmlElement.foreach(gV.render(_))
        }

        redraw()
    }

    override def setMainVertex(vertex: Vertex) {

    }

    def createSubViews = layerPack.getLayers

    /**
     * Container of the parent HTML element, required for rendering of the vertex info table.
     */
    private var _parentHtmlElement: Option[html.Element] = None

    override def render(parent: html.Element) {
        super.render(parent)

        window.onresize = {
            _ =>
                updateCanvasSize()
                redraw()
        }

        _parentHtmlElement = Some(parent)
        updateCanvasSize()
    }

    override def destroy() {
        super.destroy()
        window.onresize = {
            _ =>
        }

        if (graphView.isDefined) {
            graphView.get.destroy()
        }

        graphView = None
        mouseIsDragging = false
        mousePressedVertex = false
        mouseDownPosition = Point2D(0, 0)

        currentInfoTable.foreach(_.destroy())
    }

    override def updateGraph(graph: Option[Graph], contractLiterals: Boolean = true, resultsCount: Option[Int]) {
        // If the graph has changed, update the graph view.
        zoomControls.reset()
        if (graph != currentGraph) {
            if (graph.isDefined) {
                if (graphView.isEmpty) {
                    graphView = Some(new views.graph.visual.graph.GraphView(contractLiterals))
                }
                graphView.get.update(graph.get, topLayer.getCenter)
                graphView.foreach {
                    gV =>
                        gV.setConfiguration(currentCustomization)
                        _parentHtmlElement.foreach(gV.render(_))
                }
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
    }

    override def renderControls(toolbar: html.Element) {
        zoomControls.render(toolbar)
        animationStopButton.render(toolbar)
        animationStopButton.setIsEnabled(false)
        visualTools.render(toolbar)
        //languageMenu.render(toolbar) TODO
    }

    override def destroyControls() {
        zoomControls.destroy()
        animationStopButton.destroy()
        visualTools.destroy()
        //languageMenu.destroy() TODO
    }

    /**
     * Redraw method for during-animation-redrawing.
     */
    protected def redrawQuick() {
        graphView.map(_.redraw(layerPack, RedrawOperation.Animation))
    }

    /**
     * Full graph redraw.
     */
    def redraw() {
        graphView.map(_.redrawAll(layerPack))
    }

    /**
     * Specific redraw method for vertex selection.
     */
    def redrawSelection() {
        graphView.map(_.redraw(layerPack, RedrawOperation.Selection))
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
                redrawSelection()
            }

            vertex.foreach {
                v =>
                    if (v.selected && graphView.get.getAllSelectedVerticesCount == 1) {

                        createInfoTable(v)

                        vertexSelected.trigger(new VertexEventArgs[this.type](this, v.vertexModel))
                    }
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
        vertexView.edges.foreach {
            edgeView =>
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
        val selectedVertices = if (graphView.isDefined) {
            graphView.get.getAllVertices.filter(_.selected)
        } else {
            List()
        }

        if (!mouseIsDragging && !mousePressedVertex && !eventArgs.shiftKey) {
            //deselect all

            graphView.get.deselectAll()
            redrawSelection()
        } else if (mouseIsDragging && selectedVertices.length == 1) {
            //if mouse was drawgging only one vertex display the vertexInfoTable again

            val selectedVertex = selectedVertices(0)
            createInfoTable(selectedVertex)
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

            graphView.map { gv =>
                gv.moveAllSelectedVertices(difference)
                gv.redraw(layerPack, RedrawOperation.Movement)
            }
        } else {
            Animation.clearCurrentTimeout()
            val difference = end - mouseDownPosition

            graphView.map { gv =>
                gv.moveAllVertices(difference)
                gv.redraw(layerPack, RedrawOperation.All)
            }
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
        graphView.get.getAllVertices.foreach {
            vv =>
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
        Point2D(eventArgs.clientX - layerPack.offset.x, eventArgs.clientY - layerPack.offset.y + window.scrollY)
    }

    /**
     * Routine called on window resize event.
     */
    private def updateCanvasSize() {
        layerPack.size = Vector2D(window.innerWidth, window.innerHeight) - (topLayer.offset - Vector2D(0,
            window.scrollY))
    }
}
