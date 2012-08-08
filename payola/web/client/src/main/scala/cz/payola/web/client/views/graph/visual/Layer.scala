package cz.payola.web.client.views.graph.visual

import s2js.adapters.html
import cz.payola.web.client.views.algebra.Vector2D

/**
 * Representation of a space for drawing into a web page.
 * @param canvas object specifying sizes of the context
 * @param context object for drawing
 */
class Layer(val canvas: html.elements.Canvas, val context: html.elements.CanvasRenderingContext2D) //TODO delete
{
    /**
     * Indicator whether is the layer ready for drawing into.
     */
    var cleared = false

    /**
     * Setter of canvas dimensions.
     * @param size new dimensions.
     */
    def setSize(size: Vector2D) {
        canvas.width = size.x;
        canvas.height = size.y;
    }

    /**
     * Getter of canvas dimensions.
     * @return dimensions
     */
    def getSize: Vector2D = {
        Vector2D(canvas.width, canvas.height)
    }
}
