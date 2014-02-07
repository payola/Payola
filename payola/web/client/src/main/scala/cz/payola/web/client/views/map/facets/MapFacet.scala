package cz.payola.web.client.views.map.facets

import cz.payola.web.client.views.ComposedView
import cz.payola.web.client.views.map.Marker
import s2js.runtime.client.scala.collection.immutable.HashMap

trait MapFacet extends ComposedView
{
    def registerUri(uri: String, jsonGraphRepresentation: String, marker: Marker)

    def groupsCount : Int

    def namedMarkerGroups : HashMap[String, Seq[Marker]]
}