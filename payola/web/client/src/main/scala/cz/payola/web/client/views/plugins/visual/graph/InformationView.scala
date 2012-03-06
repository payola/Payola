package cz.payola.web.client.views.plugins.visual.graph

import s2js.adapters.js.dom.CanvasRenderingContext2D
import cz.payola.web.client.views.plugins.visual.{Vector, Color, Point}

/**
  * Graphical representation of textual data in the drawn graph.
  * @param data that are visualised (by toString function of this object)
  */
case class InformationView(data: Any) extends View {
    /**
      * Default color of text.
      */
    private val defColor = new Color(200, 200, 200, 1)

    /**
      * Default color of background behind text.
      */
    private val defColorBackground = new Color(255, 255, 255, 0.5)

    /**
      * Default width of line (used in background drawing).
      */
    private val lineWidth: Double = 1

    /**
      * Indicator of isSelected attribute. If is selected, the text is drawn with a small background white
      * see-through circle.
      */
    private var selected = false

    /**
      * Sets the selected attribute to true. After drawing of this object the attribute is set back to false.
      */
    def setSelectedForDrawing() {
        selected = true
    }

    def draw(context: CanvasRenderingContext2D, color: Option[Color], position: Option[Point]) {

        if(position != None) {
            performDrawing(context, color, position.get)
        }

        selected = false
    }

    /**
      * Performs the InformationView specific drawing routine. Draws the textual data to the specified location.
      * @param context to which text is drawn
      * @param color in which the text is draw
      * @param position where the text is drawn
      */
    private def performDrawing(context: CanvasRenderingContext2D, color: Option[Color], position: Point) {
        if(selected) {
            /*val colorToUseBackground = color.getOrElse(defColorBackground)*/

            drawCircle(context, position + Vector(1, -5), 10, lineWidth, defColorBackground)
            fillCurrentSpace(context, defColorBackground)
        }

        val colorToUse = color.getOrElse(defColor)

        drawText(context, data.toString, position, colorToUse, "12px Sans", "center")
    }
}
