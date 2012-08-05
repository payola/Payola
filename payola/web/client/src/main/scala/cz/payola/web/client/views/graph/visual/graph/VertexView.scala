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
  * Graphical representation of IdentifiedVertex object in the drawn graph.
  * @param vertexModel the vertex object from the model, that is visualised
  * @param position of this graphical representation in drawing space
  * @param settings draw settings used in draw and quickDraw routines
  * @param settingsText draw settings used by the contained informationView
  * @param rdfType type of the vertex used to identify drawing settings in an ontology
  */
class VertexView(val vertexModel: IdentifiedVertex, var position: Point2D, var settings: VertexSettingsModel,
    settingsText: TextSettingsModel, var rdfType: String) extends View[CanvasRenderingContext2D]
{
    /**
     * Neighbouring literal vertices of this vertex describing attributes of this vertex.
     */
    private val literalVertices = new mutable.HashMap[String, Seq[String]]()

    /**
     * Count of updates of the parent graphView, that this vertexView was held in memory and was not sent byt the server.
     */
    private var age = 0

    /**
      * Indicator of isSelected attribute.
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
    private var information: Option[InformationView] = vertexModel match {
        case i: Vertex => Some(new InformationView(i, settingsText))
        case _ => None
    }

    /**
     * Setter of contained informationView's data.
     * @param data what should the informationView of this vertex display
     */
    def setInformation(data: Option[Vertex]) {
        if(data.isDefined) {
            information = Some(new InformationView(data.get, settingsText))
        }
    }

    /**
     * Getter of the text values of neighbouring literalVertices of this identifiedVertex.
     * @return attributes of this identifiedVertex with types of relations (Edge between this identifiedVertex
     *         and the literalVertex)
     */
    def getLiteralVertices: mutable.HashMap[String, Seq[String]] = {
        literalVertices
    }

    /**
     * Appends a literalVertex (attribute of this identifiedVertex) identifying types of relations (Edges)
     * @param vertex
     * @param vertexEdges
     */
    def addLiteralVertex(vertex: LiteralVertex, vertexEdges: Seq[Edge]) {
        val vertexEdgesContents = vertexEdges.map(_.uri)
        literalVertices.put(vertex.toString, vertexEdgesContents)
    }

    /**
     * IsSelected attribute getter
     * @return true if marked as selected.
     */
    def isSelected: Boolean = {
        selected
    }

    /**
     * Count of parent graph update cycles that this vertexView survived without being sent from server.
     * @return count of updates from the last data refresh from the server
     */
    def getCurrentAge: Int = {
        age
    }

    /**
     * Sets count of parent graph update cycles to 0.
     */
    def resetCurrentAge() {
        age = 0
    }

    /**
     * Increases count of parent graph update graph cycles by 1.
     */
    def increaseCurrentAge() {
        age += 1
    }

    /**
     * Sets count of parent graph update cycles.
     * @param newAge
     */
    def setCurrentAge(newAge: Int) {
        age = newAge
    }

    /**
     * Determines if the point is (geometrically) inside of this vertexView (rectangle represented byt this vertexView).
     * Should be used in vertexView selection process.
     * @param point to be decided if is inside or not
     * @return true if this.position - this.settings.size <= point <= this.position + this.settings.size
     */
    def isPointInside(point: Point2D): Boolean = {
        val radiusVector = Vector2D.One * settings.radius(rdfType)
        isPointInRect(point, position + (-radiusVector),
            position + radiusVector)
    }

    def draw(context: CanvasRenderingContext2D, positionCorrection: Vector2D) {
        drawQuick(context, positionCorrection)

        val glyph = settings.glyph(rdfType)

        drawText(context, glyph, this.position + positionCorrection + Vector2D(0, settings.glyphSize / 4),
            Color.Black, settings.glyphWholeFont, settings.glyphAlign)

        val halfRadius = settings.radius(rdfType) / 2

        val informationPositionCorrection =
            if(glyph != "") { Vector2D(0, halfRadius + (settings.glyphSize / 4)) } else { Vector2D.Zero }
        val informationPosition =
            (LocationDescriptor.getVertexInformationPosition(position) + positionCorrection).toVector +
                informationPositionCorrection

        information.get.draw(context, informationPosition)
    }

    def drawQuick(context: CanvasRenderingContext2D, positionCorrection: Vector2D) {
        val correctedPosition = this.position + positionCorrection

        drawCircle(context, correctedPosition, settings.radius(rdfType), settings.borderSize, settings.borderColor)
        if (isSelected) {
            val col = settings.color(rdfType)
            fillCurrentSpace(context, Color(col.red, col.green, col.blue, 1.0))
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
