package cz.payola.web.client.views.graph

import s2js.adapters.js.dom.CanvasRenderingContext2D
import cz.payola.web.client.views.{Vector, Constants, Color, Point}
import cz.payola.common.rdf.IdentifiedObject

case class InformationView(identifiedObject: IdentifiedObject) extends View {
    var selected = false

    def draw(context: CanvasRenderingContext2D, color: Color, position: Point) {
        if(selected) {
            /*val colorToUseBackgroung = if(color != null) {
                color.inverse()
            } else {
                Constants.ColorTextBackground
            }*/

            drawCircle(context, position + Vector(1, -5), 10, Constants.EdgeWidth, Constants.ColorTextBackground)
            fillCurrentSpace(context, Constants.ColorTextBackground)
        }

        val colorToUse = if(color != null) {
            color
        } else {
            Constants.ColorText
        }
        drawText(context, identifiedObject.uri, position, colorToUse, "12px Sans", "center")
    }
}
