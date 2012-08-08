package cz.payola.web.client.views.graph.visual.graph

import s2js.adapters.html
import cz.payola.common.visual.Color
import cz.payola.web.client.views.graph.visual.settings.TextSettingsModel
import cz.payola.web.client.views.algebra._
import s2js.adapters.html._

/**
 * Graphical representation of textual data in the drawn graph.
 * @param data that are visualised (by toString function of this object)
 * @param settings how to draw this informationView
 */
class InformationView(data: Any, val settings: TextSettingsModel) extends View[html.elements.CanvasRenderingContext2D]
{
    def isSelected: Boolean = {
        false
    }

    def draw(context: elements.CanvasRenderingContext2D, positionCorrection: Vector2D) {
        drawQuick(context, positionCorrection)
    }

    def drawQuick(context: elements.CanvasRenderingContext2D, positionCorrection: Vector2D) {
        performDrawing(context, settings.color, Point2D(positionCorrection.x, positionCorrection.y))
    }

    /**
     * Performs the InformationView specific drawing routine. Draws the textual data to the specified location.
     * @param context to which text is drawn
     * @param color in which the text is draw
     * @param position where the text is drawn
     */
    private def performDrawing(context: elements.CanvasRenderingContext2D, color: Color, position: Point2D) {
        val textWidth = context.measureText(data.toString).width
        drawRoundedRectangle(context, position + Vector2D(-textWidth / 2, -15), Vector2D(textWidth, 20), 4)
        fillCurrentSpace(context, settings.colorBackground)
        //TODO how come, that the measureText returns different size on the first run??

        drawText(context, data.toString, position, color, settings.font, settings.align)
    }
}
