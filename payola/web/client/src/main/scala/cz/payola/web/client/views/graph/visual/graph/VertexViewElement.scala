package cz.payola.web.client.views.graph.visual.graph

import s2js.adapters.html.elements.CanvasRenderingContext2D
import cz.payola.common.visual.Color
import cz.payola.web.client.views.algebra._
import s2js.adapters.html
import cz.payola.web.client.views.elements._
import cz.payola.common.rdf._
import collection.mutable.ListBuffer
import scala.Some
import scala.Some
import cz.payola.common.entities.settings.OntologyCustomization
import cz.payola.web.client.models.PrefixApplier

abstract class VertexViewElement(var position: Point2D, prefixApplier: Option[PrefixApplier])
    extends View[CanvasRenderingContext2D] {

    def edges: ListBuffer[EdgeView]
    def edges_=(newEdges: ListBuffer[EdgeView])

    var radius = 25

    protected var information: Option[InformationView] = None

    protected var borderSize = 2

    protected var borderColor = Color.Black

    protected var color = new Color(51, 204, 255, 0.25)

    protected var glyph: String = ""

    protected var glyphSpan: Option[Span] = None

    /**
     * Indicator of isSelected attribute.
     */
    protected var selected = false

    def getCurrentAge: Int

    def setCurrentAge(newAge: Int)

    def resetCurrentAge()

    def increaseCurrentAge()

    def addLiteralVertex(typeOfAttribute: Edge, valueOfAttribute: Seq[Vertex], identNeighborVertex: IdentifiedVertex)

    def getFirstContainedVertex(): Vertex
    /**
     * IsSelected attribute getter
     * @return true if marked as selected.
     */
    def isSelected: Boolean = {
        selected
    }

    def setSelected(selection: Boolean) {
        selected = selection
    }

    /**
     * Determines if the point is (geometrically) inside of this vertexView (rectangle represented byt this vertexView).
     * Should be used in vertexView selection process.
     * @param point to be decided if is inside or not
     * @return true if this.position - radius <= point <= this.position + radius
     */
    def isPointInside(point: Point2D): Boolean

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

    def render(parent: html.Element) {
        glyphSpan.foreach{ gS =>
            gS.render(parent)
            gS.hide()
        }
    }

    def destroy() {
        glyphSpan.foreach(_.destroy())
    }

    def resetConfiguration() {
        setRadius(None)
        setBorderSize(None)
        setBorderColor(None)
        setColor(None)
        setGlyph(None)
    }

    def setVisualConfiguration(newCustomization: Option[OntologyCustomization], uniId: String, rdfType: String,
        getLiterals: () => List[(String, Seq[String])]) {

        if(newCustomization.isEmpty) {
            resetConfiguration()
            information = Some(InformationView.constructBySingle(uniId, prefixApplier))
        } else {
            val foundCustomization =
                if(newCustomization.get.isUserDefined){
                    val found = newCustomization.get.classCustomizations.find(_.hasId(uniId))

                    if(found.isDefined && found.get.labels != null && found.get.labels != "") {
                        information = InformationView.constructByMultiple(
                            found.get.labelsSplitted, uniId, getLiterals(), prefixApplier)
                    } else {
                        information = None
                    }
                    found
                } else {
                    information = Some(InformationView.constructBySingle(uniId, prefixApplier))
                    newCustomization.get.classCustomizations.find{_.uri == rdfType}
                }

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
    }

    override def toString: String = {
        this.position.toString
        //"["+vertexModel.toString+"]"
    }

    def isEqual(vertexElement: Any): Boolean

    def represents(vertex: Vertex): Boolean

    def contains(vertex: VertexViewElement): Boolean
}
