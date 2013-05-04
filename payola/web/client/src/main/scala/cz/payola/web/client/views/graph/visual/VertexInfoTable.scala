package cz.payola.web.client.views.graph.visual

import cz.payola.web.client.View
import cz.payola.web.client.views.elements._
import graph.VertexView
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable
import cz.payola.web.client.views.bootstrap.Icon
import cz.payola.web.client.views._
import cz.payola.common.rdf.IdentifiedVertex
import cz.payola.web.client.events._
import cz.payola.web.client.views.algebra._
import cz.payola.web.client.views.elements.lists._

class VertexInfoTable(vertex: IdentifiedVertex, values: mutable.HashMap[String, Seq[String]], position: Point2D,
    setMainVertexEnabled: Boolean) extends ComposedView
{
    var vertexBrowsingDataSource = new SimpleUnitEvent[IdentifiedVertex]

    var vertexBrowsing = new SimpleUnitEvent[IdentifiedVertex]

    var vertexSetMain = new SimpleUnitEvent[IdentifiedVertex]

    def createSubViews: Seq[View] = {
        val buffer = new ArrayBuffer[ElementView[_]]()

        val dataSourceAnchor = new Anchor(List(new Icon(Icon.hdd)))
        dataSourceAnchor.mouseClicked += { e =>
            vertexBrowsingDataSource.triggerDirectly(vertex)
            false
        }

        val setMainVertex = new Anchor(List(new Icon(Icon.flag)))
        setMainVertex.mouseClicked += { e =>
            vertexSetMain.triggerDirectly(vertex)
            false
        }

        val browsingAnchor = new Anchor(List(new Text(vertex.uri)))
        browsingAnchor.mouseClicked += { e =>
            vertexBrowsing.triggerDirectly(vertex)
            false
        }

        values.foreach { x =>
            buffer += new DefinitionTerm(List(new Text(x._1)))

            x._2.foreach { string =>
                buffer += new DefinitionDefinition(List(new Text(string)))
            }
        }

        val popoverTitle =
            if(setMainVertexEnabled)
                new Heading(List(setMainVertex, dataSourceAnchor, new Span(List(new Text(" "))), browsingAnchor), 3, "popover-title")
            else
                new Heading(List(dataSourceAnchor, new Span(List(new Text(" "))), browsingAnchor), 3, "popover-title")

        val popoverContent = if(!values.isEmpty) {
            new Div(List( new DefinitionList(buffer, "unstyled well")), "popover-content")
        } else {
            new Div(List(), "popover-content")
        }
        val popoverInner = new Div(List(popoverTitle, popoverContent), "popover-inner")
        val div = new Div(List(popoverInner))
        div.setAttribute("class", "popover fade in vitable")
        div.setAttribute("style",
            "top: %dpx; left: %dpx; z-index: 1000;".format(position.y, position.x))
        List(div)
    }

    def getSize: Vector2D = {
        Vector2D(this.blockHtmlElement.clientWidth, this.blockHtmlElement.clientHeight)
    }

    def setPosition(position: Point2D) {
        blockHtmlElement.setAttribute("style",
            "top: %dpx; left: %dpx; z-index: 1000;".format(position.y, position.x))
    }
}
