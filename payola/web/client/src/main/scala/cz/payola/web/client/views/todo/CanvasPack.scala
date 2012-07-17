package cz.payola.web.client.views.todo

import cz.payola.web.client.views.elements.Canvas

class CanvasPack(val edgesDeselected: Canvas, val edgesSelected: Canvas,
    val verticesDeselected: Canvas, val verticesSelected: Canvas){

    def clear() {
        edgesDeselected.clear()
        edgesSelected.clear()
        verticesDeselected.clear()
        verticesSelected.clear()
    }

    def clearForMovement() {
        edgesSelected.clear()
        verticesSelected.clear()
    }
}
