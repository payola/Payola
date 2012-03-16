package cz.payola.web.client.views.plugins.visual.graph

import collection.mutable.ListBuffer
import cz.payola.common.rdf.{LiteralVertex, IdentifiedVertex, Vertex}
import cz.payola.web.client.views.plugins.visual.{Vector, Color, Point}
import s2js.adapters.js.dom.{Canvas, CanvasRenderingContext2D}

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

    private val literalIcon = "/assets/images/book-icon.png"
    private val identifiedIcon = "/assets/images/view-eye-icon.png"
    private val unknownIcon = "/assets/images/question-mark-icon.png"
    
    private var image: Option[Canvas] = None
    private var previousColor: Option[Color] = None

    /**
      * Indicator of isSelected attribute. Does not effect inner mechanics.
      */
    var selected = false

    /**
      * List of edges that this vertex representation has. Allows to Iterate through the graphical representation
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
        //TODO might look good to draw the rectangle gray and red when selected, the icon should be in color of the "type"

        val colorToUse = if(!selected) {
            vertexModel match {
                case i: LiteralVertex => defColor1
                case i: IdentifiedVertex => defColor2
                case _ => color.getOrElse(defColor1)
            }
        } else {
            color.getOrElse(defColor1)
        }

        val correctedPosition = this.position + (defVertexSize / -2) + positionCorrection.getOrElse(Point.Zero).toVector

        drawRoundedRectangle(context, correctedPosition, defVertexSize, defVertexCornerRadius)
        fillCurrentSpace(context, colorToUse)

        if((image.isEmpty && previousColor.isEmpty) || (previousColor.get != colorToUse)) {
            previousColor = Some(colorToUse)
            image = Some(prepareImage(colorToUse, vertexModel match {
                case i: LiteralVertex => literalIcon
                case i: IdentifiedVertex => identifiedIcon
                case _ => unknownIcon
            }
            ))
        }
        drawImage(context, image.get, position + Vector(-10, -10), Vector(20, 20))

        /*TODO drawing is successful only on the second redraw...why!?*/
    }
}