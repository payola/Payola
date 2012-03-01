package cz.payola.web.client.views.plugins.visual.graph

import s2js.adapters.js.dom.CanvasRenderingContext2D
import cz.payola.web.client.views.plugins.visual.{Color, Point}
import cz.payola.web.client.views.plugins.visual.Constants._
import collection.mutable.ListBuffer
import cz.payola.common.rdf.{LiteralVertex, IdentifiedVertex, Vertex}

class VertexView(val vertexModel: Vertex, var position: Point) extends View {
    var selected = false

    var edges = ListBuffer[EdgeView]()

    val information: Option[InformationView] = vertexModel match {
        case i: LiteralVertex => Some(new InformationView(i))
        case i: IdentifiedVertex => Some(new InformationView(i))
        case _ => None
    }

    def draw(context: CanvasRenderingContext2D, color: Option[Color], positionCorrection: Option[Point]) {
        
        val correction = positionCorrection.getOrElse(Point.Zero).toVector

        drawRoundedRectangle(context, this.position + (VertexSize / -2) + correction, VertexSize, VertexCornerRadius)

        val colorToUse = color.getOrElse(ColorVertexDefault)

        fillCurrentSpace(context, colorToUse)
    }
}