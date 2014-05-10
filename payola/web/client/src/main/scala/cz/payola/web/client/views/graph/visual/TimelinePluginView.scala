package cz.payola.web.client.views.graph.visual

import s2js.compiler.javascript
import s2js.adapters.browser._
import s2js.adapters.html
import cz.payola.web.client.views.graph.PluginView
import cz.payola.web.client.views.elements._
import cz.payola.common.rdf._
import cz.payola.web.client.models.PrefixApplier
import cz.payola.web.client.views.bootstrap.modals.FatalErrorModal
import cz.payola.web.shared.transformators.IdentityTransformator
import cz.payola.common.rdf
import s2js.adapters.html.Element

/**
 * The timeline visualization assumes a graph where one root vertex has one or more connected vertices
 * which each has a text label and date property. These are then plotted onto a Javascript timeline
 */
class TimelinePluginView(prefixApplier: Option[PrefixApplier]) extends PluginView[rdf.Graph]("Timeline", prefixApplier)
{
    private val timelineWrapper = new Div
    timelineWrapper.id = "timeline-wrapper"

    private val wrapper = new Div(List(timelineWrapper))

    private var legendTitle = ""
    private var legendDescription = ""
    private var dataSeries = List[List[Any]]()

    @javascript(
        """
          console.log(msg);
        """)
    private def log(msg: String) {

    }

    /**Adds a bars to the chart. Is a list of list with two values - title and value.
     *
     * @param arr Bars to be displayed.
     */
    @javascript(
        """
            var dates = [];
            arr.foreach(function(x){
                var title = x[0];
                var text = title;
                if (title.length > 50){
                    title = $.trim(title.substring(0, 50)) + "...";
                }
                var value = x[1];
                dates.push({
                    "startDate": value.replace(/-/g, ","),
                    "headline": title,
                    "text": x[2],
                });
            });

            var timeline_config = {
                width: '100%',
                height: 350,
                source: {
                    "timeline": {
                        "headline": legendTitle,
                        "type": "default",
                        "text": legendDescription,
                        "date": dates
                    }
                },
                embed_id: 'timeline-wrapper',
            };
            createStoryJS(timeline_config);
        """)
    private def createTimeline(element: Element, arr: List[List[Any]], legendTitle: String, legendDescription: String) {

    }

    override def render(parent: html.Element) {
        super.render(parent)
        createTimeline(timelineWrapper.blockHtmlElement, dataSeries, legendTitle, legendDescription)
    }

    def createSubViews = {
        List(wrapper)
    }

    def findInitialVertex(g: Graph): Option[IdentifiedVertex] = {
        val identifiedVertices = g.vertices.filter(_.isInstanceOf[IdentifiedVertex]).asInstanceOf[Seq[IdentifiedVertex]]
        identifiedVertices.filter { v =>
            val timelineVertices = g.getIncomingEdges(v.uri)
            // The initial vertex should have no outgoing edges (tree root)
            // Every timeline vertex should contain a date and title
            log("trying " + v.uri)
            g.getOutgoingEdges(v.uri).find(e => Edge.rdfLabelEdges.contains(e.uri)).size > 0 &&
                timelineVertices.size > 1 && timelineVertices.forall { e =>
                e.origin match {
                    case identified: IdentifiedVertex =>
                        validateItemHasDateAndTitle(
                            g.getOutgoingEdges(identified.uri)
                        )

                    case _ => false
                }
            }
        }.reduceOption((best, current) =>
            if (g.getIncomingEdges(current.uri).size > g.getIncomingEdges(best.uri).size) current
            else best
        )
    }

    private def setGraphContentWithInitialVertex(g: Graph, initialVertex: IdentifiedVertex) {
        // Get those vertices representing bars in the chart
        val bars = g.getIncomingEdges(initialVertex.uri)/*.filter(_.uri == Edge.rdfTypeEdge)*/
            .map(_.origin)
        val initialVertexOutgoing = g.getOutgoingEdges(initialVertex.uri)
        legendTitle = initialVertexOutgoing
            .find(e => Edge.rdfLabelEdges.contains(e.uri))
            .map(e => e.destination.asInstanceOf[LiteralVertex].value.toString).getOrElse(initialVertex.uri)
        legendDescription = htmlListFromEdges(initialVertexOutgoing)
        val values = bars.map { v =>
            // Each vertex should have an edge with the title and one with the date
            // Both edges point to literal vertices (due to prior assumed validations)
            // Apart from these two, other outgoing edges are added to the visualization
            val outgoingEdges = g.getOutgoingEdges(v.uri)
            val titleEdge = outgoingEdges.find(e => Edge.rdfLabelEdges.contains(e.uri))
            val title: String = titleEdge.get.destination.asInstanceOf[LiteralVertex].value.toString
            val dateEdge = outgoingEdges.find(e => Edge.rdfDateTimeEdges.contains(e.uri))
            val date: String = dateEdge.get.destination.asInstanceOf[LiteralVertex].value.toString

            val otherProps = outgoingEdges
                .filter(e => e != dateEdge.get && e != titleEdge.get)
            List(title, date, htmlListFromEdges(otherProps)).toList
        }.toList

        dataSeries = values
    }

    private def htmlListFromEdges(edges: Seq[Edge]): String = {
        val stringified = edges
            .map(e => (e, e.destination))
            .map{
            case (e: Edge, v: Vertex) => prefixApplier.get.applyPrefix(e.toString) + ": " + prefixApplier.get.applyPrefix(v.toString)
        }
        "<ul><li>" + stringified.mkString("</li>\n<li>") + "</li></ul>"
    }

    override def updateGraph(graph: Option[Graph], contractLiterals: Boolean = true) {
        if (graph != currentGraph) {
            // Clear the wrapper
            timelineWrapper.removeAllChildNodes()

            if (graph.isEmpty) {
                renderMessage(timelineWrapper.htmlElement, "The graph is empty...")
            } else {
                val initialVertex = findInitialVertex(graph.get)
                log(graph.get.getIncomingEdges(initialVertex.get).size.toString)
                if (initialVertex.isDefined) {
                    setGraphContentWithInitialVertex(graph.get, initialVertex.get)
                } else {
                    renderMessage(
                        timelineWrapper.htmlElement,
                        "This graph can't be displayed as a timeline",
                        "Choose a different visualization plugin."
                    )
                }
            }
        }
        super.updateGraph(graph, contractLiterals = true)
    }

    /**
     * Validate edge has a label and date
     * @param edges list of outgoing edges
     * @return true if item contains at least one label and at least one date property
     */
    private def validateItemHasDateAndTitle(edges: Seq[Edge]): Boolean = {
        edges.size > 1 &&
            edges.exists(e => Edge.rdfDateTimeEdges.contains(e.uri)) &&
            edges.exists(e => Edge.rdfLabelEdges.contains(e.uri))
    }

    override def isAvailable(availableTransformators: List[String], evaluationId: String,
        success: () => Unit, fail: () => Unit) {

        IdentityTransformator.getSampleGraph(evaluationId) { sample =>
            if(sample.isEmpty && availableTransformators.exists(_.contains("IdentityTransformator"))) {
                success()
            } else {
                fail()
            }
        }
        { error =>
            fail()
            val modal = new FatalErrorModal(error.toString)
            modal.render()
        }
    }

    override def loadDefaultCachedGraph(evaluationId: String, updateGraph: Option[rdf.Graph] => Unit) {
        IdentityTransformator.transform(evaluationId)(updateGraph(_))
        { error =>
            val modal = new FatalErrorModal(error.toString)
            modal.render()
        }
    }
}
