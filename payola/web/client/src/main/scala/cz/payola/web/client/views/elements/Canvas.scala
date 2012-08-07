package cz.payola.web.client.views.elements

import s2js.adapters.js.dom
import cz.payola.web.client.views._
import s2js.adapters.js.dom.CanvasRenderingContext2D
import s2js.adapters.js.browser.window
import cz.payola.web.client.views.algebra._

class Canvas(cssClass: String = "") extends ElementView[dom.Canvas]("canvas", Nil, cssClass) {

    val context = domElement.getContext[CanvasRenderingContext2D]("2d")

    protected var cleared = false


    size = Vector2D(window.innerWidth /2, window.innerHeight /2)

    def size: Vector2D = {
        Vector2D(domElement.width, domElement.height)
    }

    def size_=(value: Vector2D) {
        domElement.width = value.x
        domElement.height = value.y
    }

    def getSize(): Vector2D = {
        Vector2D(domElement.width, domElement.height)
    }

    /**
      * Returns whether the canvas is cleared.
      */
    def isClear: Boolean = cleared

    /**
      * Clears the whole canvas from all drawn elements.
      */
    def clear() {
        context.clearRect(0, 0, domElement.width, domElement.height)
        cleared = true
    }

    /**
      * Marks canvas as "clear required to allow drawing".
      */
    def dirty() {
        cleared = false
    }

    def getCenter: Point2D = {
        Point2D(size.x / 2, size.y / 2)
    }
}
