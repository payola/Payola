package cz.payola.web.client.views.map.libwrappers

import cz.payola.common.geo.Coordinates
import cz.payola.web.client.views.ComposedView
import cz.payola.web.client.views.elements.Div
import cz.payola.web.client.views.map.Marker
import s2js.adapters.dom.Element
import s2js.compiler.javascript
import cz.payola.web.client.views.map.facets.MapFacet

/**
 * Google Maps wrapper, mostly written in JavaScript. Just creates subviews and renders.
 * @author Jiri Helmich
 */
class GoogleHeatMapWrapper(center: Coordinates, zoom: Int, mapType: String, facets: Seq[MapFacet], markerData: Seq[Marker], element: Element) extends ComposedView
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

                    var data = [];

                    for (var k in window.selfContext.markerData.getInternalJsArray()) {
                       var item = window.selfContext.markerData.getInternalJsArray()[k];


                       var marker = {location: new google.maps.LatLng(item.coordinates.lat, item.coordinates.lng), weight: 1};
                       data.push(marker);

                       console.log(marker);
                    }

                    var heatmap = new google.maps.visualization.HeatmapLayer({
                        data: data
                    });

                    heatmap.setMap(map);
                 """)
    def createMap {}

}
