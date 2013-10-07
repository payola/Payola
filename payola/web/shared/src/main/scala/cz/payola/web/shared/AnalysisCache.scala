package cz.payola.web.shared

import s2js.compiler._
import cz.payola.common.rdf._
import cz.payola.domain.entities.User
import scala.Some
import scala.collection.immutable
import scala.collection.mutable
import cz.payola.common.PayolaException

@remote
@secured object AnalysisCache
{
    private val tripleTableDefaultPage = 0
    private val tripleTableDefaultRecordsOnPage = 50


    @async def getCompleteAnalysisResult(analysisId: String, owner: Option[User] = None)
        (successCallback: (Option[Graph] => Unit))(failCallback: (Throwable => Unit)) {

        var result: Option[Graph] = None
        if(owner.isDefined) {
            result = Some(
                Payola.model.analysisResultStorageModel.getGraph(owner.get, analysisId))
        }
        successCallback(result)
    }

    @async def paginate(analysisId: String, page: Option[Int], allowedLinesOnPage: Option[Int], owner: Option[User] = None)
        (successCallback: (Graph => Unit))(failCallback: (Throwable => Unit)) {

        if(owner.isEmpty) {
            failCallback(new PayolaException("Can not get graph from cache for undefined user."))
        } else {
            val graph = Payola.model.analysisResultStorageModel.getGraph(owner.get, analysisId)

            //group edges by origin vertex
            val edgesByOrigin = new mutable.HashMap[String, mutable.HashMap[String, mutable.ListBuffer[Edge]]]
            graph.edges.foreach { edge =>
            //get or create grouping by origin vertex
                val edgesByEdgeType = edgesByOrigin.getOrElseUpdate(edge.origin.uri,
                    new mutable.HashMap[String, mutable.ListBuffer[Edge]])
                //get or create grouping by edge and add edge
                edgesByEdgeType.getOrElseUpdate(edge.uri, new mutable.ListBuffer[Edge]) += edge
            }

            var linesOnPage = 0
            var currentPageNumber = 0
            val maxLinesOnPage = allowedLinesOnPage.getOrElse(tripleTableDefaultRecordsOnPage)

            val requestedPageEdges = new mutable.ListBuffer[Edge]
            edgesByOrigin.foreach{ gE =>
                linesOnPage += gE._2.size

                if (linesOnPage > maxLinesOnPage) {
                    currentPageNumber += 1
                    linesOnPage = gE._2.size
                }

                if(currentPageNumber != page.getOrElse(tripleTableDefaultPage)) {
                    false
                } else if (linesOnPage < maxLinesOnPage){ //edge is on current page
                    gE._2.foreach{ groupedEdges =>
                        requestedPageEdges ++= groupedEdges._2
                    }
                } else {
                    false
                }
            }

            val numberOfPages = currentPageNumber + 1

            successCallback(new Graph(immutable.Seq[Vertex](), requestedPageEdges.toList, Some(numberOfPages)))
        }
    }
}
