package cz.payola.web.client.views.visualPlugin

import cz.payola.web.client.views.Plugin
import graph.GraphView
import s2js.adapters.js.dom.Element
import s2js.adapters.goog.events._
import cz.payola.common.rdf.{Graph, Edge, Vertex}

abstract class VisualPlugin(graph: Graph, element: Element) extends Plugin
{
    private var moveStart: Option[Point] = None
    
    protected val graphView = new GraphView(graph, element)

    def init() {
        listen[BrowserEvent](graphView.controlsLayer.canvas, EventType.MOUSEDOWN, onMouseDown _)
        listen[BrowserEvent](graphView.controlsLayer.canvas, EventType.MOUSEMOVE, onMouseMove _)
        listen[BrowserEvent](graphView.controlsLayer.canvas, EventType.MOUSEUP, onMouseUp _)
    }

    def update(vertices: Seq[Vertex], edges: Seq[Edge]) {

    }
    
    def clean() {
        
    }

    def redraw() {
        graphView.redrawAll()
    }

    private def onMouseDown(event: BrowserEvent) {
        val position = Point(event.clientX, event.clientY)
        val vertex = graphView.getTouchedVertex(position)
        var needsToRedraw = false;

        // Mouse down near a vertex.
        if (vertex.isDefined) {
            if (event.shiftKey) {
                needsToRedraw = graphView.invertVertexSelection(vertex.get) || needsToRedraw
            } else {
                if (!vertex.get.selected) {
                    needsToRedraw = graphView.deselectAll()
                }
                moveStart = Some(position)
                needsToRedraw = graphView.selectVertex(vertex.get) || needsToRedraw
            }

            // Mouse down somewhere in the inter-vertex space.
        } else {
            if (!event.shiftKey) {
                needsToRedraw = graphView.deselectAll()
            }
        }

        if (needsToRedraw) {
            graphView.redraw(RedrawOperation.Selection)
        }
    }

    private def onMouseMove(event: BrowserEvent) {
        if (moveStart.isDefined) {
            val end = Point(event.clientX, event.clientY)
            val difference = end - moveStart.get

            graphView.moveAllSelectedVetrtices(difference)

            moveStart = Some(end)
            graphView.redraw(RedrawOperation.Movement)
        }
    }

    private def onMouseUp(event: BrowserEvent) {
        moveStart = None
    }
}
