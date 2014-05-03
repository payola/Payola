package cz.payola.web.client.views.map

import cz.payola.common.geo.Coordinates
import cz.payola.common.rdf._
import cz.payola.web.client.models.PrefixApplier
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.graph.PluginView
import s2js.compiler.javascript
import s2js.adapters.dom.Element
import cz.payola.web.client.View
import cz.payola.web.client.views.map.facets._
import scala.collection.mutable.ArrayBuffer
import cz.payola.common.geo.Coordinates
import cz.payola.web.client.events._
import cz.payola.common.geo.Coordinates
import cz.payola.web.shared.transformators.RdfJsonTransformator
import cz.payola.web.client.views.bootstrap.modals.FatalErrorModal

/**
 * @author Jiri Helmich
 */
abstract class MapView(prefixApplier: Option[PrefixApplier] = None) extends PluginView[Any]("Map", prefixApplier) {

    val primaryFacetChanged = new SimpleBooleanEvent[MapFacet]

    protected var facets: Seq[MapFacet] = List()
    protected var primaryFacet: Option[MapFacet] = None

    def createLibWrapper(element: Element) : View

    val facetPlaceholder = new Div(List(),"facet-placeholder col-lg-3")
    val mapPlaceholder = new Div(List(),"map-placeholder col-lg-9")

    var markerData : Seq[Marker] = List()

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

    override def updateSerializedGraph(serializedGraph: Option[Any]) {
        RdfJsonTransformator.queryProperties(evaluationId.get, "select distinct ?p where {[] <http://schema.org/geo> []; ?p [] .}"){ properties =>

            facets = properties.map{ p =>
                val facet = new GroupingMapFacet(p)
                facet.primaryRequested += { e =>
                    primaryFacetChanged.trigger(new EventArgs[MapFacet](e.target))
                    false
                }

                primaryFacetChanged += { e =>
                    primaryFacet = Some(e.target)
                    if(e.target == facet){
                        facet.becamePrimary()
                    }else{
                        facet.unsetPrimary()
                    }
                    false
                }

                facet
            }

            facets.headOption.map{ f =>
                primaryFacetChanged.trigger(new EventArgs[MapFacet](f))
            }

            facetPlaceholder.removeAllChildNodes()

            serializedGraph.map{ sg =>
                parseJSON(sg)
                facets.foreach(_.createSubViews.foreach( v => v.render(facetPlaceholder.blockHtmlElement) ))

                val map = createLibWrapper(mapPlaceholder.htmlElement)

                new Marker(new Coordinates(0,0), "", "") //J4F

                mapPlaceholder.removeAllChildNodes()
                map.render(mapPlaceholder.htmlElement)
            }
        }  { err => }
    }

    @javascript(
        """
           var places = [];
           var facets = self.facets.getInternalJsArray();

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

                                for (var mf in facets){
                                    facets[mf].registerUri(uri, json, marker);
                                }
                            }
                        }
                    }
                }
           }

          var coll = new scala.collection.Seq();
          coll.internalJsArray = places;
          self.markerData = coll;
        """)
    def parseJSON(json: Any) {}

    def createSubViews = {
        List(facetPlaceholder, mapPlaceholder)
    }

    override def isAvailable(availableTransformators: List[String], evaluationId: String,
        success: () => Unit, fail: () => Unit) {

         success() //TODO when is available????
    }

    override def loadDefaultCachedGraph(evaluationId: String, updateGraph: Option[Any] => Unit) {
        RdfJsonTransformator.getCompleteGraph(evaluationId)(updateGraph(_)) //TODO default graph and paginating
        { error =>
            val modal = new FatalErrorModal(error.toString())
            modal.render()
        }
    }
}
