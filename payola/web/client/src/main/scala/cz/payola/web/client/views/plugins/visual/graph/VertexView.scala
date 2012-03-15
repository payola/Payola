package cz.payola.web.client.views.plugins.visual.graph

import collection.mutable.ListBuffer
import cz.payola.common.rdf.{LiteralVertex, IdentifiedVertex, Vertex}
import cz.payola.web.client.views.plugins.visual.{Vector, Color, Point}
import s2js.adapters.js.dom.CanvasRenderingContext2D

/**
  * Graphical representation of Vertex object in the drawn graph.
  * @param vertexModel the vertex object from the model, that is visualised
  * @param position of this graphical representation in drawing space
  */
class VertexView(val vertexModel: Vertex, var position: Point) extends View {
    /**
      * Default radius of circles in corners.
      * has to be 0 <= x <= Min(VERTEX_HEIGHT, VERTEX_WIDTH)/2 see Drawer.drawVertex(..)
      */
    private val defVertexCornerRadius: Double = 5

    /**
      * Default dimensions of a vertex.
      */
    private val defVertexSize = Vector(30, 24)

    /**
      * Default color of a vertex.
      */
    private val defColor1 = new Color(0, 180, 0, 0.8)
    private val defColor2 = new Color(0, 0, 200, 0.8)

    private val literalIcon = "/assets/images/book-icon_01.png"
    private val identifiedIcon = "/assets/images/view-eye-icon.png"
    private val unknownIcon = "/assets/images/question-mark-icon.png"
    
    private val image = prepareImage( Color.Black, vertexModel match {
        case i: LiteralVertex => literalIcon
        case i: IdentifiedVertex => identifiedIcon
        case _ => unknownIcon
    }
    )

    /**
      * Indicator of isSelected attribute. Does not effect inner mechanics.
      */
    var selected = false

    /**
      * List of edges that this vertex representation has. Allows to Iterate throught the graphical representation
      * of the graph.
      */
    var edges = ListBuffer[EdgeView]()
    
    
    /**
      * Textual data that should be visualised with this vertex ("over this vertex").
      */
    val information: Option[InformationView] = vertexModel match {
        case i: LiteralVertex => Some(new InformationView(i))
        case i: IdentifiedVertex => Some(new InformationView(i))
        case _ => None
    }

    def isPointInside(point: Point): Boolean = {
        isPointInRect(point, position + (defVertexSize / -2), position + (defVertexSize / 2))
    }

    def draw(context: CanvasRenderingContext2D, color: Option[Color], positionCorrection: Option[Point]) {
        val correctedPosition = this.position + (defVertexSize / -2) + positionCorrection.getOrElse(Point.Zero).toVector

        drawRoundedRectangle(context, correctedPosition, defVertexSize, defVertexCornerRadius)


        val colorToUse = if(!selected) {
            vertexModel match {
                case i: LiteralVertex => defColor1
                case i: IdentifiedVertex => defColor2
                case _ => color.getOrElse(defColor1)
            }
        } else {
            color.getOrElse(defColor1)
        }

        drawImage(context, prepareImage(colorToUse, literalIcon), position + Vector(-10, -10)/*correctedPosition*/, Vector(20, 20))

        /*TODO drawing the image straight out works, but is slow during redrawing...might be useful to contain it in
        a separate canvas and when necessary draw the canvas*/
    }
}