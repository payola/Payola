package cz.payola.web.client.views.plugins.visual

import cz.payola.web.client.views.plugins.Plugin
import graph.GraphView
import s2js.adapters.js.dom.Element
import s2js.adapters.goog.events._
import cz.payola.common.rdf.Graph

abstract class VisualPlugin extends Plugin
{
    private var moveStart: Option[Point] = None
    
    var graphView: Option[GraphView] = None

    def init(graph: Graph, container: Element) {
        graphView = Some(new GraphView(graph, container))
        listen[BrowserEvent](graphView.get.controlsLayer.canvas, EventType.MOUSEDOWN, onMouseDown _)
        listen[BrowserEvent](graphView.get.controlsLayer.canvas, EventType.MOUSEMOVE, onMouseMove _)
        listen[BrowserEvent](graphView.get.controlsLayer.canvas, EventType.MOUSEUP, onMouseUp _)
    }

    def update(graph: Graph) {

    }
    
    def clean() {
        
    }

    def redraw() {
        graphView.get.redrawAll()
    }

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

    private def onMouseMove(event: BrowserEvent) {
        if (moveStart.isDefined) {
            val end = Point(event.clientX, event.clientY)
            val difference = end - moveStart.get

            graphView.get.moveAllSelectedVetrtices(difference)

            moveStart = Some(end)
            graphView.get.redraw(RedrawOperation.Movement)
        }
    }

    private def onMouseUp(event: BrowserEvent) {
        moveStart = None
    }
}
