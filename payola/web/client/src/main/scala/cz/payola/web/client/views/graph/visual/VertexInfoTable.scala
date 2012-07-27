package cz.payola.web.client.views.graph.visual

import cz.payola.web.client.View
import cz.payola.web.client.views.elements._
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable
import cz.payola.web.client.views.bootstrap.Icon
import cz.payola.web.client.views._
import cz.payola.common.rdf.IdentifiedVertex
import cz.payola.web.client.events._

class VertexInfoTable(vertex: IdentifiedVertex, values: mutable.HashMap[String, Seq[String]]) extends ComposedView
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

        List(new UnorderedList(buffer, "span5 unstyled well"))
    }
}
