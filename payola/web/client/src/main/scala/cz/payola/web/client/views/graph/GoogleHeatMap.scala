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
class GoogleHeatMap(prefixApplier: Option[PrefixApplier] = None) extends GoogleMap(prefixApplier) {

    override val name = "Google HeatMap"

    override def updateSerializedGraph(serializedGraph: Option[String]) {
        serializedGraph.map{ sg =>
            val markers = fromJSON(sg)

            val center = new Coordinates(0,0)
            val map = new HeatMapView(center, 3, "satellite", markers, mapPlaceholder.htmlElement)

            new MapMarker(center,"","") //just to load it

            mapPlaceholder.removeAllChildNodes()
            map.render(mapPlaceholder.htmlElement)
        }
    }
}
