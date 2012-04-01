package cz.payola.web.client.views.plugins.visual

import cz.payola.web.client.views.plugins.Plugin
import graph.GraphView
import s2js.adapters.js.dom.Element
import s2js.adapters.goog.events._
import cz.payola.common.rdf.Graph
import s2js.adapters.js.browser.window
import s2js.compiler.javascript

/**
  * Representation of visual based output drawing plugin
  */
abstract class VisualPlugin extends Plugin
{

    private var animationInterval = 0

    /**
      * Variable helping during movement of vertices. Contains position where the movement of a vertex tarted.
      */
    private var moveStart: Option[Point] = None

    /**
      * Contained graph in visualisation packing.
      */
    var graphView: Option[GraphView] = None

    def init(container: Element) {

        graphView = Some(new GraphView(container))

        listen[BrowserEvent](graphView.get.controlsLayer.canvas, EventType.MOUSEDOWN, onMouseDown _)
        listen[BrowserEvent](graphView.get.controlsLayer.canvas, EventType.MOUSEMOVE, onMouseMove _)
        listen[BrowserEvent](graphView.get.controlsLayer.canvas, EventType.MOUSEUP, onMouseUp _)
    }

    def updateSettings(loader: SetupLoader) {
        if(graphView.isDefined) {
            graphView.get.updateSettings(loader)
        }
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

    /**
      * Description of mouse-button-down event. Is called from the layer (canvas) binded to it in the initialization.
      * @param event
      */
    private def onMouseDown(event: BrowserEvent) {
        val position = Point(event.clientX, event.clientY)
        val vertex = graphView.get.getTouchedVertex(position)
        var needsToRedraw = false;

        // Mouse down near a vertex.
        if (vertex.isDefined) {
            if (event.shiftKey) {
                needsToRedraw = graphView.get.invertVertexSelection(vertex.get) || needsToRedraw
            } else {
                if (!vertex.get.selected) {
                    needsToRedraw = graphView.get.deselectAll()
                }
                moveStart = Some(position)
                needsToRedraw = graphView.get.selectVertex(vertex.get) || needsToRedraw
            }

            // Mouse down somewhere in the inter-vertex space.
        } else {
            if (!event.shiftKey) {
                needsToRedraw = graphView.get.deselectAll()
            }
        }

        if (needsToRedraw) {
            graphView.get.redraw(RedrawOperation.Selection)
        }
    }

    /**
      * Description of mouse-move event. Is called from the layer (canvas) binded to it in the initialization.
      * @param event
      */
    private def onMouseMove(event: BrowserEvent) {
        if (moveStart.isDefined) {
            val end = Point(event.clientX, event.clientY)
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
}
