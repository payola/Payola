package cz.payola.web.client.views.map.libwrappers

import cz.payola.common.geo.Coordinates
import cz.payola.web.client.views.ComposedView
import cz.payola.web.client.views.elements.Div
import cz.payola.web.client.views.elements.form.Label
import cz.payola.web.client.views.elements.form.fields.CheckBox
import cz.payola.web.client.views.elements.lists._
import cz.payola.web.client.views.graph.datacube.TimeObservation
import cz.payola.web.client.views.map.Marker
import s2js.adapters.dom.Element
import s2js.compiler.javascript
import cz.payola.web.client.views.map.facets.MapFacet

/**
 * Google Maps wrapper, mostly written in JavaScript. Just creates subviews and renders.
 * @author Jiri Helmich
 */
class GoogleMapsWrapper(center: Coordinates, zoom: Int, mapType: String, facets: Seq[MapFacet], markerData: Seq[Marker], element: Element) extends ComposedView
{

    val mapDiv = new Div(List(), "mapview")

    def createSubViews = {
        loadMapsScript

        List(mapDiv)
    }

    /**
     * Init GMaps API
     */
    @javascript(
        """
          window.mapCallback = self.createMap
          window.selfContext = self

          window.mapCallback();

        """)
    def loadMapsScript {}

    /**
     * Create map instance.
     */
    @javascript( """
                    var map = new google.maps.Map(window.selfContext.mapDiv.htmlElement, {
                        center: new google.maps.LatLng(0,0),
                        zoom: window.selfContext.zoom,
                        mapTypeId: window.selfContext.mapType
                    });

                    var infowindow = new google.maps.InfoWindow();

                    function getTitle(item){
                        var t = "";
                        if (item.title) {
                            t += item.title;
                        }
                        return t;
                    }

                    for (var k in window.selfContext.markerData.getInternalJsArray()) {
                       var item = window.selfContext.markerData.getInternalJsArray()[k];

                       var marker = new google.maps.Marker({
                          position: new google.maps.LatLng(item.coordinates.lat, item.coordinates.lng),
                          map: map,
                          title: getTitle(item)
                       });

                       window.selfContext.addVisibilityListener(item, marker);

                       var contentString = '<p>'+item.description.replace(/\n/g, "<br />")+'</p>';

                       google.maps.event.addListener(marker, 'click', function(content) {
                            return function(){
                                infowindow.setContent(content);//set the content
                                infowindow.open(map,this);
                            }
                       }(contentString));
                    }

                 """)
    def createMap {}


    @javascript(""" customMarker.setIcon('http://chart.apis.google.com/chart?chst=d_map_pin_letter&chld=%E2%80%A2|'+hexColor) """)
    private def changeCustomMarkerColor(customMarker: Any, hexColor: String) {}

    @javascript(""" customMarker.setVisible(visible); """)
    private def changeCustomMarkerVisibility(customMarker: Any, visible: Boolean) {}

    private def addVisibilityListener(payolaMarker: Marker, customMarker: Any) = {
        payolaMarker.visibilityChanged += { e =>
            changeCustomMarkerVisibility(customMarker, e.target)
            false
        }

        payolaMarker.colorChanged += { e =>
            changeCustomMarkerColor(customMarker, e.target)
            false
        }
    }

}
