package cz.payola.web.client.views.plugins.visual

import animation.Animation
import cz.payola.web.client.views.plugins.Plugin
import graph.{InformationView, VertexView, GraphView}
import s2js.adapters.js.dom.Element
import collection.mutable.ListBuffer
import settings.components.visualsetup.VisualSetup
import s2js.adapters.js.browser.document
import cz.payola.web.client.mvvm_api.element.CanvasPack
import cz.payola.web.shared.GraphFetcher
import cz.payola.web.client.events.{DoubleClickedEventArgs, EventArgs, DraggedEventArgs, MouseDownEventArgs}
import cz.payola.common.rdf.{IdentifiedVertex, LiteralVertex, Graph}
import s2js.adapters.js.browser.window

/**
  * Representation of visual based output drawing plugin
  */
abstract class VisualPlugin(settings: VisualSetup) extends Plugin
{

    /**
      * Variable helping during movement of vertices. Contains position where the movement of a vertex tarted.
      */
    private var moveStart: Point = Point(0, 0)

    /**
      * Contained graph in visualisation packing.
      */
    var graphView: Option[GraphView] = None

    var animation: Option[Animation[VertexView]] = None

    var animationNumber = -1

    def init(container: Element) {

        graphView = Some(new GraphView(container, settings))

        graphView.get.canvasPack.mouseDown += { event =>
            moveStart = getPosition(event)
            onMouseDown(event)
            false
        }

        graphView.get.canvasPack.mouseDragged += { event => //vertices move
            onMouseDrag(event)
            false
        }

        graphView.get.canvasPack.mouseDblClicked += { event => //update graph
            onMouseDoubleClick(event)
            false
        }
    }
    
    def update(graph: Graph) {
        graphView.get.update(graph)
    }

    def clean() {
        if(graphView.isDefined) {
            graphView.get.clean()
            graphView = None
            moveStart = Point(0, 0)
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
      * @param event
      */
    private def onMouseDown(event: MouseDownEventArgs[CanvasPack]) {

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
                    resultedAnimation = Some(new Animation(Animation.showText, toAnimate, None,
                        redrawSelection, redrawSelection, None))
                } else {
                    redrawSelection()
                }
            } else { //deselect all and select the pressed one
                if (!vertex.get.selected) {
                    graphView.get.deselectAll()
                }
                moveStart = position
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

        } else { // Mouse down somewhere in between-vertex space.
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
      * @param eventArgs
      */
    private def onMouseDrag(eventArgs: DraggedEventArgs[CanvasPack]) {
        Animation.clearCurrentTimeout()
        val end = getPosition(eventArgs)
        val difference = end - moveStart

        graphView.get.moveAllSelectedVertices(difference)

        moveStart = end
        graphView.get.redraw(RedrawOperation.Movement)
    }

    private def onMouseDoubleClick(eventArgs: DoubleClickedEventArgs[CanvasPack]) {
        val vertex = graphView.get.getTouchedVertex(getPosition(eventArgs))
        if(vertex.isDefined) {
            vertex.get.vertexModel match {
                case i: IdentifiedVertex =>
                    val neighborhood = GraphFetcher.getNeighborhoodOfVertex(i.uri)
                    graphView.get.selectVertex(vertex.get)
                    update(neighborhood)
                case _ =>
            }
        }
    }

    private def getPosition(eventArgs: EventArgs[CanvasPack]): Point = {

        val positionCorrection = Vector(graphView.get.canvasPack.offsetLeft, graphView.get.canvasPack.offsetTop)

        /*if (typeOf(event.clientX) != "undefined" && typeOf(event.clientY) != "undefined") { TODO this check was fine
            Point(event.clientX, event.clientX) + positionCorrection
        }
        else {*/
            Point(eventArgs.clientX + document.body.scrollLeft + document.documentElement.scrollLeft,
                eventArgs.clientY + document.body.scrollTop + document.documentElement.scrollTop) +
            positionCorrection
        //}
    }
}
