package cz.payola.web.client.views.graph.table

import s2js.adapters.browser._
import s2js.adapters.html
import cz.payola.common.rdf._
import cz.payola.web.client.View
import cz.payola.web.client.views.VertexEventArgs
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.bootstrap.Icon
import cz.payola.web.client.views.graph.PluginView
import cz.payola.web.client.models.PrefixApplier

abstract class TablePluginView(name: String, prefixApplier: Option[PrefixApplier] = None)
    extends PluginView(name, prefixApplier)
{
    protected val tableWrapper = new Div().setAttribute("style", "padding: 0 20px; min-height: 200px;")

    def createSubViews = List(tableWrapper)

    override def updateGraph(graph: Option[Graph], contractLiterals: Boolean = true) {
        if (graph != currentGraph) {
            // Remove the old table.
            tableWrapper.removeAllChildNodes()

            if (graph.isEmpty) {
                renderMessage(tableWrapper.htmlElement, "The graph is empty...")
            } else {
                val table = document.createElement[html.Element]("table")
                table.className = "table table-striped table-bordered table-condensed"
                tableWrapper.htmlElement.appendChild(table)
                fillTable(graph, addElement(table, "thead"), addElement(table, "tbody"))
            }
        }

        super.updateGraph(graph, true)
    }

    def fillTable(graph: Option[Graph], tableHead: html.Element, tableBody: html.Element)

    protected def createVertexView(vertex: IdentifiedVertex): View = {
        val dataSourceAnchor = new Anchor(List(new Icon(Icon.hdd)))
        dataSourceAnchor.mouseClicked += { e =>
            vertexBrowsingDataSource.trigger(new VertexEventArgs[this.type](this, vertex))
            false
        }
        val uri = prefixApplier.map(_.applyPrefix(vertex.uri)).getOrElse(vertex.uri)
        val browsingAnchor = new Anchor(List(new Text(uri)))
        browsingAnchor.mouseClicked += { e =>
            vertexBrowsing.trigger(new VertexEventArgs[this.type](this, vertex))
            false
        }

        new Span(List(dataSourceAnchor, new Span(List(new Text(" "))), browsingAnchor))
    }

    protected def addRow(table: html.Element): html.Element = addElement(table, "tr")

    protected def insertRow(table: html.Element, insertBefore: html.Element): html.Element = insertElement(table, insertBefore, "tr")

    protected def addCell(row: html.Element, isHeader: Boolean = false) = addElement(row, if (isHeader) "th" else "td")

    private def insertElement(parent: html.Element, followingSibling: html.Element, name: String): html.Element = {
        val element = document.createElement[html.Element](name)
        parent.insertBefore(element, followingSibling)
        element
    }

    private def addElement(parent: html.Element, name: String): html.Element = {
        val element = document.createElement[html.Element](name)
        parent.appendChild(element)
        element
    }
}
