package cz.payola.web.client.views.graph.visual.graph

import collection.mutable.ListBuffer
import s2js.adapters.html.elements.CanvasRenderingContext2D
import cz.payola.common.visual.Color
import cz.payola.web.client.views.algebra._
import cz.payola.web.client.views.graph.visual.graph.positioning.LocationDescriptor
import cz.payola.common.rdf._
import scala.collection.mutable
import cz.payola.web.client.views.elements._
import cz.payola.common.entities.settings.OntologyCustomization
import s2js.adapters.html

/**
 * Graphical representation of IdentifiedVertex object in the drawn graph.
 * @param vertexModel the vertex object from the model, that is visualized
 * @param position of this graphical representation in drawing space
 * @param rdfType type of the vertex used to identify drawing settings in an ontology
 */
class VertexView(val vertexModel: Vertex, var position: Point2D, var rdfType: String)
    extends View[CanvasRenderingContext2D] {

    var radius = 25

    var borderSize = 2

    var borderColor = Color.Black

    var color = new Color(51, 204, 255, 0.25)

    var glyph: String = ""

    private var glyphSpan: Option[Span] = None

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
     * Textual data that should be visualized with this vertex ("over this vertex").
     */
    private var information: Option[InformationView] = vertexModel match {
        case i: Vertex => Some(new InformationView(i))
        case _ => None
    }

    /**
     * Setter of contained informationView's data.
     * @param data what should the informationView of this vertex display
     */
    def setInformation(data: Option[Vertex]) {
        if (data.isDefined) {
            information = Some(new InformationView(data.get))
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
     */
    def addLiteralVertex(typeOfAttribute: Edge, valueOfAttribute: Seq[Vertex]) {
        val values = valueOfAttribute.map(_.toString)
        literalVertices.put(typeOfAttribute.toString, values)
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
     */
    def setCurrentAge(newAge: Int) {
        age = newAge
    }

    /**
     * Determines if the point is (geometrically) inside of this vertexView (rectangle represented byt this vertexView).
     * Should be used in vertexView selection process.
     * @param point to be decided if is inside or not
     * @return true if this.position - radius <= point <= this.position + radius
     */
    def isPointInside(point: Point2D): Boolean = {
        val radiusVector = Vector2D.One * radius
        isPointInRect(point, position + (-radiusVector),
            position + radiusVector)
    }

    def setRadius(newRadius: Option[Int]) {
        radius = newRadius.getOrElse(25)
    }

    def setBorderSize(newBorderSize: Option[Int]) {
        borderSize = newBorderSize.getOrElse(2)
    }

    def setBorderColor(newColor: Option[Color]) {
        borderColor = newColor.getOrElse(Color.Black)
    }

    def setColor(newColor: Option[Color]) {
        color = newColor.getOrElse(new Color(51, 204, 255, 0.25))
    }

    def render(parent: html.Element) {
        glyphSpan.foreach{ gS =>
            gS.render(parent)
            gS.hide()
        }
    }

    def destroy() {
        glyphSpan.foreach(_.destroy())
    }

    def setGlyph(newGlyph: Option[String]) {

        glyph = newGlyph.getOrElse("")

        if(glyph == "" && glyphSpan.isDefined) {
            glyphSpan.get.destroy()
            glyphSpan = None
        } else if(glyph != "") {
            if(glyphSpan.isDefined) {
                glyphSpan.get.destroy()
                glyphSpan = None
            }

            glyphSpan = Some(new Span(List(new Text(glyph)), "glyphed-element"))
        }
    }

    def resetConfiguration() {
        setRadius(None)
        setBorderSize(None)
        setBorderColor(None)
        setColor(None)
        setGlyph(None)
    }

    def setConfiguration(newCustomization: Option[OntologyCustomization]) {
        if(newCustomization.isEmpty) {
            resetConfiguration()
        } else {
            val foundCustomization = newCustomization.get.classCustomizations.find{_.uri == rdfType}

            if(foundCustomization.isEmpty) {
                resetConfiguration()
            } else {
                //radius
                if(foundCustomization.get.radius != 0) {
                    setRadius(Some(foundCustomization.get.radius))
                } else {
                    setRadius(None)
                }

                //color
                if(foundCustomization.get.fillColor.length != 0) {
                    setColor(Color(foundCustomization.get.fillColor))
                } else {
                    setColor(None)
                }

                //glyph
                if(foundCustomization.get.glyph.length != 0) {
                    setGlyph(Some(foundCustomization.get.glyph))
                } else {
                    setGlyph(None)
                }
            }
        }

        if(information.isDefined) {
            information.get.setConfiguration(newCustomization)
        }
    }

    def draw(context: CanvasRenderingContext2D, positionCorrection: Vector2D) {

        drawQuick(context, positionCorrection)

        if(glyphSpan.isDefined) {

            if(0 < position.y - radius && position.y + radius < context.canvas.clientHeight &&
                0 < position.x - radius && position.x + radius < context.canvas.clientWidth) {

                glyphSpan.get.show("inline")

                val halfSize = math.max(glyphSpan.get.htmlElement.getBoundingClientRect.height,
                    glyphSpan.get.htmlElement.getBoundingClientRect.width) / 2

                val left = position.x + positionCorrection.x - halfSize

                val top = position.y + positionCorrection.y - halfSize

                glyphSpan.get.setAttribute("style",
                    "left: "+left.toString+"px; top: "+top.toString+
                    "px; font-size: "+(radius+30)+"px;")
            } else {
                glyphSpan.get.hide()
            }
        }

        val informationPositionCorrection =
            if(glyphSpan.isDefined) { Vector2D(0, radius + borderSize) } else { Vector2D.Zero }
        val informationPosition =
            (LocationDescriptor.getVertexInformationPosition(position) + positionCorrection).toVector +
                informationPositionCorrection

        information.get.draw(context, informationPosition)
    }

    def drawQuick(context: CanvasRenderingContext2D, positionCorrection: Vector2D) {
        val correctedPosition = this.position + positionCorrection

        drawCircle(context, correctedPosition, radius, borderSize, borderColor)
        if (isSelected) {
            fillCurrentSpace(context, new Color(color.red, color.green, color.blue))
        } else {
            fillCurrentSpace(context, color)
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
