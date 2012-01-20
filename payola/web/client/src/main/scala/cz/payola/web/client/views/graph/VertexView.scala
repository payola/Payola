package cz.payola.web.client.views.graph

import s2js.adapters.js.dom.CanvasRenderingContext2D
import cz.payola.web.client.views.{Color, Point}
import cz.payola.web.client.views.Constants._
import cz.payola.web.client.model.graph.Vertex

class VertexView(val vertexModel: Vertex, var position: Point) extends View {
    var selected = false

    val information: InformationView = InformationView(vertexModel)

    def draw(context: CanvasRenderingContext2D, color: Color, positionCorrection: Point) {
        val correction = if (positionCorrection != null) {
            positionCorrection.toVector
        } else {
            Point.Zero.toVector
        }

        drawRoundedRectangle(context, this.position + (VertexSize / -2) + correction, VertexSize, VertexCornerRadius)

        context.fillStyle = if(color != null) {
            color.toString
        } else {
            ColorVertexDefault.toString
        }
        context.fill()
    }
}
