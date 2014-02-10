package cz.payola.web.client.views.map

import cz.payola.common.geo.Coordinates
import cz.payola.web.client.models.PrefixApplier
import cz.payola.web.client.views.map.libwrappers._
import cz.payola.common.geo.Coordinates
import s2js.adapters.dom.Element
import cz.payola.web.client.views.map.facets.MapFacet

/**
 * @author Jiri Helmich
 */
class ArcGisMapView(prefixApplier: Option[PrefixApplier] = None) extends MapView(prefixApplier) {

    override val name = "ArcGis Map"

    def createLibWrapper(element: Element) = {
        val center = new Coordinates(0,0)
        new ArcGisMapsWrapper(center, facets, markerData, element)
    }
}
