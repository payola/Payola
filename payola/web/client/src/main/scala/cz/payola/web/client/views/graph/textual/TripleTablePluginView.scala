package cz.payola.web.client.views.graph.textual

import scala.collection._
import s2js.adapters.js.browser._
import s2js.adapters.js.dom.Element
import cz.payola.common.rdf._
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.graph.visual.settings.components.visualsetup.VisualSetup
import cz.payola.web.client.views.graph._
import cz.payola.web.client.views.VertexEventArgs
import cz.payola.web.client.views.bootstrap.Icon
import cz.payola.web.client.View
import cz.payola.common.entities.settings.OntologyCustomization

/**
  * A plugin that displays all edges in the graph as a table. The edges are firstly grouped by the edge origins,
  * secondly by the edge types.
  */
class TripleTablePluginView(settings: VisualSetup) extends PluginView("Triple Table")
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
            val tableHead = addElement(table, "thead")
            val tableBody = addElement(table, "tbody")

            // Create the headers.
            val headerRow = addRow(tableHead)
            List("Subject", "Property", "Value").foreach { title =>
                val cell = addCell(headerRow, isHeader = true)
                cell.innerHTML = title
            }

            // Fill the table with cells.
            groupEdges(graph).foreach { edgesByOrigin =>
                var originCell: Element = null
                var originRowCount = 0

                edgesByOrigin._2.foreach { edgesByEdgeType =>
                    val edgeUri = edgesByEdgeType._1
                    val edges = edgesByEdgeType._2
                    val origin = edges.head.origin
                    val row = addRow(tableBody)

                    // The origin vertex cell.
                    originRowCount += 1
                    if (originCell == null) {
                        originCell = addCell(row)
                        createVertexView(origin).render(originCell)
                    }

                    // The edge cell.
                    val edgeCell = addCell(row)
                    new Text(edgeUri).render(edgeCell)

                    // The destinations cell.
                    val destinationsCell = addCell(row)
                    val destinationListItems = edges.map { (edge: Edge) =>
                        val vertexElement = edge.destination match {
                            case iv: IdentifiedVertex => createVertexView(iv)
                            case lv: LiteralVertex => new Text(lv.value.toString)
                            case v => new Text(v.toString)
                        }
                        new ListItem(List(vertexElement))
                    }
                    new UnorderedList(destinationListItems, "unstyled").render(destinationsCell)
                }
                originCell.setAttribute("rowspan", originRowCount.toString)
            }
        }

        super.updateGraph(graph)
    }

    private def groupEdges(graph: Option[Graph]): Map[String, Map[String, Seq[Edge]]] = {
        val edgesByOrigin = new mutable.HashMap[String, mutable.HashMap[String, mutable.ListBuffer[Edge]]]
        graph.foreach {
            _.edges.foreach { edge =>
                val edgesByEdgeType = edgesByOrigin.getOrElseUpdate(edge.origin.uri,
                    new mutable.HashMap[String, mutable.ListBuffer[Edge]])
                edgesByEdgeType.getOrElseUpdate(edge.uri, new mutable.ListBuffer[Edge]) += edge
            }
        }

        edgesByOrigin
    }

    private def createVertexView(vertex: IdentifiedVertex): View = {
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

    private def addRow(table: Element): Element = addElement(table, "tr")

    private def addCell(row: Element, isHeader: Boolean = false) = addElement(row, if (isHeader) "th" else "td")

    private def addElement(parent: Element, name: String): Element = {
        val element = document.createElement[Element](name)
        parent.appendChild(element)
        element
    }
}
