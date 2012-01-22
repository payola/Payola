package cz.payola.web.client.views.graph

import s2js.adapters.js.dom.CanvasRenderingContext2D
import cz.payola.web.client.views.{Constants, Color, Point}
import cz.payola.web.client.model.graph.ModelObject

case class InformationView(modelObject: ModelObject) extends View {

    def draw(context: CanvasRenderingContext2D, color: Color, position: Point) {

        val colorToUse = if(color != null) {
            color
        } else {
            Constants.ColorText
        }
        drawText(context, modelObject.uri, position, colorToUse, "12px Sans", "center")
    }
}
