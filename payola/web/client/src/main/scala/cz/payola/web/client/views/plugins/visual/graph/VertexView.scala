package cz.payola.web.client.views.plugins.visual.graph

import collection.mutable.ListBuffer
import cz.payola.common.rdf.{LiteralVertex, IdentifiedVertex, Vertex}
import s2js.adapters.js.dom.CanvasRenderingContext2D
import cz.payola.web.client.views.plugins.visual.{SetupLoader, Vector, Color, Point}

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
    private var rectangleCornerRadius: Double = 5

    /**
      * Default dimensions of a vertex.
      */
    private var rectangleSize = Vector(30, 24)

    /**
      * Default color of a vertex.
      */
    private var rectangleColor = new Color(180, 240, 180, 0.8)
    
    private var image = prepareImage(
        vertexModel match {
            case i: LiteralVertex => new Color(200, 150, 0, 1)
            case i: IdentifiedVertex => new Color(0, 200, 150, 1)
            case _ => new Color(150, 0, 200, 1)
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
        isPointInRect(point, position + (rectangleSize / -2), position + (rectangleSize / 2))
    }

    def draw(context: CanvasRenderingContext2D, color: Option[Color], positionCorrection: Option[Point]) {

        val colorToUseOnBox = color.getOrElse(rectangleColor)
        val correctedPosition = this.position + (rectangleSize / -2) + positionCorrection.getOrElse(Point(0,0)).toVector

        drawRoundedRectangle(context, correctedPosition, rectangleSize, rectangleCornerRadius)
        fillCurrentSpace(context, colorToUseOnBox)

        drawImage(context, image, position + Vector(-10, -10), Vector(20, 20))

        /*TODO drawing is successful only on the second redraw...why!?*/
    }
    
    def drawInformation(context: CanvasRenderingContext2D, color: Option[Color], positionCorrection: Option[Point]) {
        if(information.isDefined) {
            vertexModel match {
                case i: IdentifiedVertex => information.get.draw(context, color, positionCorrection)
                case _ => if(selected) { information.get.draw(context, color, positionCorrection) }
            }
        }
    }
    
    private def updateSize(loader: SetupLoader) {

        val newValueWidth = loader.getValue(loader.VertexDimensionWidth)
        var newWidth = if(newValueWidth.isDefined) { newValueWidth.get.toInt } else { rectangleSize.x }
        newWidth = if(10 <= newWidth && newWidth <= 100) { //TODO constants
            newWidth
        } else {
            rectangleSize.x
        }

        val newValueHeight = loader.getValue(loader.VertexDimensionHeight)
        var newHeight = if(newValueHeight.isDefined) { newValueHeight.get.toInt } else { rectangleSize.y }
        newHeight = if(10 <= newHeight && newHeight <= 100) { //TODO constants
            newHeight
        } else {
            rectangleSize.y
        }

        rectangleSize = Vector(newWidth, newHeight)
    }
    
    private def updateCornerRadius(loader: SetupLoader) {

        val newValue = loader.getValue(loader.VertexDimensionCornerRadius)
        val newCornerRadius = if(newValue.isDefined) { newValue.get.toInt } else { rectangleCornerRadius }

        rectangleCornerRadius = if(0 <= newCornerRadius && newCornerRadius <=
            scala.math.min(rectangleSize.x, rectangleSize.y)/2) {
            newCornerRadius
        } else {
            scala.math.min(rectangleSize.x, rectangleSize.y)/2
        }
    }
    
    private def updateRectangleColor(loader: SetupLoader) {
        rectangleColor = loader.createColor(loader.VertexColorMedium).getOrElse(rectangleColor)
    }

    private def getLiteralIconColor(loader: SetupLoader): Color = {
        loader.createColor(loader.VertexColorLiteral).getOrElse(new Color(200, 0, 0, 1))
    }
    
    private def getIdentifiedIconColor(loader: SetupLoader): Color = {
        loader.createColor(loader.VertexColorIdentified).getOrElse(new Color(0, 200, 0, 1))
    }

    private def getUnknownIconColor(loader: SetupLoader): Color = {
        loader.createColor(loader.VertexColorUnknown).getOrElse(new Color(0, 0, 200, 1))
    }

    private def getLiteralIcon(loader: SetupLoader): String = {
        loader.getValue(loader.VertexIconLiteral).getOrElse("/assets/images/book-icon.png")
    }
    
    private def getIdentifiedIcon(loader: SetupLoader): String = {
        loader.getValue(loader.VertexIconIdentified).getOrElse("/assets/images/view-eye-icon.png")
    }

    private def getUnknownIcon(loader: SetupLoader): String = {
        loader.getValue(loader.VertexIconUnknown).getOrElse("/assets/images/question-mark-icon.png")
    }
    
    
    private def updateImage(loader: SetupLoader) {

        val icon = vertexModel match {
            case i: LiteralVertex => getLiteralIcon(loader)
            case i: IdentifiedVertex => getIdentifiedIcon(loader)
            case _ => getUnknownIcon(loader)
        }
        val iconColor = vertexModel match {
            case i: LiteralVertex => getLiteralIconColor(loader)
            case i: IdentifiedVertex => getIdentifiedIconColor(loader)
            case _ => getUnknownIconColor(loader)
                
        }

        image = prepareImage(iconColor, icon)
    }
    
    def updateSettings(loader: SetupLoader) {

        updateSize(loader)//WARNING updateCornerRadius has to be called AFTER (!!!!) updateSize to correct its value

        updateCornerRadius(loader)

        updateRectangleColor(loader)

        updateImage(loader)

        if(information.isDefined) {
            information.get.updateSettings(loader)
        }
    }
}