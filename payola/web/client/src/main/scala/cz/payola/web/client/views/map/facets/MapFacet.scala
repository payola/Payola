package cz.payola.web.client.views.map.facets

import cz.payola.web.client.views.ComposedView

trait MapFacet extends ComposedView
{
    def registerUri(uri: String, jsonGraphRepresentation: String)
}