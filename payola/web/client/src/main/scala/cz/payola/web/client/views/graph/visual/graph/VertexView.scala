package cz.payola.web.client.views.graph.visual.graph

import collection.mutable.ListBuffer
import s2js.adapters.js.dom.CanvasRenderingContext2D
import cz.payola.web.client.views.graph.visual.settings._
import cz.payola.web.client.views.graph.visual.Color
import cz.payola.web.client.views.algebra._
import cz.payola.web.client.views.graph.visual.graph.positioning.LocationDescriptor
import cz.payola.common.rdf._
import scala.collection.mutable

/**
  * Graphical representation of Vertex object in the drawn graph.
  * @param vertexModel the vertex object from the model, that is visualised
  * @param position of this graphical representation in drawing space
  */
class VertexView(val vertexModel: IdentifiedVertex, var position: Point2D, var settings: VertexSettingsModel,
    settingsText: TextSettingsModel, var rdfType: String) extends View[CanvasRenderingContext2D]
{
    private var literalVertices = new mutable.HashMap[String, Seq[String]]()

    private var age = 0

    /*private val image = prepareImage(//TODO This has to be called after color or path change event was fired
        vertexModel match {
            case i: LiteralVertex => new Color(180, 50, 50, 1)
            case i: IdentifiedVertex => new Color(50, 180, 50, 1)
            case _ => new Color(0, 0, 0, 1)
        }, vertexModel match {
            case i: LiteralVertex => "/assets/images/book-icon.png"
            case i: IdentifiedVertex => "/assets/images/view-eye-icon.png"
            case _ => "/assets/images/question-mark-icon.png"
        })*/

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

    def getLiteralVertices: mutable.HashMap[String, Seq[String]] = {
        literalVertices
    }

    def addLiteralVertex(vertex: LiteralVertex, vertexEdges: Seq[Edge]) {
        val vertexEdgesContents = vertexEdges.map(_.uri)
        literalVertices.put(vertex.toString, vertexEdgesContents)
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

    def isPointInside(point: Point2D): Boolean = {
        val radiusVector = Vector2D.One * settings.radius(rdfType)
        isPointInRect(point, position + (-radiusVector),
            position + radiusVector)
    }

    def draw(context: CanvasRenderingContext2D, positionCorrection: Vector2D) {
        drawQuick(context, positionCorrection)

        if (information.isDefined) {
            information.get.draw(context,
                (LocationDescriptor.getVertexInformationPosition(position) + positionCorrection).toVector)
        }
    }

    def drawQuick(context: CanvasRenderingContext2D, positionCorrection: Vector2D) {
        val correctedPosition = this.position + positionCorrection

        drawCircle(context, correctedPosition, settings.radius(rdfType), 2, Color.Black)
        if (isSelected) {
            fillCurrentSpace(context, settings.color(rdfType)) //TODO differ selected color
        } else {
            fillCurrentSpace(context, settings.color(rdfType))
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
        if (vertexView == null) {
            false
        }
        vertexView match {
            case vv: VertexView =>
                vv.vertexModel.toString eq vertexModel.toString
            case _ => false
        }
    }
}
