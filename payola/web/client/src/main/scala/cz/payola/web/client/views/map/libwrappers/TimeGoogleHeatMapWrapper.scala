package cz.payola.web.client.views.map.libwrappers

import cz.payola.common.geo.Coordinates
import cz.payola.web.client.views.ComposedView
import cz.payola.web.client.views.elements.Div
import cz.payola.web.client.views.elements.form.Label
import cz.payola.web.client.views.elements.form.fields.CheckBox
import cz.payola.web.client.views.elements.lists._
import cz.payola.web.client.views.graph.datacube.TimeObservation
import s2js.adapters.dom.Element
import s2js.compiler.javascript
import scala.collection.mutable

/**
 * Google Maps wrapper, mostly written in JavaScript. Just creates subviews and renders.
 * @author Jiri Helmich
 */
class TimeGoogleHeatMapWrapper(center: Coordinates, zoom: Int, mapType: String, heatData: Seq[TimeObservation],
    yearList: mutable.ArrayBuffer[String], hashMap: mutable.HashMap[String, mutable.ArrayBuffer[TimeObservation]],
    element: Element) extends ComposedView
{
    @javascript( """console.log(x)""")
    def log(x: Any) {}

    val items = yearList.sortWith(_.toInt < _.toInt).map {
        y =>
            val checkbox = new CheckBox(y, true, y)
            checkbox.mouseClicked += { e =>
                toggleLayer(y)
                true
            }
            val label = new Label(y, checkbox.htmlElement)
            new ListItem(List(label, checkbox))
    }

    val list = new UnorderedList(items)

    val filterDiv = new Div(List(list), "mapview-filter")

    val mapDiv = new Div(List(), "mapview")

    def createSubViews = {
        loadMapsScript

        List(mapDiv, filterDiv)
    }

    /**
     * Switch a layer on or off
     * @param year Year of the layer.
     */
    @javascript(
        """
           var heatmapLayer = window.mapData.yearlyHeatmap[year];
           if (heatmapLayer){
            var enabled = heatmapLayer.enabled;
            if (enabled){
                heatmapLayer.layer.setMap(null);
                heatmapLayer.enabled = false;
            }else{
                heatmapLayer.layer.setMap(window.mapData.map);
                heatmapLayer.enabled = true;
            }
           }
        """)
    def toggleLayer(year: String) {}

    /**
     * Init GMaps API
     */
    @javascript(
        """
          window.mapCallback = self.createMap
          window.selfContext = self
          var url = "https://maps.googleapis.com/maps/api/js?key=AIzaSyCesJn7XyQUzK78CRLwJusuLUR1-Wy8fVc&sensor=false&libraries=visualization&callback=window.mapCallback";

          $.getScript(url);

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

                  window.mapData = { map: map, yearlyHeatmap: {} };

                  var yearlyData = window.selfContext.getAllData();
                  for (var y in yearlyData){
                    var data = yearlyData[y];
                    var heatmap = new google.maps.visualization.HeatmapLayer({
                        data: data
                    });

                    window.mapData.yearlyHeatmap[y] = {layer: heatmap, enabled: true};
                    heatmap.setMap(map);
                  }

                 """)
    def createMap {}

    /**
     * Gather all data from passed data strucutres.
     */
    @javascript( """

                var data = [];
                var map = {};

                for (var k in self.heatData.getInternalJsArray()) {
                    var item = self.heatData.getInternalJsArray()[k];
                    var marker = {location: new google.maps.LatLng(item.coordinates.lat, item.coordinates.lng),
                    weight: item.value/100};
                    //data.push(marker);

                    var yearData = map[item.year] || [];
                    yearData.push(marker);
                    map[item.year] = yearData;
                }

                return map;
                 """)
    def getAllData {}
}
