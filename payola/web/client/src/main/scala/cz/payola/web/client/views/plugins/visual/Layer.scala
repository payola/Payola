package cz.payola.web.client.views.plugins.visual

import s2js.adapters.js.dom.{CanvasRenderingContext2D, Canvas}

/**
  * Representation of a space for drawing into a web page.
  * @param canvas object specifying sizes of the context
  * @param context object for drawing
  */
class Layer(val canvas: Canvas, val context: CanvasRenderingContext2D)
{
    /**
      * Indicator whether is the layer ready for drawing into.
      */
    var cleared = false

    /**
      * Setter of canvas dimensions.
      * @param size new dimensions.
      */
    def setSize(size: Vector) {
        canvas.width = size.x;
        canvas.height = size.y;
    }

    /**
      * Getter of canvas dimensions.
      * @return dimensions
      */
    def getSize: Vector = {
        Vector(canvas.width, canvas.height)
    }
}
