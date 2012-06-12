package cz.payola.web.client.mvvm.element

import cz.payola.web.client.mvvm.Component
import s2js.adapters.js.dom
import dom.{Element, CanvasRenderingContext2D}
import s2js.adapters.js.browser.document
import cz.payola.web.client.views.plugins.visual.{Point, Vector}

/**
 * Representation of a space for drawing into a web page.
 */
class Canvas(width: Double, height: Double) extends Component {

    protected var mousePressed = false

    protected var shiftPressed = false

    val canvasElement = document.createElement[dom.Canvas]("canvas")
    canvasElement.width =  width
    canvasElement.height = height

    /**
     * Object for drawing
     */
    val context = canvasElement.getContext[CanvasRenderingContext2D]("2d")

    /**
     * Indicator whether is the layer ready for drawing into.
     */
    protected var cleared = false

    def isCleared: Boolean = {
        cleared
    }

    /**
     * Setter of canvas dimensions.
     * @param size new dimensions.
     */
    def setSize(size: Vector) {
        canvasElement.width = size.x
        canvasElement.height = size.y
    }

    /**
     * Getter of canvas dimensions.
     * @return dimensions
     */
    def getSize: Vector = {
        Vector(canvasElement.width, canvasElement.height)
    }

    def render(parent: Element) {
        parent.appendChild(canvasElement)
    }

    /**
     * Clears the whole area from all drawn elements
     */
    def clear() {
        context.clearRect(0, 0, canvasElement.width, canvasElement.height)
        cleared = true
    }

    /**
     * Marks canvas as "clear required to allow drawing".
     */
    def dirty() {
        cleared = false
    }
}
