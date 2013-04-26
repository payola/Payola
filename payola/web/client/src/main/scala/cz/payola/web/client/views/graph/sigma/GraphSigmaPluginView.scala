package cz.payola.web.client.views.graph.sigma

import cz.payola.common.rdf
import s2js.adapters.js
import s2js.compiler.javascript
import scala.collection.mutable.ListBuffer

class GraphSigmaPluginView extends SigmaPluginView("Sigma.js") {

    var popUp: Any = null //used only in the javascript of functions showVertexInfo and hideVertexInfo


    def setDrawingProperties() {
        sigmaInstance.get.drawingProperties(new DrawingProperties)
    }

    def setGraphProperties() {

    }

    def fillGraph(graphOpt: Option[rdf.Graph]) {

       graphOpt.foreach{ graph =>
            graph.vertices.foreach{ vertex =>
                vertex match{
                    case i: rdf.IdentifiedVertex =>
                        createVertexView(i, countEdges(i, graph.edges), getAttributes(i, graph.edges))
                }
            }

            graph.edges.foreach{ edge =>
                createEdgeView(edge)
            }
        }


        //sigmaInstance.get.activateFishEye()
        //sigmaInstance.get.bind("overnodes", showVertexInfo(_))
        //sigmaInstance.get.bind("outnodes", hideVertexInfo(_))

        sigmaInstance.get.bind("overnodes", hideVertices(_))
        sigmaInstance.get.bind("outnodes", showVertices(_))
    }


    //TODO javascript -> scala
    @javascript(
        "self.popUp && self.popUp.remove(); "+
        "var node;\n " +
        " self.sigmaInstance.get().iterNodes(function(n){ node = n; },[event.content[0]]);\n " +
        " var nodeContent = node['attr']['value']; \n" +
        " if(typeof nodeContent == 'undefined' || nodeContent == null || nodeContent.isEmpty()) return; \n" +

        " self.popUp = $( '<div class=\"node-info-popup\"></div>' ).append(\n" +
        " self.attributesToString(nodeContent)).attr(\n" +
        " 'id','node-info'+self.sigmaInstance.get().getID()).css({\n" +
        "'display': 'inline-block','border-radius': 3,'padding': 5,'background': '#fff','color': '#000'," +
        "'box-shadow': '0 0 4px #666','position': 'absolute','left': node.displayX,'top': node.displayY+15});\n" +
        "$('ul',self.popUp).css('margin','0 0 0 20px');$(self.sigmaPluginWrapper.htmlElement).append(self.popUp);\n\n")
    private def showVertexInfo(event: Unit) {}

    //TODO javascript -> scala
    @javascript("self.popUp && self.popUp.remove(); self.popUp = false;")
    private def hideVertexInfo(event: Unit) {}

    //TODO javascript -> scala
    @javascript(
        "var nodes = event.content;\n" +
            "    var neighbors = {};\n" +
            "    self.sigmaInstance.get().iterEdges(function(e){\n" +
            //"       e.color='red';" +
            "      if(nodes.indexOf(e.source.id)<0 && nodes.indexOf(e.target.id)<0){\n" +
            "        if(!e.attr['grey']){\n" +
            "          e.attr['true_color'] = e.color;\n" +
            "          e.color = 'grey';\n" +
            "          e.attr['grey'] = 1;\n" +
            "        }\n" +
            "      }else{\n" +
            "        e.color = e.attr['grey'] ? e.attr['true_color'] : e.color;\n" +
            "        e.attr['grey'] = 0;\n\n" +
            "        neighbors[e.source.id] = 1;\n" +
            "        neighbors[e.target.id] = 1;\n      }\n" +
            "    });\n" +
            "   self.sigmaInstance.get().iterNodes(function(n){\n" +
            //"       n.color='red'" +
            "      if(!neighbors[n.id]){\n" +
            "        if(!n.attr['grey']){\n" +
            "          n.attr['true_color'] = n.color;\n" +
            "          n.color = 'grey';\n" +
            "          n.attr['grey'] = 1;\n        }\n" +
            "      }else{\n" +
            "        n.color = n.attr['grey'] ? n.attr['true_color'] : n.color;\n" +
            "        n.attr['grey'] = 0;\n      }\n" +
            "    });\n" +
            "   self.sigmaInstance.get().draw(5,5,5);")
    private def hideVertices(event: Unit) {}

    //TODO javascript -> scala
    @javascript(
        "    self.sigmaInstance.get().iterEdges(function(e){\n" +
        "      e.color = e.attr['grey'] ? e.attr['true_color'] : e.color;\n" +
        "      e.attr['grey'] = 0;\n" +
        "    }).iterNodes(function(n){\n" +
        "      n.color = n.attr['grey'] ? n.attr['true_color'] : n.color;\n" +
        "      n.attr['grey'] = 0;\n" +
        "    }).draw(2,2,2);")
    private def showVertices(event: Unit) {}

    private def getAttributes(vertex: rdf.IdentifiedVertex, edges: Seq[rdf.Edge]): ListBuffer[(String, _)] = {

        val result: ListBuffer[(String, _)] = new ListBuffer[(String, _)]()
        var plesk = 1
        edges.foreach{ edge =>
            if(edge.origin == vertex) {
                edge.destination match {
                    case i: rdf.LiteralVertex =>
                        plesk = 2
                        result += ((edge.uri, i.value))
                }
            }
        }

        result
    }

    private def countEdges(vertex: rdf.IdentifiedVertex, edges: Seq[rdf.Edge]): Int = {
        edges.count(edge => edge.origin == vertex)
    }

    private def attributesToString(attr: ListBuffer[(String, Any)]): String = {
        //TODO je potreba tam nejak dat scroolbar, pro pripad, ze atributu je hodne
        var resultList = "<ul>"
        attr.foreach { record =>
            resultList += "<li>" + record._1 + " : " + record._2 + "</li>"
        }
        resultList += "</ul>"
        resultList
    }
}
