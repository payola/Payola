package cz.payola.web.client.views.graph.visual.graph

import collection.mutable.ListBuffer
import s2js.adapters.html.elements.CanvasRenderingContext2D
import cz.payola.common.visual.Color
import cz.payola.web.client.views.algebra._
import cz.payola.web.client.views.graph.visual.graph.positioning.LocationDescriptor
import cz.payola.common.rdf._
import cz.payola.web.client.models.PrefixApplier
import cz.payola.common.entities.settings.DefinedCustomization

/**
 * Graphical representation of IdentifiedVertex object in the drawn graph.
 * @param vertexModel the vertex object from the model, that is visualized
 * @param position of this graphical representation in drawing space
 * @param rdfType type of the vertex used to identify drawing settings in a customization
 * @param prefixApplier labels transformer
 */
class VertexView(_vertexModel: Vertex, position: Point2D, private var _rdfType: String, prefixApplier: Option[PrefixApplier])
    extends VertexViewElement(position, prefixApplier) {

    def rdfType = _rdfType

    def rdfType_=(newType: String) {
        _rdfType = newType
    }

    def vertexModel = _vertexModel
    /**
     * Neighbouring literal vertices of this vertex describing attributes of this vertex.
     */
    private var literalVertices = List[(String, Seq[String])]()

    /**
     * Count of updates of the parent graphView, that this vertexView was held in memory and was not sent byt the server.
     */
    private var age = 0

    /**
     * List of edges that this vertex representation has. Allows to Iterate through the graphical representation
     * of the graph.
     */
    private var _edges = ListBuffer[EdgeView]()

    def edges = _edges

    def edges_=(newEdges: ListBuffer[EdgeView]) {
        _edges = newEdges
    }

    /**
     * Textual data that should be visualized with this vertex ("over this vertex").
     */
    information = Some(InformationView.constructBySingle(vertexModel, prefixApplier))

    /**
     * Setter of contained informationView's data.
     * @param data what should the informationView of this vertex display
     */
    def setInformation(data: Option[Vertex]) {
        if (data.isDefined) {
            information = Some(InformationView.constructBySingle(data.get, prefixApplier))
        }
    }

    /**
     * Getter of the text values of neighbouring literalVertices of this identifiedVertex.
     * @return attributes of this identifiedVertex with types of relations (Edge between this identifiedVertex
     *         and the literalVertex)
     */
    def getLiteralVertices(): List[(String, Seq[String])] = {
        literalVertices
    }

    /**
     * Appends a literalVertex (attribute of this identifiedVertex) identifying types of relations (Edges)
     */
    def addLiteralVertex(typeOfAttribute: Edge, valueOfAttribute: Seq[Vertex], identNeighborVertex: IdentifiedVertex) {
        val values = valueOfAttribute.map(_.toString)
        literalVertices ++= List(((typeOfAttribute.toString, values)))
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

    def isPointInside(point: Point2D): Boolean = {
        if(isHidden) {
            false
        } else {
            val radiusVector = Vector2D.One * radius
            isPointInRect(point, position + (-radiusVector), position + radiusVector)
        }
    }

    def draw(context: CanvasRenderingContext2D, positionCorrection: Vector2D) {
        if(!isHidden) {
            drawInner(context, positionCorrection)
        }
    }

    private def drawInner(context: CanvasRenderingContext2D, positionCorrection: Vector2D) {

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

        if (information.isDefined) {
            information.get.draw(context, informationPosition)
        }
    }

    def drawQuick(context: CanvasRenderingContext2D, positionCorrection: Vector2D) {
        if(!isHidden) {
            drawQuickInner(context, positionCorrection)
        }
    }

    private def drawQuickInner(context: CanvasRenderingContext2D, positionCorrection: Vector2D) {

        val correctedPosition = this.position + positionCorrection

        drawCircle(context, correctedPosition, radius, borderSize, borderColor)
        if (isSelected) {
            fillCurrentSpace(context, new Color(color.red, color.green, color.blue))
        } else {
            fillCurrentSpace(context, color)
        }
    }

    def setConfiguration(newCustomization: Option[DefinedCustomization]) {
        setVisualConfiguration(newCustomization, vertexModel.toString(), rdfType, getLiteralVertices)
    }

    /**
     * Compares this to another vertexView. Returns true if vertexModels.toString are equal.
     * @param vertexView
     * @return
     */
    override def isEqual(vertexView: Any): Boolean = {
        if (vertexView == null) {
            false
        }
        vertexView match {
            case vv: VertexView =>
                this.represents(vv.vertexModel)
            case _ => false
        }
    }

    def contains(vertex: VertexViewElement): Boolean = {
        vertex match {
            case view: VertexView =>
                represents(view.vertexModel)
        }
    }

    def represents(vertex: Vertex): Boolean = {
        vertexModel.toString eq vertex.toString
    }

    def getFirstContainedVertex(): Vertex = vertexModel
}
