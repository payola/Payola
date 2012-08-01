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

    var dataSourceButtonPressed = new SimpleUnitEvent[IdentifiedVertex]

    def createSubViews: Seq[View] = {
        val buffer = new ArrayBuffer[ListItem]()

        val browsingButton = new Anchor(List(new Icon(Icon.hdd)))
        browsingButton.mouseClicked += { e =>
            dataSourceButtonPressed.triggerDirectly(vertex)
            false
        }
        buffer += new ListItem(List(browsingButton))

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
        div.setAttribute("class","popover fade right in vitable")
        div.setAttribute("style","top: "+(position.y-10).toString()+"px; left: "+position.x.toString()+"px;")
        List(div)
    }
}
