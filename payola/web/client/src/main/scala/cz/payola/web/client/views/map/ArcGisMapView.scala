package cz.payola.web.client.views.map

import cz.payola.common.geo.Coordinates
import cz.payola.web.client.models.PrefixApplier
import cz.payola.web.client.views.map.libwrappers._
import cz.payola.common.geo.Coordinates
import s2js.adapters.dom.Element

/**
 * @author Jiri Helmich
 */
class ArcGisMapView(prefixApplier: Option[PrefixApplier] = None) extends MapView {

    override val name = "ArcGis Map"

    def createLibWrapper(markers: Seq[Marker], element: Element) = {
        val center = new Coordinates(0,0)
        new ArcGisMapsWrapper(center, markers, element)
    }
}
