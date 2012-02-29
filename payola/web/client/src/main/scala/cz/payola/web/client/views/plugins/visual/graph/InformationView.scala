package cz.payola.web.client.views.plugins.visual.graph

import s2js.adapters.js.dom.CanvasRenderingContext2D
import cz.payola.web.client.views.plugins.visual.{Vector, Constants, Color, Point}

case class InformationView(data: String) extends View {
    private var selected = false

    def setSelectedForDrawing() {
        selected = true
    }
    def draw(context: CanvasRenderingContext2D, color: Option[Color], position: Option[Point]) {
        
        if(position != None) {
            performDrawing(context, color, position.get)
        }
        
        selected = false
    }
    
    def performDrawing(context: CanvasRenderingContext2D, color: Option[Color], position: Point) {
        if(selected) {
            /*val colorToUseBackground = color.getOrElse(Constants.ColorTextBackground)*/

            drawCircle(context, position + Vector(1, -5), 10, Constants.EdgeWidth, Constants.ColorTextBackground)
            fillCurrentSpace(context, Constants.ColorTextBackground)
        }

        val colorToUse = color.getOrElse(Constants.ColorText)

        drawText(context, data, position, colorToUse, "12px Sans", "center")
    }
}
