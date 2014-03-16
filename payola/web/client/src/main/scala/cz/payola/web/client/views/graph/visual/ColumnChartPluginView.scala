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

class ColumnChartPluginView(prefixApplier: Option[PrefixApplier]) extends PluginView[rdf.Graph]("Column Chart", prefixApplier)
{
    private val chartWrapper = new Div
    chartWrapper.id = "chart-wrapper"

    private val wrapper = new Div(List(chartWrapper))

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
                if (title.length > 50){
                    title = $.trim(title.substring(0, 50)) + "...";
                }
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

          var options = {
                label: legendTitle,
                xaxis: {
                    ticks: titles
                },
                grid: {
                    hoverable: true,
                    clickable: true
                }
          };
          $.plot($("#chart-wrapper"), data, options);
        """)
    private def createDataTable(arr: List[List[Any]], legendTitle: String) {

    }

    override def render(parent: html.Element) {
        super.render(parent)
        val width = window.innerWidth
        val height = window.innerHeight - wrapper.offset.y
        val sizeStyle = "width: %dpx; height: %dpx; overflow: auto;".format(width, height)
        wrapper.setAttribute("style", sizeStyle)
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
            List(e1, e2, e3, e4, e5, e6, e7, e8, e9),
            None
        )
    }

    def createSubViews = {
        List(wrapper)
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

            val title: String = literals.find(litVertex => variableIsString(litVertex.value)).get.value.toString
            val valueVertex = literals.find(litVertex => variableIsNumber(litVertex.value)).get
            val value = valueVertex.value
            legendTitle = outgoingEdges.find(_.destination == valueVertex).get.uri
            List(title, value).toList
        }.toList

        setupDivSizeForColumns(values)
        setupTooltipsWithTitles(values.map(_(0)))
        createDataTable(values, legendTitle)
    }

    @javascript(
        """
          var previousPoint = null;
              $("#chart-wrapper").bind("plothover", function (event, pos, item) {
                  if (item) {
                      if (previousPoint != item.dataIndex) {
                          previousPoint = item.dataIndex;

                          $("#tooltip").remove();
                          var x = item.pageX,
                              y = item.pageY;
                          $('<div id="tooltip">Title: ' + titles.internalJsArray[item.dataIndex]+ '<br/>Value: ' + item.datapoint[1] + '</div>').css( {
                                      position: 'absolute',
                                      display: 'none',
                                      top: y + 5,
                                      left: x + 5,
                                      border: '1px solid #fdd',
                                      padding: '2px',
                                      'padding-left': '5px',
                                      'padding-right': '5px',
                                      'background-color': '#fee',
                                      opacity: 0.80
                                  }).appendTo("body").fadeIn(200);
                      }
                  }
                  else {
                      $("#tooltip").remove();
                      previousPoint = null;
                  }
              });
        """)
    private def setupTooltipsWithTitles(titles: List[Any]){

    }

    private def setupDivSizeForColumns(values: List[_]){
        val multiplier = 80
        var width = values.length * multiplier
        if (width < 500) {
            width = 500
        }
        val height = window.innerHeight - chartWrapper.offset.y - 20 // The 20 is for potential horizontal scrollbar.

        val styleString = "width: %dpx; height: %dpx; margin: 0 20px;".format(width, height)
        chartWrapper.setAttribute("style", styleString)
    }

    override def updateGraph(graph: Option[Graph], conractLiterals: Boolean = true) {
        if (graph != currentGraph) {
            // Clear the wrapper
            chartWrapper.removeAllChildNodes()

            if (graph.isEmpty) {
                renderMessage(chartWrapper.htmlElement, "The graph is empty...")
            } else {
                val initialVertex = findInitialVertexForColumnChart(graph.get)
                if (initialVertex.isDefined) {
                    setGraphContentWithInitialVertex(graph.get, initialVertex.get)
                } else {
                    renderMessage(
                        chartWrapper.htmlElement,
                        "This graph can't be displayed as a column chart...",
                        "Choose a different visualization plugin."
                    )
                }
            }
        }
        super.updateGraph(graph, true)
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
            val modal = new FatalErrorModal(error.toString())
            modal.render()
        }
    }

    override def loadDefaultCachedGraph(evaluationId: String, updateGraph: Option[rdf.Graph] => Unit) {
        IdentityTransformator.transform(evaluationId)(updateGraph(_))
        { error =>
            val modal = new FatalErrorModal(error.toString())
            modal.render()
        }
    }
}
