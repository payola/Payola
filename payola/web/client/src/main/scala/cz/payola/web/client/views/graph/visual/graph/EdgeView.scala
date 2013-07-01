package cz.payola.web.client.views.graph.visual.graph

import s2js.adapters.html
import cz.payola.common.rdf.Edge
import cz.payola.common.visual.Color
import cz.payola.web.client.views.algebra._
import cz.payola.web.client.views.graph.visual.graph.positioning.LocationDescriptor
import s2js.adapters.html._
import cz.payola.common.entities.settings.OntologyCustomization
import cz.payola.web.client.models.PrefixApplier

/**
 * Graphical representation of Edge object in the drawn graph.
 * @param edgeModel the object graphically represented by this class
 * @param originView the vertex object representing origin of this edge
 * @param destinationView of this graphical representation in drawing space
 */
class EdgeView(val edgeModel: Edge, val originView: VertexView, val destinationView: VertexView, prefixApplier: Option[PrefixApplier])
    extends View[html.elements.CanvasRenderingContext2D]
{
    var width = 1

    var color = new Color(150, 150, 150, 0.4)

    /**
     * Textual data that should be visualized with this edge ("over this edge").
     */
    val information: InformationView = new InformationView(List(edgeModel))

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

    def setWidth(newWidth: Option[Int]) {
        width = newWidth.getOrElse(1)
    }

    def setColor(newColor: Option[Color]) {
        color = newColor.getOrElse(new Color(150, 150, 150, 0.4))
    }

    def resetConfiguration() {
        setWidth(None)
        setColor(None)
    }

    def setConfiguration(newCustomization: Option[OntologyCustomization]) {
        if(newCustomization.isEmpty) {
            resetConfiguration()
        } else {
            val foundCustomizationType =
                if(newCustomization.get.isUserDefined) {
                    newCustomization.get.classCustomizations.find(_.uri == "properties")
                } else {
                    newCustomization.get.classCustomizations.find{_.uri == originView.rdfType}
                }

            if(foundCustomizationType.isEmpty) {
                resetConfiguration()
            } else {
                val foundCustomizationProperty = foundCustomizationType.get.propertyCustomizations.find{
                    _.uri == edgeModel.uri
                }
                if(foundCustomizationProperty.isEmpty) {
                    resetConfiguration()
                } else {
                    //width
                    if(foundCustomizationProperty.get.strokeWidth != 0) {
                        setWidth(Some(foundCustomizationProperty.get.strokeWidth))
                    } else {
                        setWidth(None)
                    }

                    //color
                    if(foundCustomizationProperty.get.strokeColor.length != 0) {
                        setColor(Color(foundCustomizationProperty.get.strokeColor))
                    } else {
                        setColor(None)
                    }
                }
            }
        }

        information.setConfiguration(newCustomization)
    }

    def draw(context: elements.CanvasRenderingContext2D, positionCorrection: Vector2D) {
        drawQuick(context, positionCorrection)
        if (isSelected) {
            information.draw(context, (LocationDescriptor.getEdgeInformationPosition(originView.position,
                destinationView.position) + positionCorrection).toVector)
        }
    }

    def drawQuick(context: elements.CanvasRenderingContext2D, positionCorrection: Vector2D) {
        val colorToUse = if(isSelected) {
            new Color(color.red, color.green, color.blue)
        } else {
            color
        }

        drawArrow(context, originView.position, destinationView.position,
            originView.radius  + 5, destinationView.radius + 5, width, colorToUse)
    }

    override def toString: String = {
        "[%s - %s - %s]".format(originView.toString(), edgeModel.toString(), destinationView.toString())
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
