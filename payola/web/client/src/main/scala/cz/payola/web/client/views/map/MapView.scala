package cz.payola.web.client.views.map

import cz.payola.common.geo.Coordinates
import cz.payola.common.rdf._
import cz.payola.web.client.models.PrefixApplier
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.graph.PluginView
import s2js.compiler.javascript
import s2js.adapters.dom.Element
import cz.payola.web.client.View

/**
 * @author Jiri Helmich
 */
abstract class MapView(prefixApplier: Option[PrefixApplier] = None) extends PluginView("Map", prefixApplier) {

    def createLibWrapper(markers: Seq[Marker], element: Element) : View

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

            val map = createLibWrapper(markers, mapPlaceholder.htmlElement)

            new Marker(new Coordinates(0,0), "", "") //J4F

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
                    var coordsUriObject = entity["http://schema.org/geo"][0];
                    if (coordsUriObject["type"] && coordsUriObject["type"] == "uri"){
                        var coords = json[coordsUriObject["value"]];

                        if (coords){
                            var latObj = coords["http://schema.org/latitude"];
                            var longObj = coords["http://schema.org/longitude"];
                            if (latObj && longObj && latObj[0] && latObj[0]["value"] && longObj[0] && longObj[0]["value"]){
                                var coordinates = new cz.payola.common.geo.Coordinates(latObj[0]["value"],longObj[0]["value"]);

                                var titleObj = entity["http://schema.org/title"];
                                var descObj = entity["http://schema.org/description"];
                                var title = (titleObj && titleObj[0] && titleObj[0]["value"] ? titleObj[0]["value"] : "");
                                var desc = (descObj && descObj[0] && descObj[0]["value"] ? descObj[0]["value"] : "");

                                var marker = new cz.payola.web.client.views.map.Marker(coordinates, title, desc);
                                places.push(marker);
                            }
                        }
                    }
                }
           }

           var coll = new scala.collection.Seq();
           coll.internalJsArray = places;
           return coll;
        """)
    def fromJSON(json: String): Seq[Marker] = List()

    def createSubViews = {
        List(mapPlaceholder)
    }
}
