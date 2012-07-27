package cz.payola.web.client.views.graph.visual

import cz.payola.web.client.views.graph.visual.settings.components.visualsetup.VisualSetup
import cz.payola.web.client.views.graph.PluginView
import cz.payola.web.client.views.elements._
import cz.payola.common.rdf._
import s2js.compiler.javascript

class BarChartPluginView(settings: VisualSetup) extends PluginView("Bar Chart")
{

    private val chartWrapper = new Div()

    // Used in drawChart()'s @javascript annotation. Beware before renaming
    private val chartWrapperElement = chartWrapper.domElement

    // Used from @javascript annotations. Do *NOT* remove.
    private var googleDataTable = null

    /** Adds a bars to the chart. Is a list of list with two values - title and value.
     *
     * @param arr Bars to be displayed.
     */
    @javascript("self.googleDataTable.addRows(arr);")
    private def addRowsToGoogleDataTable(arr: List[List[Any]]){

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

        val e1 = new Edge(initialVertex, bar1, Edge.rdfTypeEdge)
        val e2 = new Edge(initialVertex, bar2, Edge.rdfTypeEdge)
        val e3 = new Edge(initialVertex, bar3, Edge.rdfTypeEdge)

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

    def createSubViews = List(chartWrapper)

    @javascript("var chart = new google.visualization.BarChart(self.chartWrapperElement); chart.draw(self.googleDataTable, {});")
    private def drawChart(){

    }

    def findInitialVertexForBarChart(g: Graph): Option[IdentifiedVertex] = {
        val identifiedVertices = g.vertices.filter(_.isInstanceOf[IdentifiedVertex]).asInstanceOf[Seq[IdentifiedVertex]]
        identifiedVertices.find { v =>
            val typeEdges = g.getOutgoingEdges(v.uri).filter(_.uri == Edge.rdfTypeEdge)
            typeEdges.size > 0 && typeEdges.forall { e =>
                e.destination match {
                    case identified: IdentifiedVertex => validateLiteralVerticesOnEdges(g.getOutgoingEdges(identified.uri))
                    case _ => false
                }
            }
        }
    }


    @javascript("self.googleDataTable = new google.visualization.DataTable(); self.googleDataTable.addColumn('string', 'Titles'); self.googleDataTable.addColumn('number', 'Value');")
    private def prepareGoogleDataTable(){

    }

    private def setGraphContentWithInitialVertex(g: Graph, v: IdentifiedVertex) {
        // Get those vertices representing bars in the chart
        val bars = g.getOutgoingEdges(v.uri).filter(_.uri == Edge.rdfTypeEdge).map(_.destination.asInstanceOf[IdentifiedVertex])

        prepareGoogleDataTable()

        // Our assumption here is that the graph-as-chart has been validated
        // before being passed here, so no additional checks will be performed
        val values = bars.map { v =>
            // Each vertex should have exactly two edges, one with the title and one
            // with the value
            // Both edges point to literal vertices (due to prior assumed validations)
            val literals = g.getOutgoingEdges(v.uri).map(_.destination.asInstanceOf[LiteralVertex])

            val title = literals.find(litVertex => variableIsString(litVertex.value)).get.value
            val value = literals.find(litVertex => variableIsNumber(litVertex.value)).get.value
            List(title, value).toList
        }.toList
        addRowsToGoogleDataTable(values)

        // Draw the chart
        drawChart()
    }

    private def setTextualContent(message: String, details: String) {
        val messageDiv = new Div(List(new Text(message)), "bar-chart-textual-content bar-chart-textual-content-large")
        val descriptionDiv = new Div(List(new Text(details)), "bar-chart-textual-content bar-chart-textual-content-small")
        messageDiv.render(chartWrapperElement)
        descriptionDiv.render(chartWrapperElement)
    }

    override def updateGraph(g: Option[Graph]) {
        val graph = Some(createPhonyGraph)
        if (graph != currentGraph) {
            // Clear the wrapper
            chartWrapper.removeAllChildNodes()

            if (graph.isEmpty) {
                // Add a 'No graph' title
                setTextualContent("No graph available...", "Load a graph to begin.")
            }else{
                val initialVertex = findInitialVertexForBarChart(graph.get)
                if (initialVertex.isDefined){
                    setGraphContentWithInitialVertex(graph.get, initialVertex.get)
                }else{
                    setTextualContent("This graph can't be displayed as a bar chart...", "Choose a different visualisation plugin.")
                }
            }

        }
        super.updateGraph(graph)
    }

    private def validateLiteralVerticesOnEdges(edges: Seq[Edge]): Boolean = {
        if (edges.size == 2 && edges.forall(_.destination.isInstanceOf[LiteralVertex])) {
            // We need exactly two vertices, one with the bar title, one with the bar height (value)
            edges.find(e => variableIsString(e.destination.asInstanceOf[LiteralVertex].value)).isDefined &&
                edges.find(e => variableIsNumber(e.destination.asInstanceOf[LiteralVertex].value)).isDefined
        }else{
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
