package cz.payola.web.client.views.map

import cz.payola.common.geo.Coordinates
import cz.payola.web.client.views.ComposedView
import s2js.compiler.javascript
import s2js.adapters.dom.Element

class MapView(center: Coordinates, zoom: Int, mapType: String, heatData: Seq[(Coordinates, Double)], element: Element) extends ComposedView
{

    def createSubViews = {
        loadMapsScript
        List()
    }

    @javascript(
        """
          window.mapCallback = self.createMap
          window.selfContext = self
          var url = "https://maps.googleapis.com/maps/api/js?key=AIzaSyCesJn7XyQUzK78CRLwJusuLUR1-Wy8fVc&sensor=false&libraries=visualization&callback=window.mapCallback";

          $.getScript(url);

        """)
    def loadMapsScript {}

    @javascript("""

                  var map = new google.maps.Map(window.selfContext.element, {
                    center: new google.maps.LatLng(0,0),
                    zoom: window.selfContext.zoom,
                    mapTypeId: window.selfContext.mapType
                  });

                  var heatmap = new google.maps.visualization.HeatmapLayer({
                    data: window.selfContext.getData()
                  });

                  heatmap.setMap(map);

                """)
    def createMap {  }

    @javascript("""

                var data = [];

                for (var k in self.heatData.getInternalJsArray()) {
                    var item = self.heatData.getInternalJsArray()[k];
                    data.push({location: new google.maps.LatLng(item._1.lat, item._1.lng), weight: item._2});
                }

                return data;
                """)
    def getData {}
}
