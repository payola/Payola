package cz.payola.web.client.views.graph.visual

import animation.Animation
import cz.payola.web.client.views.graph.PluginView
import collection.mutable.ListBuffer
import settings.components.visualsetup.VisualSetup
import cz.payola.web.client.events._
import cz.payola.common.rdf._
import cz.payola.web.client.presenters.components.ZoomControls
import cz.payola.web.client.views.elements._
import cz.payola.web.client._
import cz.payola.web.client.views.algebra._
import s2js.adapters.js.browser.window
import s2js.adapters.js._
import s2js.compiler.javascript
import cz.payola.web.client.views.graph.visual.graph._
import cz.payola.web.client.views._
import s2js.adapters.js.browser.document
import cz.payola.web.client.views.bootstrap.Icon
import s2js.adapters.js.dom.CanvasContext
import cz.payola.common.entities.settings.OntologyCustomization

/**
  * Representation of visual based output drawing plugin
  */
abstract class VisualPluginView(settings: VisualSetup, name: String) extends PluginView(name)
{
    protected var mouseIsPressed = false

    private var mousePressedVertex = false

    private var mouseIsDragging = false

    private var mouseDownPosition = Point2D(0, 0)

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
        c.domElement.width = topLayer.domElement.width
        c.domElement.height = topLayer.domElement.height

        c.domElement.getContext[CanvasContext]("2d").drawImage(layerPack.edgesDeselected.domElement,0,0)
        c.domElement.getContext[CanvasContext]("2d").drawImage(layerPack.edgesSelected.domElement,0,0)
        c.domElement.getContext[CanvasContext]("2d").drawImage(layerPack.verticesDeselected.domElement,0,0)
        c.domElement.getContext[CanvasContext]("2d").drawImage(layerPack.verticesSelected.domElement,0,0)
        c.domElement.getContext[CanvasContext]("2d").drawImage(topLayer.domElement,0,0)

        window.open(c.domElement.toDataURL("image/png"))
        false
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

    private var destroyVertexInfo: Option[SimpleUnitEvent[Boolean]] = None

    private def triggerDestroyVertexInfo() {
        if(destroyVertexInfo.isDefined) {
            destroyVertexInfo.get.triggerDirectly(true)
            destroyVertexInfo = None
        }
    }

    override def updateOntologyCustomization(newCustomization: Option[OntologyCustomization]) {
        currentCustomization = newCustomization

        if(graphView.isDefined) {
            graphView.get.setVisualSetup(newCustomization)
        }

        settings.setOntologyCustomization(newCustomization)

        redraw()
    }

    def createSubViews = layers

    private var parent : Option[dom.Element] = None

    override def render(parent: dom.Element) {
        super.render(parent)

        setMouseWheelListener()
        fitCanvas()
        this.parent = Some(parent)
    }

    override def updateGraph(graph: Option[Graph]) {
        // If the graph has changed, update the graph view.
        zoomControls.reset()
        if (graph != currentGraph) {
            if(graph.isDefined) {
                if (graphView.isEmpty) {
                    graphView = Some(new views.graph.visual.graph.GraphView(settings))
                }
                graphView.get.update(graph.get)
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
    }

    override def renderControls(toolbar: dom.Element) {
        zoomControls.render(toolbar)
        pngDownloadButton.render(toolbar)
    }

    override def destroyControls() {
        zoomControls.destroy()
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
    private def onMouseDown(eventArgs: BrowserEventArgs[Canvas]) {
        val position = getPosition(eventArgs)
        val vertex = graphView.get.getTouchedVertex(position)

        if (vertex.isDefined) {
            // Mouse down near a vertex.
            if (eventArgs.shiftKey) {
                //change selection of the pressed one
                graphView.get.invertVertexSelection(vertex.get)

                triggerDestroyVertexInfo()

                redrawSelection()
            } else {
                triggerDestroyVertexInfo()

                //deselect all and select the pressed one
                if (!vertex.get.selected) {
                    graphView.get.deselectAll()
                }
                graphView.get.selectVertex(vertex.get)

                vertex.foreach{ v =>
                    if(v.selected) {
                        val infoTable = new VertexInfoTable(v.vertexModel, v.getLiteralVertices, v.position+Vector2D(v.settings.radius(v.rdfType),0))
                        infoTable.vertexBrowsing += { a =>
                            triggerDestroyVertexInfo()
                            vertexBrowsing.trigger(new VertexEventArgs[this.type](this, vertex.get.vertexModel))
                        }
                        infoTable.vertexBrowsingDataSource += { a =>
                            triggerDestroyVertexInfo()
                            vertexBrowsingDataSource.trigger(new VertexEventArgs[this.type](this, vertex.get.vertexModel))
                        }


                        infoTable.render(parent.getOrElse(document.body))

                        destroyVertexInfo = Some(new SimpleUnitEvent[Boolean])
                        destroyVertexInfo.get += { event => infoTable.destroy() }

                        vertexSelected.trigger(new VertexEventArgs[this.type](this, v.vertexModel))
                    }
                }


                redrawSelection()
            }
            mousePressedVertex = true
        } else {
            mousePressedVertex = false
            triggerDestroyVertexInfo()
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
        alterVertexPositions(1 + zoomControls.zoomStep, (- mousePosition.toVector) * zoomControls.zoomStep)
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

    private def getPosition(eventArgs: BrowserEventArgs[Canvas]): Point2D = {
        Point2D(eventArgs.clientX - topLayerOffset.x, eventArgs.clientY - topLayerOffset.y)
    }

    def fitCanvas() {
        topLayerOffset = calculateTopLayerOffset
        val layerSize = Vector2D(window.innerWidth, window.innerHeight) - topLayerOffset
        layers.foreach(_.size = layerSize)
    }


    private def calculateTopLayerOffset: Vector2D = topLayer.topLeftCorner
}
