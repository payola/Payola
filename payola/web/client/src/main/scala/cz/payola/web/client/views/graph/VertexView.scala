package cz.payola.web.client.views.graph

import s2js.adapters.js.dom.CanvasRenderingContext2D
import cz.payola.web.client.views.Constants._
import cz.payola.web.client.views.{Color, Point}
import collection.mutable.ListBuffer
import cz.payola.common.rdf.{IdentifiedVertex, Vertex}

class VertexView(val vertexModel: Vertex, var position: Point) extends View {
    var selected = false

    var edges = ListBuffer[EdgeView]()

    val information: Option[InformationView] = vertexModel match {
        case i: IdentifiedVertex => Some(new InformationView(i))
        case _ => None
    }

    def draw(context: CanvasRenderingContext2D, color: Option[Color], positionCorrection: Option[Point]) {
        
        val correction = positionCorrection match {
            case None => Point.Zero.toVector
            case _ => positionCorrection.get.toVector
        }

        drawRoundedRectangle(context, this.position + (VertexSize / -2) + correction, VertexSize, VertexCornerRadius)

        val colorToUse = color match {
            case None => ColorVertexDefault
            case _ => color.get
        }

        fillCurrentSpace(context, colorToUse)
    }
}