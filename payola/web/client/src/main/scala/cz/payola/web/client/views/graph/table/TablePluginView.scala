package cz.payola.web.client.views.graph.table

import cz.payola.web.client.views.graph.PluginView
import s2js.adapters.js.dom.Element
import cz.payola.common.rdf._
import cz.payola.web.client.View
import cz.payola.web.client.views.VertexEventArgs
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap.Icon
import s2js.adapters.js.browser.document

abstract class TablePluginView(name: String) extends PluginView(name)
{
    private val tableWrapper = new Div()

    private val tableWrapperElement = tableWrapper.domElement

    tableWrapper.setAttribute("style", "padding: 0 0 0 30px;")

    def createSubViews = List(tableWrapper)

    override def updateGraph(graph: Option[Graph]) {
        if (graph != currentGraph) {
            // Remove the old table.
            tableWrapper.removeAllChildNodes()

            // Insert the new table.
            val table = document.createElement[Element]("table")
            tableWrapperElement.appendChild(table)

            table.className = "table table-striped table-bordered table-condensed"

            fillTable(graph, addElement(table, "thead"), addElement(table, "tbody"))
        }

        super.updateGraph(graph)
    }

    def fillTable(graph: Option[Graph], tableHead: Element, tableBody: Element)

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

    protected def addRow(table: Element): Element = addElement(table, "tr")

    protected def addCell(row: Element, isHeader: Boolean = false) = addElement(row, if (isHeader) "th" else "td")

    protected def addElement(parent: Element, name: String): Element = {
        val element = document.createElement[Element](name)
        parent.appendChild(element)
        element
    }
}
