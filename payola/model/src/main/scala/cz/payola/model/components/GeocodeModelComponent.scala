package cz.payola.model.components

import cz.opendata.tenderstats.Geocoder
import cz.opendata.tenderstats.Geocoder.GeoProviderFactory
import cz.payola.common.geo.Coordinates

/**
 * GeoCoder (by Matej Snoha) wrapper for Payola.
 * @author Jiri Helmich
 */
trait GeocodeModelComponent
{
    lazy val geocodeModel = new
        {
            /**
             * Returns Coordinates based on location name.
             * @param place Location name
             * @return Coordinates
             */
            def geocode(place: String): Option[cz.payola.common.geo.Coordinates] = {

                Geocoder.loadCacheIfEmpty("cache/geocoder.cache");
                val gisgraphy = GeoProviderFactory.createXMLGeoProvider(
                    "http://xrg15.projekty.ms.mff.cuni.cz:5555/fulltext/fulltextsearch?allwordsrequired=false&from=1&to=1&q=",
                    "/response/result/doc[1]/double[@name=\"lat\"]",
                    "/response/result/doc[1]/double[@name=\"lng\"]",
                    0);
                val pos = Geocoder.locate(place, gisgraphy);
                val coordinates = if (pos != null) {
                    Some(new Coordinates(pos.getLatitude, pos.getLongitude))
                } else {
                    None
                }
                Geocoder.saveCache("cache/geocoder.cache");
                coordinates
            }
        }
}
