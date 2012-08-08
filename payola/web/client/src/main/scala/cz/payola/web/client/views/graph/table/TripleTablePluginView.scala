package cz.payola.web.client.views.graph.table

import scala.collection._
import s2js.adapters.html
import cz.payola.common.rdf._
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.elements.lists._

/**
 * A plugin that displays all edges in the graph as a table. The edges are firstly grouped by the edge origins,
 * secondly by the edge types.
 */
class TripleTablePluginView extends TablePluginView("Triple Table")
{
    def fillTable(graph: Option[Graph], tableHead: html.Element, tableBody: html.Element) {
        // Create the headers.
        val headerRow = addRow(tableHead)
        List("Subject", "Property", "Value").foreach { title =>
            val cell = addCell(headerRow, isHeader = true)
            cell.innerHTML = title
        }

        // Fill the table with cells.
        groupEdges(graph).foreach { edgesByOrigin =>
            var originCell: html.Element = null
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

    private def groupEdges(graph: Option[Graph]): Map[String, Map[String, Seq[Edge]]] = {
        val edgesByOrigin = new mutable.HashMap[String, mutable.HashMap[String, mutable.ListBuffer[Edge]]]
        graph.foreach {
            _.edges.foreach {
                edge =>
                    val edgesByEdgeType = edgesByOrigin.getOrElseUpdate(edge.origin.uri,
                        new mutable.HashMap[String, mutable.ListBuffer[Edge]])
                    edgesByEdgeType.getOrElseUpdate(edge.uri, new mutable.ListBuffer[Edge]) += edge
            }
        }

        edgesByOrigin
    }
}
