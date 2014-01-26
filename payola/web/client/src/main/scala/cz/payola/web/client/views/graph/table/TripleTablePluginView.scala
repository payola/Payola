package cz.payola.web.client.views.graph.table

import scala.collection._
import s2js.adapters.html
import cz.payola.common.rdf._
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.elements.lists._
import cz.payola.web.client.views.bootstrap.Icon
import cz.payola.web.client.models.PrefixApplier
import cz.payola.web.shared.transformators.TripleTableTransformator
import cz.payola.web.client.views.bootstrap.modals.FatalErrorModal

/**
 * A plugin that displays all edges in the graph as a table. The edges are firstly grouped by the edge origins,
 * secondly by the edge types.
 */
class TripleTablePluginView(prefixApplier: Option[PrefixApplier]) extends TablePluginView("Triple Table", prefixApplier)
{

    private val countOfProperties = 5
    private val showMoreLabel = "Show more"

    /**
     * Returns edges, that will be shown on the page (according to tablPageNumber); number of pages requred to show
     * the whole graph; count of triples (records) for the current page; count of all triples (records)
     */
    private def getEdgesForThisPage(tablePageNumber: Int, groupedEdges: Map[String, Map[String, Seq[Edge]]]):
        (Map[String, Map[String, Seq[Edge]]], Int, Int, Int) = {

        var linesOnPage = 0
        var currentPageNumber = 0
        var currentPageTriplesCount = 0
        var otherTriplesCount = 0


        ((groupedEdges.filter{ gE =>
            linesOnPage += gE._2.size

            if (linesOnPage > allowedLinesOnPage) {
                currentPageNumber += 1
                linesOnPage = gE._2.size
            }

            if(currentPageNumber != tablePageNumber) {
                gE._2.foreach(otherTriplesCount += _._2.size)
                false
            } else if (linesOnPage <= allowedLinesOnPage){
                gE._2.foreach(currentPageTriplesCount += _._2.size)
                true
            } else {
                gE._2.foreach(otherTriplesCount += _._2.size)
                false
            }
        }, currentPageNumber + 1, currentPageTriplesCount, currentPageTriplesCount + otherTriplesCount))
    }

    def fillTable(graph: Option[Graph], tableHead: html.Element, tableBody: html.Element, tablePageNumber: Int): (Int, Int, Int) = {
        val groupedEdges = groupEdges(graph)

        val tableListing = getEdgesForThisPage(tablePageNumber, groupedEdges)
        val edgesForThisPage = tableListing._1

        // Create the headers.
        val headerRow = addRow(tableHead)
        List("Subject", "Property", "Value").foreach { title =>
            val cell = addCell(headerRow, isHeader = true)
            cell.innerHTML = title
        }

        // Fill the table with cells.
        edgesForThisPage.foreach { edgesByOrigin =>
            var originCell: html.Element = null
            var originRowCount = 0


            edgesByOrigin._2.take(countOfProperties).foreach{ edgesByEdgeType =>
                val row = addRow(tableBody)

                originRowCount += 1
                if (originCell == null) {
                    originCell = addCell(row)
                    createVertexView(edgesByEdgeType._2.head.origin).render(originCell)
                }

                createVertexDetailRow(edgesByEdgeType._1, edgesByEdgeType._2, row)
            }

            if(edgesByOrigin._2.size > countOfProperties) {
                //add a row and a "show more" button in the property column
                val row = addRow(tableBody)
                val cell = addCell(row)
                val showMoreLink = new Anchor(List(new Icon(Icon.plus), new Text(showMoreLabel)))

                showMoreLink.render(cell)
                cell.setAttribute("colspan", "2")
                originRowCount += 1

                showMoreLink.mouseClicked += { e =>

                    val hiddenEdgesCount = edgesByOrigin._2.size - countOfProperties;

                    edgesByOrigin._2.takeRight(hiddenEdgesCount).foreach{ edgesByEdgeType =>
                        val appendedRow = insertRow(tableBody, row)

                        createVertexDetailRow(edgesByEdgeType._1, edgesByEdgeType._2, appendedRow)
                    }

                    originCell.setAttribute("rowspan", edgesByOrigin._2.size.toString)
                    tableBody.removeChild(row)

                    false
                }
            }

            originCell.setAttribute("rowspan", originRowCount.toString)
        }

        if(graph.isDefined && graph.get.resultsCount.isDefined) {
            ((tableListing._3, math.ceil(graph.get.resultsCount.get / allowedLinesOnPage).toInt,
                graph.get.resultsCount.get.toInt))
        } else { ((tableListing._3, tableListing._2, tableListing._4)) }
    }

    private def createVertexDetailRow(edgeUri: String, edges: Seq[Edge], row: html.Element) {

        // The edge cell.
        val edgeCell = addCell(row)
        val prefixedEdgeUri = prefixApplier.map(_.applyPrefix(edgeUri)).getOrElse(edgeUri)
        new Text(prefixedEdgeUri).render(edgeCell)

        // The destinations cell.
        val destinationsCell = addCell(row)
        val destinationListItems = edges.map { edge =>
            val vertexElement = edge.destination match {
                case iv: IdentifiedVertex => createVertexView(iv)
                case lv: LiteralVertex => new Text(lv.value.toString)
                case v => new Text(v.toString)
            }
            new ListItem(List(vertexElement))
        }
        new UnorderedList(destinationListItems, "unstyled").render(destinationsCell)
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

    override def isAvailable(availableTransformators: List[String], evaluationId: String,
        success: () => Unit, fail: () => Unit) {

        TripleTableTransformator.getSmapleGraph(evaluationId) { sample =>
        //TripleTableTransformator.getClass.getName does not work after s2js
            if(sample.isEmpty && availableTransformators.exists(_.contains("TripleTableTransformator"))) {
                success()
            } else {
                fail()
            }
        }
        { error =>
            fail()
            val modal = new FatalErrorModal(error.toString())
            modal.render()
        }
    }

    override def loadDefaultCachedGraph(evaluationId: String, updateGraph: Option[Graph] => Unit) {
        TripleTableTransformator.getCachedPage(evaluationId, currentPage, allowedLinesOnPage)
        { pageOfGraph =>
            updateGraph(pageOfGraph)
        }
        { error =>
            val modal = new FatalErrorModal(error.toString())
            modal.render()
        }
    }
}
