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
 *
 * The graph provided for this plugin should match the ASK query defined in askIfSupportedQuery
 */
class TimelinePluginView(prefixApplier: Option[PrefixApplier]) extends PluginView[rdf.Graph]("Timeline", prefixApplier)
{
    private val timelineWrapper = new Div
    timelineWrapper.id = "timeline-wrapper"

    private val wrapper = new Div(List(timelineWrapper))

    private var legendTitle = ""
    private var legendDescription = ""
    private var dataSeries = List[List[Any]]()

    /**
     * @param element Wrapper for timeline
     * @param arr Array of timeline events, each item is a list with the title, date and description
     * @param legendTitle Title on the first non-event timeline element
     * @param legendDescription Description for the first non-event timeline element
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

    /**
     * @param g Graph
     * @param events Event nodes
     * @return If a vertex which has an incoming edge from all events exists and has a label, it is returned
     */
    def findLegendVertex(g: Graph, events: Seq[IdentifiedVertex]): Option[IdentifiedVertex] = {
        val nonEventIdVertices = g.vertices
            .filter(_.isInstanceOf[IdentifiedVertex])
            .filterNot(events.contains(_))
            .asInstanceOf[Seq[IdentifiedVertex]]
        nonEventIdVertices.filter { v =>
            // The initial vertex should have a label and all events should have an edge pointing to the root
            g.getOutgoingEdges(v.uri).find(e => Edge.rdfLabelEdges.contains(e.uri)).size > 0 &&
                events.forall { ev =>
                    g.edges.exists(e => e.origin == ev && e.destination == v)
            }
        }.reduceOption((best, current) =>
            if (g.getOutgoingEdges(current.uri).size > g.getOutgoingEdges(best.uri).size) current
            else best
        )
    }

    /**
     * Finds vertices which are event nodes, that it having a label and title
     * @param g Graph
     * @return Vertices which are event nodes
     */
    private def findEventNodes(g: Graph): Seq[IdentifiedVertex] = {
        val identifiedVertices = g.vertices.filter(_.isInstanceOf[IdentifiedVertex]).asInstanceOf[Seq[IdentifiedVertex]]
        identifiedVertices.filter { v =>
            validateItemHasDateAndTitle(
                g.getOutgoingEdges(v.uri)
            )
        }
    }

    private def fillTimelineDataSeries(g: Graph, events: Seq[IdentifiedVertex]) {

        val edgeDestinationValue = (e: Option[Edge]) => {
            e match {
                case Some(edge) => edge.destination.asInstanceOf[LiteralVertex].value.toString
                case None => ""
            }
        }

        val values = events.map { v =>
            // Each vertex should have an edge with the title and one with the date
            // Both edges point to literal vertices (due to prior assumed validations)
            // Apart from these two, other outgoing edges are added to the visualization
            val outgoingEdges = g.getOutgoingEdges(v.uri)
            val titleEdge = outgoingEdges.find(e => Edge.rdfLabelEdges.contains(e.uri))
            val title: String = edgeDestinationValue(titleEdge)
            val dateEdge = outgoingEdges.find(e => Edge.rdfDateTimeEdges.contains(e.uri))
            val date: String = edgeDestinationValue(dateEdge)

            val otherProps = outgoingEdges
                .filter(e => e != dateEdge.get && e != titleEdge.get)
            List(title, date, htmlListFromEdges(otherProps)).toList
        }.toList

        legendTitle = ""
        legendDescription = ""
        val legend = findLegendVertex(g, events)
        if (legend.isDefined) {
            val outgoingEdges = g.getOutgoingEdges(legend.get.uri)
            val titleEdge = outgoingEdges.find(e => Edge.rdfLabelEdges.contains(e.uri))
            legendTitle = edgeDestinationValue(titleEdge)
            val descEdge = outgoingEdges.find(e => Edge.rdfDescriptionEdges.contains(e.uri))
            // Use description node or list outgoing properties other than title
            legendDescription = descEdge match {
                case Some(edge) => edge.destination.asInstanceOf[LiteralVertex].value.toString
                case None =>
                    val otherProps = outgoingEdges
                        .filter(_ != titleEdge.get)
                    htmlListFromEdges(otherProps)
            }
        }


        dataSeries = values
    }

    private def htmlListFromEdges(edges: Seq[Edge]): String = {
        val stringified = edges
            .map(e => (e, e.destination))
            .map{
            case (e: Edge, v: Vertex) =>
                "<dt>%s</dt><dd>%s</dd>"
                    .format(prefixApplier.get.applyPrefix(e.toString), prefixApplier.get.applyPrefix(v.toString))
        }
        "<dl class=\"dl-horizontal\">" + stringified.mkString("\n") + "</dl>"
    }

    override def updateGraph(graph: Option[Graph], contractLiterals: Boolean = true) {
        if (graph != currentGraph) {
            // Clear the wrapper
            timelineWrapper.removeAllChildNodes()

            if (graph.isEmpty) {
                renderMessage(timelineWrapper.htmlElement, "The graph is empty...")
            } else {
                val events = findEventNodes(graph.get)
                if (!events.isEmpty) {
                    fillTimelineDataSeries(graph.get, events)
                } else {
                    renderMessage(
                        timelineWrapper.htmlElement,
                        "This graph can't be displayed as a timeline",
                        "Choose a different visualization plugin or make sure it contains multiple vertices with a date and title."
                    )
                }
            }
        }
        super.updateGraph(graph, contractLiterals = true)
    }

    /**
     * Validate edge has a label and date (thus being an event node)
     * @param edges list of outgoing edges
     * @return true if item contains at least one label and at least one date property
     */
    private def validateItemHasDateAndTitle(edges: Seq[Edge]): Boolean = {
        edges.size > 1 &&
            edges.exists(e => Edge.rdfDateTimeEdges.contains(e.uri)) &&
            edges.exists(e => Edge.rdfLabelEdges.contains(e.uri))
    }

    private def askIfSupportedQuery(): String = {
        """PREFIX dc: <http://purl.org/dc/elements/1.1/>
          |PREFIX dcterms: <http://purl.org/dc/terms/>
          |PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>
          |ASK {
          | {
          |  ?s ?labelprop ?l .
          |  ?s ?dateprop ?d .
          |  FILTER(
          |    (?labelprop = dc:title || ?labelprop = dcterms:title || ?labelprop = rdfs:label)
          |    &&
          |    (?dateprop = dc:date || ?dateprop = dcterms:date)
          |  )
          |  OPTIONAL {
          |    ?legend ?labelprop ?ll .
          |    ?s ?anyprop ?legend
          |  }
          | }
          |}"""
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
