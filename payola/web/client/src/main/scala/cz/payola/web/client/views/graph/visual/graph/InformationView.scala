package cz.payola.web.client.views.graph.visual.graph

import s2js.adapters.html
import cz.payola.common.visual.Color
import cz.payola.web.client.views.algebra._
import s2js.adapters.html._
import cz.payola.common.entities.settings._
import cz.payola.web.client.models.PrefixApplier
import cz.payola.common.rdf._

/**
 * Graphical representation of textual data in the drawn graph.
 * @param labels that are visualized (by toString function of this object)
 */
class InformationView(labels: List[Any]) extends View[html.elements.CanvasRenderingContext2D] {

    var colorBackground = new Color(255, 255, 255, 0.2)

    var color = new Color(50, 50, 50)

    var font = "12px Sans"

    var align  = "center"

    def isSelected: Boolean = {
        false //information can not be selected
    }

    def setBackgroundColor(newColor: Option[Color]) {
        colorBackground = newColor.getOrElse(new Color(255, 255, 255, 0.2))
    }

    def setColor(newColor: Option[Color]) {
        color = newColor.getOrElse(new Color(50, 50, 50))
    }

    def setFont(newFont: Option[String]) {
        font = newFont.getOrElse("12px Sans")
    }

    def setAlign(newAlign: Option[String]) {
        align = newAlign.getOrElse("center")
    }

    def resetConfiguration() {
        setBackgroundColor(None)
        setColor(None)
        setFont(None)
        setAlign(None)
    }

    def setConfiguration(newCustomization: Option[OntologyCustomization]) {
        //ontologies do not contain text configuration
    }

    def draw(context: elements.CanvasRenderingContext2D, positionCorrection: Vector2D) {
        drawQuick(context, positionCorrection)
    }

    def drawQuick(context: elements.CanvasRenderingContext2D, positionCorrection: Vector2D) {
        performDrawing(context, color, Point2D(positionCorrection.x, positionCorrection.y))
    }

    /**
     * Performs the InformationView specific drawing routine. Draws the textual data to the specified location.
     * @param context to which text is drawn
     * @param color in which the text is draw
     * @param position where the text is drawn
     */
    private def performDrawing(context: elements.CanvasRenderingContext2D, color: Color, position: Point2D) {
        val textWidth = context.measureText(labels.head.toString).width
        drawRoundedRectangle(context, position + Vector2D(-textWidth / 2, -15), Vector2D(textWidth, 20), 4)
        fillCurrentSpace(context, colorBackground)
        //TODO how come, that the measureText returns different size on the first run??

        drawText(context, labels.head.toString, position, color, font, align)
    }
}

object InformationView {
    def constructByMultiple(labels: List[LabelItem], modelObject: Any, literals: List[(String, Seq[String])],
        prefixApplier: Option[PrefixApplier]): Option[InformationView] = {

        val uriOpt = getUri(modelObject)
        val uri = if(uriOpt.isDefined && prefixApplier.isDefined) prefixApplier.get.applyPrefix(uriOpt.get) else ""
        val processedLabels = processLabels(labels, uri, literals)
        if(labels.isEmpty) { None } else { Some(new InformationView(processedLabels)) }
    }

    def constructBySingle(modelObject: Any, prefixApplier: Option[PrefixApplier]): InformationView = {
        val uriOpt = getUri(modelObject)
        if(uriOpt.isDefined && prefixApplier.isDefined) {
            new InformationView(List(prefixApplier.get.applyPrefix(uriOpt.get)))
        } else {
            new InformationView(List(modelObject.toString()))
        }
    }

    private def getUri(modelObject: Any): Option[String] = {
        modelObject match{
            case i: IdentifiedVertex =>
                Some(i.uri)
            case i: LiteralVertex =>
                None
            case i: Edge =>
                Some(i.uri)
            case _ =>
                None
        }
    }

    private def processLabels(labels: List[LabelItem], modelObjectUri: String, literals: List[(String, Seq[String])]):
        List[String] = {

        val acceptedLabels = labels.filter{ label =>
            label.accepted && (label.userDefined || label.value == "uri" || literals.exists{
                _.toString().contains(label.value.substring(2))
            })
        }

        acceptedLabels.map { label =>
            if(label.userDefined) {
                label.value
            } else if(label.value == "uri") {
                modelObjectUri
            } else {
                literals.find{ literal => labels.contains(literal._1.substring(2)) }.get.toString
            }
        }
    }
}
