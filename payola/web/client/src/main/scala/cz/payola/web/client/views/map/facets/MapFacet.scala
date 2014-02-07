package cz.payola.web.client.views.map.facets

import cz.payola.web.client.views.ComposedView
import cz.payola.web.client.views.map.Marker
import scala.collection.mutable

trait MapFacet extends ComposedView
{
    def registerUri(uri: String, jsonGraphRepresentation: String, marker: Marker)

    def groupsCount : Int

    def namedMarkerGroups : mutable.HashMap[String, mutable.ArrayBuffer[Marker]]
}