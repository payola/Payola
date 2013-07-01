package cz.payola.web.client.views.map

import cz.payola.common.geo.Coordinates
import cz.payola.web.client.views.ComposedView
import s2js.compiler.javascript
import s2js.adapters.dom.Element
import cz.payola.web.client.views.graph.datacube.TimeObservation
import cz.payola.web.client.views.elements.Div
import cz.payola.web.client.views.elements.lists._
import cz.payola.common.geo.Coordinates
import cz.payola.web.client.views.graph.datacube.TimeObservation
import cz.payola.web.client.views.elements.form.fields.CheckBox
import cz.payola.web.client.views.elements.form.Label
import scala.collection.mutable

class MapView(center: Coordinates, zoom: Int, mapType: String, heatData: Seq[TimeObservation], yearList: mutable.ArrayBuffer[String], hashMap: mutable.HashMap[String, mutable.ArrayBuffer[TimeObservation]], element: Element) extends ComposedView
{

    @javascript("""console.log(x)""")
    def log(x: Any) {}

    val items = yearList.map{ y =>
        val checkbox = new CheckBox(y,true,y)
        val label = new Label(y, checkbox.htmlElement)
        new ListItem(List(label, checkbox))
    }

    val list = new UnorderedList(items)
    val filterDiv = new Div(List(list),"mapview-filter")
    val mapDiv = new Div(List(),"mapview")

    def createSubViews = {
        loadMapsScript

        List(mapDiv, filterDiv)
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

                  var map = new google.maps.Map(window.selfContext.mapDiv.htmlElement, {
                    center: new google.maps.LatLng(0,0),
                    zoom: window.selfContext.zoom,
                    mapTypeId: window.selfContext.mapType
                  });

                  var heatmap = new google.maps.visualization.HeatmapLayer({
                    data: window.selfContext.getAllData()
                  });

                  heatmap.setMap(map);

                """)
    def createMap {  }

    @javascript("""

                var data = [];

                for (var k in self.heatData.getInternalJsArray()) {
                    var item = self.heatData.getInternalJsArray()[k];
                    data.push({location: new google.maps.LatLng(item.coordinates.lat, item.coordinates.lng), weight: item.value/100});
                }

                return data;
                """)
    def getAllData {}
}
