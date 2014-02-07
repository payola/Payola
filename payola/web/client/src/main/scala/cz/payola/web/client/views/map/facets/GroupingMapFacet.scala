package cz.payola.web.client.views.map.facets

import cz.payola.web.client.views.elements._
import s2js.runtime.client.scala.collection.immutable.HashMap
import s2js.compiler.javascript
import cz.payola.web.client.views.map.Marker
import scala.collection.mutable.ArrayBuffer
import cz.payola.web.client.views.bootstrap.Icon

class GroupingMapFacet(typeUri: String = "http://www.w3.org/2000/01/rdf-schema#type") extends MapFacet
{

    private val runBtnIcon = new Icon(Icon.play, true)
    val hider = new Button(new Text("abcd"), "btn btn-success span2", runBtnIcon)
    hider.mouseClicked += { e=>
        markers.foreach(_.isVisible = false)
        false
    }

    val facetContainer = new Div(List(hider), "facetContainer")
    val groups = new HashMap[String, Seq[Marker]]
    val markers = new ArrayBuffer[Marker]()

    @javascript(""" console.log(x) """)
    def log(x: Any) {}

    def registerUri(uri: String, jsonGraphRepresentation: String, marker: Marker){
        //log(uri)
        markers += marker
    }

    def groupsCount = groups.size

    def namedMarkerGroups = {
        val hashMap = new HashMap[String, Seq[Marker]]
        hashMap.put("global", markers.toList)
        hashMap
    }
    // = groups

    def createSubViews = List(facetContainer)
}
