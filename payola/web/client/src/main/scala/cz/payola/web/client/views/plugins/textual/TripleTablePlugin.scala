package cz.payola.web.client.views.plugins.textual

import scala.collection._
import s2js.adapters.js.browser._
import s2js.adapters.js.dom.Element
import cz.payola.common.rdf._
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.plugins.Plugin
import cz.payola.web.client.views.plugins.visual.settings.components.visualsetup.VisualSetup

/**
  * A plugin that displays all edges in the graph as a table. The edges are firstly grouped by the edge origins,
  * secondly by the edge types.
  */
class TripleTablePlugin(settings: VisualSetup) extends Plugin("Triple Table")
{
    val tableWrapper = new Div()

    val tableWrapperElement = tableWrapper.domElement

    def createSubComponents = List(tableWrapper)

    def updateGraph(graph: Option[Graph]) {
        // Remove the old table.
        while (tableWrapperElement.hasChildNodes) {
            tableWrapperElement.removeChild(tableWrapperElement.firstChild)
        }

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
                    createVertexAnchor(origin).render(originCell)
                }

                // The edge cell.
                val edgeCell = addCell(row)
                new Text(edgeUri).render(edgeCell)

                // The destinations cell.
                val destinationsCell = addCell(row)
                val destinationListItems = edges.map { (edge: Edge) =>
                    val vertexElement = edge.destination match {
                        case iv: IdentifiedVertex => createVertexAnchor(iv)
                        case lv: LiteralVertex => new Text(lv.value.toString)
                        case v => new Text(v.toString)
                    }
                    new ListItem(List(vertexElement))
                }
                new UnorderedList(destinationListItems).render(destinationsCell)
            }
            originCell.setAttribute("rowspan", originRowCount.toString)
       }
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

    private def createVertexAnchor(vertex: IdentifiedVertex): Anchor = {
        val anchor = new Anchor(List(new Text(vertex.uri)))
        anchor.mouseClicked += { e =>
            vertexClickedHandler(vertex)
            false
        }
        anchor
    }

    private def vertexClickedHandler(vertex: Vertex) {
        // TODO
    }

    private def addRow(table: Element): Element = addElement(table, "tr")

    private def addCell(row: Element, isHeader: Boolean = false) = addElement(row, if (isHeader) "th" else "td")

    private def addElement(parent: Element, name: String): Element = {
        val element = document.createElement[Element](name)
        parent.appendChild(element)
        element
    }
}
