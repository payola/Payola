package cz.payola.web.client.views.plugins.visual.graph

import collection.mutable.ListBuffer
import cz.payola.common.rdf.{LiteralVertex, IdentifiedVertex, Vertex}
import s2js.adapters.js.dom.CanvasRenderingContext2D
import cz.payola.web.client.views.plugins.visual._
import settings.{TextSettingsModel, VertexSettingsModel}
import s2js.adapters.js.browser.window

/**
 * Graphical representation of Vertex object in the drawn graph.
 * @param vertexModel the vertex object from the model, that is visualised
 * @param position of this graphical representation in drawing space
 */
class VertexView(val vertexModel: Vertex, var position: Point, var settings: VertexSettingsModel,
    settingsText: TextSettingsModel) extends View
{

    private var age = 0

    private val image = prepareImage( //TODO This has to be called after color or path change event was fired
        vertexModel match {
            case i: LiteralVertex => new Color(180, 50, 50, 1)
            case i: IdentifiedVertex => new Color(50, 180, 50, 1)
            case _ => new Color(0, 0, 0, 1)
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
        case i: LiteralVertex => Some(new InformationView(i, settingsText))
        case i: IdentifiedVertex => Some(new InformationView(i, settingsText))
        case _ => None
    }

    def isSelected: Boolean = {
        selected
    }

    def getCurrentAge: Int = {
        age
    }

    def resetCurrentAge() {
        age = 0
    }

    def increaseCurrentAge() {
        age += 1
    }

    def setCurrentAge(newAge: Int) {
        age = newAge
    }

    def isPointInside(point: Point): Boolean = {
        isPointInRect(point, position + (new Vector(settings.radius, settings.radius) / -2), position + (new Vector(settings.radius, settings.radius) / 2))
    }

    def draw(context: CanvasRenderingContext2D, color: Option[Color], positionCorrection: Vector) {
        drawQuick(context, color, positionCorrection)
        drawImage(context, image, position + Vector(-10, -10), Vector(20, 20))
    }

    def drawQuick(context: CanvasRenderingContext2D, color: Option[Color], positionCorrection: Vector) {
        val colorToUseOnBox = color.getOrElse(settings.color)
        val correctedPosition = this.position + positionCorrection

        drawCircle(context, correctedPosition, settings.radius/2, 2, Color.Black)
        //val correctedPosition = this.position + (settings.getSize / -2) + positionCorrection
        //drawRoundedRectangle(context, correctedPosition, settings.getSize, settings.cornerRadius)
        fillCurrentSpace(context, colorToUseOnBox)
    }

    def drawInformation(context: CanvasRenderingContext2D, color: Option[Color], positionCorrection: Vector) {
        if (information.isDefined) {
            vertexModel match {
                case i: IdentifiedVertex => information.get.draw(context, color, positionCorrection)
                case _ => if (selected) { information.get.draw(context, color, positionCorrection) }
            }
        }
    }

    override def toString: String = {
        this.position.toString
        //"["+vertexModel.toString+"]"
    }

    /**
     * Compares this to another vertexView. Returns true if vertexModels.toString are equal.
     * @param vertexView
     * @return
     */
    def isEqual(vertexView: Any): Boolean = {
        if(vertexView == null) {
            false
        }
        vertexView match {
            case vv: VertexView =>
                vv.vertexModel.toString eq vertexModel.toString
            case _ => false
        }
    }
}