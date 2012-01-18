package cz.payola.web.client.views.graph

import s2js.adapters.js.dom.CanvasRenderingContext2D
import cz.payola.web.client.views.{Point, Vector}
import cz.payola.web.client.views.Constants._
import cz.payola.web.client.model.graph.Vertex

case class InformationView(val vertexModel: Vertex, var position: Point)
{
    private val positionCorrection = Vector(0, 4)

    def draw(context: CanvasRenderingContext2D) {
        context.fillStyle = ColorText.toString
        context.font = "12px Sans"
        context.textAlign = "center"

        val correctedPosition = position + positionCorrection
        context.fillText(vertexModel.uri, correctedPosition.x, correctedPosition.y)
    }
}
