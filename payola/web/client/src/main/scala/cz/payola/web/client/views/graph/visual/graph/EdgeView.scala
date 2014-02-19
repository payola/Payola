package cz.payola.web.client.views.graph.visual.graph

import s2js.adapters.html
import cz.payola.common.rdf.Edge
import cz.payola.common.visual.Color
import cz.payola.web.client.views.algebra._
import cz.payola.web.client.views.graph.visual.graph.positioning.LocationDescriptor
import s2js.adapters.html._
import cz.payola.common.entities.settings._
import cz.payola.web.client.models.PrefixApplier
import scala.Some

/**
 * Graphical representation of Edge object in the drawn graph.
 * @param edgeModel the object graphically represented by this class
 * @param originView the vertex object representing origin of this edge
 * @param destinationView of this graphical representation in drawing space
 */
class EdgeView(val edgeModel: Edge, private var _originView: VertexViewElement, private var _destinationView: VertexViewElement,
    prefixApplier: Option[PrefixApplier]) extends View[html.elements.CanvasRenderingContext2D]
{
    def originView = originBackup.getOrElse(_originView)
    def destinationView = destinationBackup.getOrElse(_destinationView)

    var width = 1

    var color = new Color(150, 150, 150, 0.4)

    private var originBackup: Option[VertexViewElement] = None
    private var destinationBackup: Option[VertexViewElement] = None

    /**
     * Textual data that should be visualized with this edge ("over this edge").
     */
    val information: InformationView = new InformationView(
        List(prefixApplier.map(_.applyPrefix(edgeModel.uri)).getOrElse(edgeModel.uri)))

    /**
     * Indicator of selection of this graphs element. Is used during color selection in draw function.
     * @return true if one of the edges vertices is selected.
     */
    def isSelected: Boolean = {
        _originView.isSelected || _destinationView.isSelected
    }

    def forceRedirectOrigin(redirection: VertexViewElement) {
        originBackup = None
        _originView = redirection
    }

    def redirectOrigin(redirection: Option[VertexViewElement]) {
        if (redirection.isDefined && originBackup.isDefined) {
            _originView = redirection.get
        } else if (redirection.isDefined && originBackup.isEmpty) {
            originBackup = Some(_originView)
            _originView = redirection.get
        } else if(originBackup.isDefined) {
            _originView = originBackup.get
            originBackup = None
        }
    }

    def forceRedirectDestination(redirection: VertexViewElement) {
        destinationBackup = None
        _destinationView = redirection
    }

    def redirectDestination(redirection: Option[VertexViewElement]) {
        if (redirection.isDefined && destinationBackup.isDefined) {
            _destinationView = redirection.get
        } else if (redirection.isDefined && destinationBackup.isEmpty) {
            destinationBackup = Some(_destinationView)
            _destinationView = redirection.get
        } else if(destinationBackup.isDefined) {
            _destinationView = destinationBackup.get
            destinationBackup = None
        }
    }

    /**
     * Indicator of selection of this graphs element. Is not used by inner mechanics.
     * @return true if both edges vertices are selected.
     */
    def areBothVerticesSelected: Boolean = {
        originView.isSelected && destinationView.isSelected
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

    def setConfiguration(newCustomization: Option[DefinedCustomization]) {
        if(newCustomization.isEmpty) {
            resetConfiguration()
        } else {
            val foundCustomizationType =
                newCustomization.get match {
                    case uc: UserCustomization =>
                        uc.classCustomizations.find(_.uri == "properties")
                    case oc: OntologyCustomization =>
                        oc.classCustomizations.find{ cust =>
                        originView match {
                            case view: VertexView => cust.uri == view.rdfType
                            case _ => false
                        }}
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
        if(!isHidden) {
            drawInner(context, positionCorrection)
        }
    }

    private def drawInner(context: elements.CanvasRenderingContext2D, positionCorrection: Vector2D) {

        if(!_originView.isEqual(_destinationView)) {
            drawQuick(context, positionCorrection)
            if (isSelected) {
                information.draw(context, (LocationDescriptor.getEdgeInformationPosition(_originView.position,
                    _destinationView.position) + positionCorrection).toVector)
            }
        }
    }

    def drawQuick(context: elements.CanvasRenderingContext2D, positionCorrection: Vector2D) {
        if(!isHidden) {
            drawQuickInner(context, positionCorrection)
        }
    }

    private def drawQuickInner(context: elements.CanvasRenderingContext2D, positionCorrection: Vector2D) {

        val colorToUse = if(isSelected) {
            new Color(color.red, color.green, color.blue)
        } else {
            color
        }

        val spacingDestination =
            if(_destinationView.isInstanceOf[VertexViewGroup]) { scala.math.sqrt(2) * _destinationView.radius + 5 }
            else { _destinationView.radius + 5 }
        val spacingOrigin =
            if(_originView.isInstanceOf[VertexViewGroup]) { scala.math.sqrt(2) * _originView.radius + 5 }
            else { _originView.radius + 5}

        drawArrow(context, _originView.position, _destinationView.position,
            spacingOrigin, spacingDestination, width, colorToUse)
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

    def isOrigin(vertex: VertexViewElement): Boolean = {
        _originView.isEqual(vertex) || (if(originBackup.isDefined) {
            originBackup.get.isEqual(vertex) } else { false })
    }

    def isDestination(vertex: VertexViewElement): Boolean = {
        _destinationView.isEqual(vertex) || (if(destinationBackup.isDefined) {
            destinationBackup.get.isEqual(vertex) } else { false })
    }
}
