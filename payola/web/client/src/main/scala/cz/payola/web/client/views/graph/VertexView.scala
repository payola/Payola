package cz.payola.web.client.views.graph

import s2js.adapters.js.dom.CanvasRenderingContext2D
import cz.payola.web.client.views.Constants._
import cz.payola.web.client.views.{Color, Point, Vector}
import collection.mutable.ListBuffer
import cz.payola.common.rdf.Vertex

class VertexView(val vertexModel: Vertex, var position: Point) extends View
{
    var selected = false

    var force = Vector(0, 0) //required by the gravity model calculations

    var velocity = Vector(0, 0)

    var edges = ListBuffer[EdgeView]()

    val information: InformationView = InformationView(vertexModel)

    def draw(context: CanvasRenderingContext2D, color: Color, positionCorrection: Point) {
        val correction = if (positionCorrection != null) {
            positionCorrection.toVector
        } else {
            Point.Zero.toVector
        }

        drawRoundedRectangle(context, this.position + (VertexSize / -2) + correction, VertexSize, VertexCornerRadius)

        val colorToUse = if (color != null) {
            color
        } else {
            ColorVertexDefault
        }

        fillCurrentSpace(context, colorToUse)
    }
}
