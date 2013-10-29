package cz.payola.web.client.views.graph.datacube

import cz.payola.common.geo.Coordinates

/**
 * @author Jiri Helmich
 */
case class TimeObservation(coordinates: Coordinates, year: String, value: Int)
