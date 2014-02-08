package cz.payola.web.client.views.map.facets

import cz.payola.web.client.views.elements._
import s2js.runtime.client.scala.collection.immutable.HashMap
import s2js.compiler.javascript
import cz.payola.web.client.views.map.Marker
import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer
import cz.payola.web.client.views.bootstrap.Icon
import cz.payola.web.client.views.elements.form.fields.CheckBox

class GroupingMapFacet(typeUri: String = "http://www.w3.org/2000/01/rdf-schema#type") extends MapFacet
{

    val panelContent = new Div(List())
    val panel = new Div(List(new Strong(List(new Text("Filter by value of <"+typeUri+">:"))), panelContent),"well")

    val facetContainer = new Div(List(panel), "facetContainer")
    val groups = new mutable.HashMap[String, ArrayBuffer[Marker]]
    val markers = new ArrayBuffer[Marker]()

    @javascript(""" console.log(x) """)
    def log(x: Any) {}

    @javascript(
        """
           var entity = jsonGraphRepresentation[uri];
           if (entity[self.typeUri]){
                return new scala.Some(entity[self.typeUri][0].value);
           }
           return scala.None.get();
        """)
    def getGroupValue(uri: String, jsonGraphRepresentation: String): Option[String] = None

    def registerUri(uri: String, jsonGraphRepresentation: String, marker: Marker){
        val groupValue = getGroupValue(uri, jsonGraphRepresentation)
        groupValue.map{ g =>
            if (!groups.isDefinedAt(g)){
                groups.put(g, new ArrayBuffer[Marker])
            }
            groups.get(g).foreach(_.append(marker))
        }
        markers += marker
    }

    def groupsCount = groups.size

    def namedMarkerGroups = groups

    def createSubViews = {

        groups.keys.foreach{ k =>
            val cbox = new CheckBox(k,true)
            val span = new Div(List(cbox, new Text(k)))

            cbox.changed += { e =>
                groups.get(k).foreach(_.foreach{ _.isVisible = e.target.value } )
            }

            span.render(panelContent.blockHtmlElement)
        }

        List(facetContainer)
    }
}
