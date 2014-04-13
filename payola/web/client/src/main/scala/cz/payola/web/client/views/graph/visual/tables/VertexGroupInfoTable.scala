package cz.payola.web.client.views.graph.visual.tables

import cz.payola.web.client.views.graph.visual.graph._
import cz.payola.web.client.views._
import bootstrap.Icon
import cz.payola.web.client.views.algebra._
import cz.payola.web.client.events.SimpleUnitEvent
import collection.mutable.ArrayBuffer
import cz.payola.web.client.models.PrefixApplier
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.elements.lists._
import cz.payola.common.rdf._
import cz.payola.web.client
import form.fields._

class VertexGroupInfoTable(group: VertexViewGroup, position: Point2D, prefixApplier: Option[PrefixApplier]) extends InfoTable
{
    var removeVertexFromGroup = new SimpleUnitEvent[VertexViewElement]
    var removeAllFromGroup = new SimpleUnitEvent[List[VertexViewElement]]
    val groupNameField = new TextInput("GroupName", group.getName)
    private val tableStyle = "top: %dpx; left: %dpx; z-index:1000; display:block; overflow:auto; height:250px; width:250px; max-width: none;"
    private var verticesListElement: Option[DefinitionList] = None

    def createSubViews: Seq[client.View] = {
        val buffer = new ArrayBuffer[ElementView[_]]()

        group.vertexViewsLabels.foreach { vertexTuple =>
            val removeVertexIcon = new Anchor(List(new Icon(Icon.share)), "#", "", "Unpack "+vertexTuple._2)
            removeVertexIcon.mouseClicked += { e =>
                removeVertexFromGroup.triggerDirectly(vertexTuple._1)
                false
            }

            buffer += new DefinitionTerm(List(removeVertexIcon, new Text(vertexTuple._2)))
        }

        val removeAll = new Anchor(List(new Icon(Icon.share_alt), new Text("Unpack all")))
        removeAll.mouseClicked += { e =>

            removeAllFromGroup.triggerDirectly(group.vertexViews.clone.toList)
            false
        }

        val popoverTitle =
            new Heading(List(new Text("Group: "), groupNameField,
                new Heading(List(new Text("count: "+group.vertexViews.size), removeAll), 5)), 3, "popover-title")

        val popoverContent = if(!buffer.isEmpty) {
            verticesListElement = Some(new DefinitionList(buffer, ""))
            new Div(List(verticesListElement.get), "")
        } else {
            new Div(List(), "popover-content")
        }
        val popoverInner = new Div(List(popoverTitle, popoverContent), "")
        val div = new Div(List(popoverInner))
        div.setAttribute("class", "popover fade in resizable")
        div.setAttribute("style", tableStyle.format(position.y, position.x))
        List(div)
    }

    def getSize: Vector2D = {
        Vector2D(this.blockHtmlElement.clientWidth, this.blockHtmlElement.clientHeight)
    }

    def setPosition(position: Point2D) {
        blockHtmlElement.setAttribute("style", tableStyle.format(position.y, position.x))
    }

    def updateStyle() {
        verticesListElement.map(_.setAttribute("style", "margin: 0px; padding: 19px; overflow-wrap: break-word; max-width: none;"))
    }
}
