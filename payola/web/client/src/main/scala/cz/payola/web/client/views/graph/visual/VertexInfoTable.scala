package cz.payola.web.client.views.graph.visual

import cz.payola.web.client.View
import cz.payola.web.client.views.elements._
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable
import cz.payola.web.client.views.bootstrap.Icon
import cz.payola.web.client.views._
import cz.payola.common.rdf.IdentifiedVertex
import cz.payola.web.client.events._
import cz.payola.web.client.views.algebra.Point2D

class VertexInfoTable(vertex: IdentifiedVertex, values: mutable.HashMap[String, Seq[String]], position: Point2D) extends ComposedView
{


    var vertexBrowsingDataSource = new SimpleUnitEvent[IdentifiedVertex]
    var vertexBrowsing = new SimpleUnitEvent[IdentifiedVertex]

    def createSubViews: Seq[View] = {
        val buffer = new ArrayBuffer[ElementView[_]]()

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

        var even = true
        values.foreach { x =>
            buffer += new DefinitionTerm(List(new Text(x._1)))

            x._2.foreach { string =>
                buffer += new DefinitionDefinition(List(new Text(string)))
            }

            even = !even
        }

        val popoverTitle = new Heading(List(dataSourceAnchor, new Span(List(new Text(" "))), browsingAnchor),3,"popover-title")
        val popoverContent = new Div(List(new DefinitionList(buffer, "unstyled well")),"popover-content")
        val popoverInner = new Div(List(popoverTitle, popoverContent), "popover-inner")
        val popoverArrow = new Div(Nil,"arrow")
        popoverArrow.setAttribute("style","top: 15px;")
        val div = new Div(List(popoverArrow, popoverInner))
        div.setAttribute("class","popover fade right in vitable")
        div.setAttribute("style","top: "+(position.y-10).toString()+"px; left: "+position.x.toString()+"px;")
        List(div)
    }
}
