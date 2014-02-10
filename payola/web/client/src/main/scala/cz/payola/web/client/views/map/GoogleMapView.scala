package cz.payola.web.client.views.map

import cz.payola.common.geo.Coordinates
import cz.payola.web.client.models.PrefixApplier
import cz.payola.web.client.views.map.libwrappers.GoogleMapsWrapper
import s2js.adapters.dom.Element
import cz.payola.web.client.views.map.facets.MapFacet

/**
 * @author Jiri Helmich
 */
class GoogleMapView(prefixApplier: Option[PrefixApplier] = None) extends MapView(prefixApplier) {

    override val name = "Google Map"

    def createLibWrapper(element: Element) = {
        val center = new Coordinates(0,0)
        new GoogleMapsWrapper(center, 3, "satellite", facets, markerData, element)
    }
}
