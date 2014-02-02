package cz.payola.web.client.views.map.facets

import cz.payola.web.client.views.elements.Div
import s2js.runtime.client.scala.collection.immutable.HashMap
import s2js.compiler.javascript

class GroupingMapFacet(typeUri: String = "http://www.w3.org/2000/01/rdf-schema#type") extends MapFacet
{
    val facetContainer = new Div()
    val groups = new HashMap[String, Seq[String]]

    @javascript(""" console.log(x) """)
    def log(x: Any) {}

    def registerUri(uri: String, jsonGraphRepresentation: String){
        log(uri)
    }

    def createSubViews = List(facetContainer)
}
