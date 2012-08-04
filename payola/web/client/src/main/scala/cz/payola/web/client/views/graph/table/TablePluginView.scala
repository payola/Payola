package cz.payola.web.client.views.graph.table

import s2js.adapters.browser._
import s2js.adapters.html
import cz.payola.common.rdf._
import cz.payola.web.client.View
import cz.payola.web.client.views.VertexEventArgs
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap.Icon
import cz.payola.web.client.views.graph.PluginView

abstract class TablePluginView(name: String) extends PluginView(name)
{
    private val tableWrapper = new Div()

    private val tableWrapperElement = tableWrapper.htmlElement

    tableWrapper.setAttribute("style", "padding: 0 0 0 30px;")

    def createSubViews = List(tableWrapper)

    override def updateGraph(graph: Option[Graph]) {
        if (graph != currentGraph) {
            // Remove the old table.
            tableWrapper.removeAllChildNodes()

            // Insert the new table.
            val table = document.createElement[html.Element]("table")
            tableWrapperElement.appendChild(table)

            table.className = "table table-striped table-bordered table-condensed"

            fillTable(graph, addElement(table, "thead"), addElement(table, "tbody"))
        }

        super.updateGraph(graph)
    }

    def fillTable(graph: Option[Graph], tableHead: html.Element, tableBody: html.Element)

    protected def createVertexView(vertex: IdentifiedVertex): View = {
        val dataSourceAnchor = new Anchor(List(new Icon(Icon.hdd)))
        dataSourceAnchor.mouseClicked += { e =>
            vertexBrowsingDataSource.trigger(new VertexEventArgs[this.type](this, vertex))
            false
        }
        val browsingAnchor = new Anchor(List(new Text(vertex.uri)))
        browsingAnchor.mouseClicked += { e =>
            vertexBrowsing.trigger(new VertexEventArgs[this.type](this, vertex))
            false
        }

        new Span(List(dataSourceAnchor, new Span(List(new Text(" "))), browsingAnchor))
    }

    protected def addRow(table: html.Element): html.Element = addElement(table, "tr")

    protected def addCell(row: html.Element, isHeader: Boolean = false) = addElement(row, if (isHeader) "th" else "td")

    protected def addElement(parent: html.Element, name: String): html.Element = {
        val element = document.createElement[html.Element](name)
        parent.appendChild(element)
        element
    }
}
