package cz.payola.web.client.views.graph.visual

import cz.payola.web.client.View
import cz.payola.web.client.views.elements._
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable
import cz.payola.web.client.views.bootstrap.Icon
import cz.payola.web.client.views._
import cz.payola.common.rdf.IdentifiedVertex
import cz.payola.web.client.events._
import s2js.compiler.javascript
import s2js.adapters.js.dom.Element
import cz.payola.web.client.views.algebra.Point2D

class VertexInfoTable(vertex: IdentifiedVertex, values: mutable.HashMap[String, Seq[String]], position: Point2D) extends ComposedView
{


    var vertexBrowsingDataSource = new SimpleUnitEvent[IdentifiedVertex]
    var vertexBrowsing = new SimpleUnitEvent[IdentifiedVertex]

    def createSubViews: Seq[View] = {
        val buffer = new ArrayBuffer[ListItem]()

        val dataSourceAnchor = new Anchor(List(new Icon(Icon.hdd)))
        dataSourceAnchor.mouseClicked += { e =>
            vertexBrowsingDataSource.triggerDirectly(vertex)
            false
        }
        val browsingAnchor = new Anchor(List(new Text(vertex.uri)))
        browsingAnchor.mouseClicked += { e =>
            vertexBrowsing.triggerDirectly(vertex)
            false
        }

        buffer += new ListItem(List(dataSourceAnchor, new Span(List(new Text(" "))), browsingAnchor))

        var even = true
        values.foreach {
            x =>
                val innerList = x._2.map {
                    string =>
                        new ListItem(List(new Text(string)))
                }

                buffer += new ListItem(List(new Text(x._1), new UnorderedList(innerList)), "badge " + (if (even) {
                    "badge-info"
                }))
                even = !even
        }

        val popoverTitle = new Heading(List(new Text("Vertex info")),3,"popover-title")
        val popoverContent = new Div(List(new UnorderedList(buffer, "unstyled well")),"popover-content")
        val popoverInner = new Div(List(popoverTitle, popoverContent), "popover-inner")
        val popoverArrow = new Div(Nil,"arrow")
        popoverArrow.setAttribute("style","top: 15px;")
        val div = new Div(List(popoverArrow, popoverInner))
        div.setAttribute("class","popover fade right in")
        div.setAttribute("style","display: block; max-height: 300px; position: absolute; top: "+(position.y-10).toString()+"px; left: "+position.x.toString()+"px;")
        List(div)
    }
}
