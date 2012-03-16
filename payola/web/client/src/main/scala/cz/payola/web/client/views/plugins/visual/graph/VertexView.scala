package cz.payola.web.client.views.plugins.visual.graph

import collection.mutable.ListBuffer
import cz.payola.common.rdf.{LiteralVertex, IdentifiedVertex, Vertex}
import cz.payola.web.client.views.plugins.visual.{SetupLoader, Vector, Color, Point}
import s2js.adapters.js.dom.{Element, CanvasRenderingContext2D}

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
    private var defVertexCornerRadius: Double = 5

    /**
      * Default dimensions of a vertex.
      */
    private var defVertexSize = Vector(30, 24)

    /**
      * Default color of a vertex.
      */
    private var colorDefault = new Color(0, 180, 0, 0.8)
    
    private var image = prepareImage(
        vertexModel match {
            case i: LiteralVertex => new Color(200, 0, 0, 1)
            case i: IdentifiedVertex => new Color(0, 200, 0, 1)
            case _ => new Color(0, 0, 200, 1)
        }, vertexModel match {
            case i: LiteralVertex => "/assets/images/book-icon.png"
            case i: IdentifiedVertex => "/assets/images/view-eye-icon.png"
            case _ => "/assets/images/question-mark-icon.png"
        })

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

        val colorToUseOnBox = color.getOrElse(colorDefault)
        val correctedPosition = this.position + (defVertexSize / -2) + positionCorrection.getOrElse(Point.Zero).toVector

        drawRoundedRectangle(context, correctedPosition, defVertexSize, defVertexCornerRadius)
        fillCurrentSpace(context, colorToUseOnBox)

        drawImage(context, image, position + Vector(-10, -10), Vector(20, 20))

        /*TODO drawing is successful only on the second redraw...why!?*/
    }
    
    def updateSettings(settings: Element) {

        defVertexCornerRadius = 5

        defVertexSize = Vector(30, 24)

        val setupNodeCD = getNodeByPath(settings, "setup.vertex.colors.default")
        val resultCD = if(setupNodeCD.isDefined) {
            createColor(setupNodeCD.get)
        } else {
            None
        }
        colorDefault = resultCD.getOrElse(colorDefault)


        val setupNodeLIC = getNodeByPath(settings, "setup.vertex.colors.literal")
        val literalIconColor = if(setupNodeLIC.isDefined) {
            createColor(setupNodeLIC.get)
        } else {
            None
        }

        val setupNodeIIC = getNodeByPath(settings, "setup.vertex.colors.identified")
        val identifiedIconColor = if(setupNodeIIC.isDefined) {
            createColor(setupNodeIIC.get)
        } else {
            None
        }

        val setupNodeUIC = getNodeByPath(settings, "setup.vertex.colors.unknown")
        val unknownIconColor = if(setupNodeUIC.isDefined) {
            createColor(setupNodeUIC.get)
        } else {
            None
        }

        val setupNodeLI = getNodeByPath(settings, "setup.vertex.icons.literal")
        val resultLI = if(setupNodeLI.isDefined){
            setupNodeLI.get.getAttribute("value")
        } else {
            ""
        }
        val literalIcon = if(resultLI.isEmpty) {
            "/assets/images/book-icon.png"
        } else {
            resultLI
        }

        val setupNodeII = getNodeByPath(settings, "setup.vertex.icons.identified")
        val resultII = if(setupNodeII.isDefined){
            setupNodeII.get.getAttribute("value")
        } else {
            ""
        }
        val identifiedIcon = if(resultII.isEmpty){
            "/assets/images/view-eye-icon.png"
        } else {
            resultII
        }

        val setupNodeUI = getNodeByPath(settings, "setup.vertex.icons.unknown")
        val resultUI = if(setupNodeUI.isDefined){
            setupNodeUI.get.getAttribute("value")
        } else {
            ""
        }
        val unknownIcon = if(resultUI.isEmpty) {
            "/assets/images/question-mark-icon.png"
        } else {
            resultUI
        }

        val icon = vertexModel match {
            case i: LiteralVertex => literalIcon
            case i: IdentifiedVertex => identifiedIcon
            case _ => unknownIcon
        }
        val iconColor = vertexModel match {
            case i: LiteralVertex => literalIconColor.getOrElse(new Color(200, 0, 0, 1))
            case i: IdentifiedVertex => identifiedIconColor.getOrElse(new Color(0, 200, 0, 1))
            case _ => unknownIconColor.getOrElse(new Color(0, 0, 200, 1))
        }

        image = prepareImage(iconColor, icon)
    }
}