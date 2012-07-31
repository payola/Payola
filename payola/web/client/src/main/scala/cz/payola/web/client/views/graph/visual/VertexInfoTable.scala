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

        val div = new Div(List(new UnorderedList(buffer, "span5 unstyled well")))
        div.setAttribute("rel","popover")
        div.setAttribute("style","position: absolute; top: "+position.y.toString()+"px; left: "+position.x.toString()+"px;")
        List(div)
    }

    @javascript("""jQuery(e).popover("show")""")
    def activatePopover(e: Element){}

    override def render(parent: Element){
        super.render(parent)
        activatePopover(subViews.head.blockDomElement)
    }
}
