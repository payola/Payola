package cz.payola.web.client.views.graph

import s2js.adapters.goog.events._
import s2js.adapters.goog.events.{EventType, BrowserEvent}
import cz.payola.web.client.views.{Constants, Layer, Point}

class Controls(val graphView: GraphView, val layer: Layer) {

    var selectedCount = 0

    private var selectionStart: Option[Point] = None

    private var moveStart: Option[Point] = None

    def init() {

        listen[BrowserEvent](layer.canvas, EventType.MOUSEDOWN, onMouseDown _)
        listen[BrowserEvent](layer.canvas, EventType.MOUSEMOVE, onMouseMove _)
        listen[BrowserEvent](layer.canvas, EventType.MOUSEUP, onMouseUp _)
    }

    private def onMouseDown(event: BrowserEvent) {
        val position = Point(event.clientX, event.clientY)
        val vertex = getTouchedVertex(position)
        var needsToRedraw = false;

        // Mouse down near a vertex.
        if (vertex.isDefined) {
            if (event.shiftKey) {
                needsToRedraw = invertVertexSelection(vertex.get) || needsToRedraw
            } else {
                if (!vertex.get.selected) {
                    needsToRedraw = deselectAll(graphView)
                }
                moveStart = Some(position)
                needsToRedraw = selectVertex(vertex.get) || needsToRedraw
            }

            // Mouse down somewhere in the inter-vertex space.
        } else {
            if (!event.shiftKey) {
                needsToRedraw = deselectAll(graphView)
            }
            selectionStart = Some(position)
        }

        if (needsToRedraw) {
            graphView.redraw()
        }
    }

    private def onMouseMove(event: BrowserEvent) {
        if (selectionStart.isDefined) {
            //TODO place to write "rectangle selection" code

        } else if (moveStart.isDefined) {
            val end = Point(event.clientX, event.clientY)
            val difference = end - moveStart.get

            graphView.vertexViews.foreach { vertex =>
                if(vertex.selected) {
                    vertex.position += difference
                }
            }

            moveStart = Some(end)
            graphView.redraw()
        }
    }

    private def onMouseUp(event: BrowserEvent) {
        selectionStart = None
        moveStart = None
        graphView.redraw()
    }

    def getTouchedVertex(p: Point): Option[VertexView] = {
        import cz.payola.web.client.views.Constants._
        graphView.vertexViews.find(v => isPointInRect(p, v.position + (VertexSize / -2), v.position + (VertexSize / 2)))
    }

    def isPointInRect(p: Point, topLeft: Point, bottomRight: Point): Boolean = {
        p >= topLeft && p <= bottomRight
    }

    def setVertexSelection(vertex: VertexView, selected: Boolean): Boolean = {
        if (vertex.selected != selected) {
            selectedCount += (if (selected) 1 else -1)
            vertex.selected = selected
            true
        } else {
            false
        }
    }

    def selectVertex(vertex: VertexView): Boolean = {
        setVertexSelection(vertex, true)
    }

    def deselectVertex(vertex: VertexView): Boolean = {
        setVertexSelection(vertex, false)
    }

    def invertVertexSelection(vertex: VertexView): Boolean = {
        setVertexSelection(vertex, !vertex.selected)
    }

    def deselectAll(graph: GraphView): Boolean = {
        var somethingChanged = false
        if (selectedCount > 0) {
            graphView.vertexViews.foreach {vertex =>
                somethingChanged = deselectVertex(vertex) || somethingChanged
            }
            selectedCount = 0
        }
        somethingChanged
    }
}