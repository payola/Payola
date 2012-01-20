package cz.payola.web.client.views.graph

import s2js.adapters.js.dom.CanvasRenderingContext2D
import cz.payola.web.client.views.{Constants, Color, Point}
import cz.payola.web.client.model.graph.ModelObject

case class InformationView(modelObject: ModelObject) extends View {

    def draw(context: CanvasRenderingContext2D, color: Color, position: Point) {
        context.fillStyle = if(color != null) {
            color.toString
        } else {
            Constants.ColorText.toString
        }
        context.font = "12px Sans"
        context.textAlign = "center"

        context.fillText(modelObject.uri, position.x, position.y)
    }
}
