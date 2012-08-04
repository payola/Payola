package cz.payola.web.client.views.elements

import s2js.adapters.js.html
import s2js.adapters.js.browser.window
import cz.payola.web.client.views._
import cz.payola.web.client.views.algebra.Vector2D
import s2js.adapters.html.elements._

class Canvas(cssClass: String = "") extends ElementView[html.elements.Canvas]("canvas", Nil, cssClass)
{
    val context = htmlElement.getContext[html.elements.CanvasRenderingContext2D]("2d")

    protected var cleared = false

    size = Vector2D(window.innerWidth / 2, window.innerHeight / 2)

    def size: Vector2D = {
        Vector2D(htmlElement.width, htmlElement.height)
    }

    def size_=(value: Vector2D) {
        htmlElement.width = value.x
        htmlElement.height = value.y
    }

    def sizeGet(): Vector2D = {
        Vector2D(htmlElement.width, htmlElement.height)
    }

    /**
     * Returns whether the canvas is cleared.
     */
    def isClear: Boolean = cleared

    /**
     * Clears the whole canvas from all drawn elements.
     */
    def clear() {
        context.clearRect(0, 0, htmlElement.width, htmlElement.height)
        cleared = true
    }

    /**
     * Marks canvas as "clear required to allow drawing".
     */
    def dirty() {
        cleared = false
    }
}
