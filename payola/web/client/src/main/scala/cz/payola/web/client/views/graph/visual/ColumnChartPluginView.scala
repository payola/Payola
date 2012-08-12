package cz.payola.web.client.views.graph.visual

import cz.payola.web.client.views.graph.PluginView
import cz.payola.web.client.views.elements._
import cz.payola.common.rdf._
import s2js.compiler.javascript
import s2js.adapters.browser._

class ColumnChartPluginView extends PluginView("Column Chart")
{
    private val chartWrapper = new Div(Nil)
    chartWrapper.setAttribute("id", "chart-wrapper")

    private val pluginWrapper = new Div(List(chartWrapper), "column-chart-wrapper")

    // Used in drawChart()'s @javascript annotation. Beware before renaming
    private val chartWrapperElement = chartWrapper.htmlElement

    /**Adds a bars to the chart. Is a list of list with two values - title and value.
     *
     * @param arr Bars to be displayed.
     */
    @javascript(
        """
           var rawData = [];
           var titles = [];

           var counter = 0;
           arr.foreach(function(x){
                var title = x[0];
                var value = x[1];
                rawData.push([ counter, value ]);
                titles.push([ counter, title ]);
                ++counter;
           });

           var data = [{
                label: legendTitle,
                data: rawData,
                bars: {
                    show: true,
                    barWidth: 0.5,
                    align: "center"
                }
           }];
          $.plot($("#chart-wrapper"), data, { label: legendTitle, xaxis: { ticks: titles }, hoverable: true });
        """)
    private def createDataTable(arr: List[List[Any]], legendTitle: String) {
    }

    def createPhonyGraph: Graph = {
        val initialVertex = new IdentifiedVertex("core-of-the-sun")

        val bar1 = new IdentifiedVertex("bar1")
        val bar2 = new IdentifiedVertex("bar2")
        val bar3 = new IdentifiedVertex("bar3")

        val name1 = new LiteralVertex("name1")
        val name2 = new LiteralVertex("name2")
        val name3 = new LiteralVertex("name3")

        val value1 = new LiteralVertex(333)
        val value2 = new LiteralVertex(666)
        val value3 = new LiteralVertex(999)

        val e1 = new Edge(bar1, initialVertex, Edge.rdfTypeEdge)
        val e2 = new Edge(bar2, initialVertex, Edge.rdfTypeEdge)
        val e3 = new Edge(bar3, initialVertex, Edge.rdfTypeEdge)

        val e4 = new Edge(bar1, name1, "name")
        val e5 = new Edge(bar1, value1, "value")

        val e6 = new Edge(bar2, name2, "name")
        val e7 = new Edge(bar2, value2, "value")

        val e8 = new Edge(bar3, name3, "name")
        val e9 = new Edge(bar3, value3, "value")


        new Graph(List(initialVertex, bar1, bar2, bar2, name1, name2, name3, value1, value2, value3),
            List(e1, e2, e3, e4, e5, e6, e7, e8, e9)
        )
    }

    def createSubViews = {
        // Update width and height of the pluginWrapper
        val width = window.innerWidth - 220
        val styleString = "width: " + width + "px;"
        pluginWrapper.setAttribute("style", styleString)
        List(pluginWrapper)
    }

    def findInitialVertexForColumnChart(g: Graph): Option[IdentifiedVertex] = {
        val identifiedVertices = g.vertices.filter(_.isInstanceOf[IdentifiedVertex]).asInstanceOf[Seq[IdentifiedVertex]]
        identifiedVertices.find { v =>
            val typeEdges = g.getIncomingEdges(v.uri).filter(_.uri == Edge.rdfTypeEdge)
            typeEdges.size > 0 && typeEdges.forall { e =>
                e.origin match {
                    case identified: IdentifiedVertex => validateLiteralVerticesOnEdges(
                        g.getOutgoingEdges(identified.uri))
                    case _ => false
                }
            }
        }
    }

    private def setGraphContentWithInitialVertex(g: Graph, initialVertex: IdentifiedVertex) {
        // Get those vertices representing bars in the chart
        val bars = g.getIncomingEdges(initialVertex.uri).filter(_.uri == Edge.rdfTypeEdge)
            .map(_.origin)
        var legendTitle = ""

        // Our assumption here is that the graph-as-chart has been validated
        // before being passed here, so no additional checks will be performed
        val values = bars.map { v =>
           // Each vertex should have exactly two edges, one with the title and one
           // with the value
           // Both edges point to literal vertices (due to prior assumed validations)
            val outgoingEdges = g.getOutgoingEdges(v.uri)
            val literals = outgoingEdges.filter(_.destination.isInstanceOf[LiteralVertex]).map(_.destination.asInstanceOf[LiteralVertex])

            val title = literals.find(litVertex => variableIsString(litVertex.value)).get.value
            val valueVertex = literals.find(litVertex => variableIsNumber(litVertex.value)).get
            val value = valueVertex.value
            legendTitle = outgoingEdges.find(_.destination == valueVertex).get.uri
            List(title, value).toList
        }.toList

        setupDivSizeForColumns(values)
        createDataTable(values, legendTitle)
    }

    private def setTextualContent(message: String, details: String) {
        val messageDiv = new
                Div(List(new Text(message)), "column-chart-textual-content column-chart-textual-content-large")
        val descriptionDiv = new
                Div(List(new Text(details)), "column-chart-textual-content column-chart-textual-content-small")
        messageDiv.render(chartWrapperElement)
        descriptionDiv.render(chartWrapperElement)
    }

    private def setupDivSizeForColumns(values: List[_]){
        val multiplier = 80
        val width = values.length * multiplier
        val height = 400

        val styleString = "width: " + width + "px; height: " + height + "px;"
        chartWrapper.setAttribute("style", styleString)
    }

    override def updateGraph(graph: Option[Graph]) {
        if (graph != currentGraph) {
            // Clear the wrapper
            chartWrapper.removeAllChildNodes()

            if (graph.isEmpty) {
                // Add a 'No graph' title
                setTextualContent("No graph available...", "Load a graph to begin.")
            } else {
                val initialVertex = findInitialVertexForColumnChart(graph.get)
                if (initialVertex.isDefined) {
                    setGraphContentWithInitialVertex(graph.get, initialVertex.get)
                } else {
                    setTextualContent("This graph can't be displayed as a column chart...",
                        "Choose a different visualization plugin.")
                }
            }
        }
        super.updateGraph(graph)
    }

    private def validateLiteralVerticesOnEdges(edges: Seq[Edge]): Boolean = {
        if (edges.size == 3 && edges.forall(e => e.destination.isInstanceOf[LiteralVertex] || e.uri == Edge.rdfTypeEdge)) {
            // We need exactly two vertices, one with the bar title, one with the bar height (value)
            edges.find(e => e.destination.isInstanceOf[LiteralVertex] && variableIsString(e.destination.asInstanceOf[LiteralVertex].value)).isDefined &&
                edges.find(e => e.destination.isInstanceOf[LiteralVertex] && variableIsNumber(e.destination.asInstanceOf[LiteralVertex].value)).isDefined
        } else {
            false
        }
    }

    @javascript("return (typeof num == 'number');")
    private def variableIsNumber(num: Any): Boolean = {
        false
    }

    @javascript("return (typeof str == 'string');")
    private def variableIsString(str: Any): Boolean = {
        false
    }
}
