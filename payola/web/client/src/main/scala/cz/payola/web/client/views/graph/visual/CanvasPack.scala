package cz.payola.web.client.views.graph.visual

import cz.payola.web.client.views.elements.Canvas
import cz.payola.web.client.views.algebra.Vector2D

class CanvasPack(val controls: Canvas, val edgesDeselected: Canvas, val edgesSelected: Canvas,
    val verticesDeselected: Canvas, val verticesSelected: Canvas) {

    def width = controls.htmlElement.width

    def height = controls.htmlElement.height

    def offset: Vector2D = {
        controls.offset
    }

    def clear() {
        getLayers.foreach(_.clear())
    }

    def clearForMovement() {
        edgesSelected.clear()
        verticesSelected.clear()
    }

    def getLayers: Seq[Canvas] = {
        List(edgesDeselected, edgesSelected, verticesDeselected,verticesSelected, controls)
    }

    def size: Vector2D = {
        controls.size
    }

    def size_=(value: Vector2D) {
        getLayers.foreach(_.size = value)
    }

    def getCenter = controls.getCenter
}
