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

    def supportedDataFormat: String = "RDF/JSON"

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

    override def updateSerializedGraph(serializedGraph: Option[String]) {
        serializedGraph.map{ sg =>
            val markers = fromJSON(sg)

            val center = new Coordinates(0,0)
            val map = new MapView(center, 3, "satellite", markers, mapPlaceholder.htmlElement)

            mapPlaceholder.removeAllChildNodes()
            map.render(mapPlaceholder.htmlElement)
        }
    }

    @javascript(
        """
           var places = [];

           for (var uri in json){
                var entity = json[uri];
                if (entity["http://schema.org/geo"] && entity["http://schema.org/geo"].length > 0){
                    var coordsUriObject = entity["http://schema.org/geo"][0]
                    if (coordsUriObject["type"] && coordsUriObject["type"] == "uri"){
                        var coords = json[coordsUriObject["value"]];

                        if (coords){
                            var coordinates = new cz.payola.common.geo.Coordinates(coords["http://schema.org/latitude"][0]["value"],coords["http://schema.org/longitude"][0]["value"]);
                            var marker = new cz.payola.web.client.views.map.MapMarker(coordinates, entity["http://schema.org/title"][0]["value"], "");
                            places.push(marker);
                        }
                    }
                }
           }

           var coll = new scala.collection.Seq();
           coll.internalJsArray = places;
           return coll;
        """)
    def fromJSON(json: String): Seq[MapMarker] = List()

    def createSubViews = {
        List(mapPlaceholder)
    }
}
