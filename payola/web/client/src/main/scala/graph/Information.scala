package cz.payola.web.client.graph

import cz.payola.web.client.Point
import s2js.adapters.js.dom.CanvasRenderingContext2D
import Constants._

case class Information(var text: String, var position: Point) {

    private val positionCorrection: Point = Point(0, 4)

    def draw(context: CanvasRenderingContext2D) {
        
        context.fillStyle = ColorText.toString
        context.font = "12px Sans"
        context.textAlign = "center"
        context.fillText(text, position.x + positionCorrection.x, position.y + positionCorrection.y)
    }
}