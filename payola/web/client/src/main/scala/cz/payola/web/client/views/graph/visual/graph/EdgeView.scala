package cz.payola.web.client.views.graph.visual.graph

import cz.payola.common.rdf.Edge
import s2js.adapters.html
import cz.payola.web.client.views.graph.visual.Color
import cz.payola.web.client.views.algebra._
import cz.payola.web.client.views.graph.visual.settings.components.visualsetup.VisualSetup
import cz.payola.web.client.views.graph.visual.graph.positioning.LocationDescriptor
import s2js.adapters.html._

/**
 * Graphical representation of Edge object in the drawn graph.
 * @param edgeModel the object graphically represented by this class
 * @param originView the vertex object representing origin of this edge
 * @param destinationView of this graphical representation in drawing space
 * @param settings draw settings used in draw and quickDraw routines
 */
class EdgeView(val edgeModel: Edge, val originView: VertexView, val destinationView: VertexView,
    val settings: VisualSetup) extends View[html.elements.CanvasRenderingContext2D]
{
    /**
     * Textual data that should be visualised with this edge ("over this edge").
     */
    val information: InformationView = new InformationView(edgeModel, settings.textModel)

    /**
     * Indicator of selection of this graphs element. Is used during color selection in draw function.
     * @return true if one of the edges vertices is selected.
     */
    def isSelected: Boolean = {
        originView.selected || destinationView.selected
    }

    /**
     * Indicator of selection of this graphs element. Is not used by inner mechanics.
     * @return true if both edges vertices are selected.
     */
    def areBothVerticesSelected: Boolean = {
        originView.selected && destinationView.selected
    }

    def draw(context: elements.CanvasRenderingContext2D, positionCorrection: Vector2D) {
        drawQuick(context, positionCorrection)
        if (isSelected) {
            information.draw(context, (LocationDescriptor.getEdgeInformationPosition(originView.position,
                destinationView.position) + positionCorrection).toVector)
        }
    }

    def drawQuick(context: elements.CanvasRenderingContext2D, positionCorrection: Vector2D) {
        val colorToUse = if (isSelected) {
            val col = settings.edgesModel.color(originView.rdfType, edgeModel.uri)

            Color(col.red, col.green, col.blue, 1.0)
        } else {
            settings.edgesModel.color(originView.rdfType, edgeModel.uri)
        }

        drawArrow(context, originView.position, destinationView.position,
            settings.vertexModel.radius(originView.rdfType) * 3 / 2,
            settings.vertexModel.radius(destinationView.rdfType) * 3 / 2,
            settings.edgesModel.width(originView.rdfType, edgeModel.uri), colorToUse)
    }

    override def toString: String = {
        "[" + originView.toString + "-" + edgeModel.toString + "-" + destinationView.toString + "]"
    }

    /**
     * Compares another edgeView to this one.
     * @param edgeView to compare this with
     * @return true if edgeModels.toString are equal and destination vertices
     *         and origin vertices are equal
     */
    def isEqual(edgeView: Any): Boolean = {
        if (edgeView == null) {
            false
        }

        edgeView match {
            case ev: EdgeView =>
                ((originView isEqual ev.originView) && (destinationView isEqual ev.destinationView)
                    && (edgeModel.toString eq ev.edgeModel.toString))
            case _ => false
        }
    }
}
