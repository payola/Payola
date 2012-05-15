package cz.payola.web.client.views.plugins.visual.graph

import s2js.adapters.js.dom.CanvasRenderingContext2D
import cz.payola.web.client.views.plugins.visual.{SetupLoader, Vector, Color, Point}
import s2js.adapters.js.browser.window;

/**
  * Graphical representation of textual data in the drawn graph.
  * @param data that are visualised (by toString function of this object)
  */
case class InformationView(data: Any) extends View {
    /**
      * Default color of text.
      */
    private var textColor = new Color(50, 50, 50, 1)

    /**
      * Default color of background behind text.
      */
    private var backgroundColor = new Color(255, 255, 255, 0.5)

    /**
      * Default width of line (used in background drawing).
      */
    private val lineWidth: Double = 1

    private var textAlpha: Double = 1

    /**
      * Indicator of isSelected attribute. If is selected, the text is drawn with a small background white
      * see-through circle.
      */
    private var selected = false

    def setTextVisibility(newAlpha: Double) {
        textAlpha = newAlpha
    }

    /**
      * Sets the selected attribute to true. After drawing of this object the attribute is set back to false.
      */
    def setSelectedForDrawing() {
        selected = true
    }

    def draw(context: CanvasRenderingContext2D, color: Option[Color], position: Option[Point]) {

        drawQuick(context, color, position)
    }

    def drawQuick(context: CanvasRenderingContext2D, color: Option[Color], position: Option[Point]) {

        val colorToUse = if(color.isDefined) {
            Color(color.get.red, color.get.green, color.get.blue, textAlpha)
        } else {
            Color(textColor.red, textColor.green, textColor.blue, textAlpha)
        }

        if(position != None) {
            performDrawing(context, colorToUse, position.get)
        }

        selected = false
    }

    /**
      * Performs the InformationView specific drawing routine. Draws the textual data to the specified location.
      * @param context to which text is drawn
      * @param color in which the text is draw
      * @param position where the text is drawn
      */
    private def performDrawing(context: CanvasRenderingContext2D, color: Color, position: Point) {
        if(selected) {
            val textWidth = context.measureText(data.toString).width
            drawRoundedRectangle(context, position + Vector(-textWidth/2, -15), Vector(textWidth, 20), 4)
            fillCurrentSpace(context, backgroundColor)
            //todo how come, that the measureText returns different size on the first run??
        }

        drawText(context, data.toString, position, color, "12px Sans", "center")
    }
}
