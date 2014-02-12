package cz.payola.web.client.views.map.facets

import cz.payola.web.client.views.elements.Div
import cz.payola.web.client.views.map.Marker
import s2js.compiler.javascript
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

class PlainMapFacet(typeUri: String = "http://www.w3.org/2000/01/rdf-schema#type") extends MapFacet
{
    val facetContainer = new Div()
    val markers = new ArrayBuffer[Marker]()

    @javascript(""" console.log(x) """)
    def log(x: Any) {}

    def registerUri(uri: String, jsonGraphRepresentation: String, marker: Marker){
        markers += marker
    }

    def becamePrimary(){}

    def unsetPrimary(){}

    def groupsCount = 1

    def namedMarkerGroups = {
        val hashMap = new mutable.HashMap[String, ArrayBuffer[Marker]]
        hashMap.put("global", markers)
        hashMap
    }

    def createSubViews = List(facetContainer)
}
