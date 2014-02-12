package cz.payola.web.client.views.map.facets

import cz.payola.web.client.views.ComposedView
import cz.payola.web.client.views.map.Marker
import scala.collection.mutable
import cz.payola.web.client.events.SimpleBooleanEvent

trait MapFacet extends ComposedView
{
    val primaryRequested = new SimpleBooleanEvent[MapFacet]

    def registerUri(uri: String, jsonGraphRepresentation: String, marker: Marker)

    def groupsCount : Int

    def namedMarkerGroups : mutable.HashMap[String, mutable.ArrayBuffer[Marker]]

    def becamePrimary()

    def unsetPrimary()
}