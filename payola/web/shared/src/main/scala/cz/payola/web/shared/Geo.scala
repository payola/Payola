package cz.payola.web.shared

import s2js.compiler._
import cz.payola.common.geo.Coordinates

/**
 * Remote object for using GeoCoder from the client-side.
 * @author Jiri Helmich
 */
@remote object Geo
{
    @async def geocode(place: String)(successCallback: (Option[Coordinates] => Unit))
        (failCallback: (Throwable => Unit)) {
        val coordinates = Payola.model.geocodeModel.geocode(place)

        successCallback(coordinates)
    }

    @async def geocodeBatch(places: Seq[String])(successCallback: (Seq[Option[Coordinates]] => Unit))
        (failCallback: (Throwable => Unit)) {
        val coordinates = places.map {
            place =>
                Payola.model.geocodeModel.geocode(place)
        }

        successCallback(coordinates)
    }
}
