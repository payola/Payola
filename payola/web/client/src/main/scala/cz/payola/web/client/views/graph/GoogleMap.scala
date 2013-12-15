package cz.payola.web.client.views.graph

import cz.payola.common.geo.Coordinates
import cz.payola.common.rdf._
import cz.payola.web.client.models.PrefixApplier
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.map._
import s2js.compiler.javascript

/**
 * @author Jiri Helmich
 */
class GoogleMap(prefixApplier: Option[PrefixApplier] = None) extends PluginView("Google map", prefixApplier) {

    val mapPlaceholder = new Div(List(),"map-placeholder")

    @javascript("""console.log(str)""")
    def log(str: Any) {}

    @javascript(""" return parseInt(str); """)
    def intval(str: String) : Int = 0


    val coordsUri = "http://schema.org/geo"
    val latUri = "http://schema.org/latitude"
    val lngUri = "http://schema.org/longitude"
    val descUri = "http://schema.org/description"
    val nameUri = "http://schema.org/title"

    def toDouble(obj: Any) : Double = {
        obj match {
            case x: LiteralVertex => x.value.toString.toDouble
            case _ => 0.0
        }
    }

    def toString(obj: Any) : String = {
        obj match {
            case x: LiteralVertex => x.value.toString
            case _ => ""
        }
    }

    override def updateGraph(graph: Option[Graph], contractLiterals: Boolean = true) {

        graph.map { g =>
            val hasGeo = g.edges.filter(_.uri == coordsUri)
            val markers = hasGeo.map{ e =>

                val markerData = g.getOutgoingEdges(e.destination)
                val placeData = g.getOutgoingEdges(e.origin)

                val lat = markerData.find(_.uri == latUri).map{ v => toDouble(v.destination)}.getOrElse(0.0)
                val lng = markerData.find(_.uri == lngUri).map{ v => toDouble(v.destination)}.getOrElse(0.0)
                val title = placeData.find(_.uri == nameUri).map{ v => toString(v.destination)}.getOrElse("")
                val desc = placeData.find(_.uri == descUri).map{ v => toString(v.destination)}.getOrElse("")

                new MapMarker(new Coordinates(lat, lng), title, desc)
            }

            val center = new Coordinates(0,0)
            val map = new MapView(center, 3, "satellite", markers, mapPlaceholder.htmlElement)

            mapPlaceholder.removeAllChildNodes()
            map.render(mapPlaceholder.htmlElement)
        }
    }

    def createSubViews = {
        List(mapPlaceholder)
    }
}
