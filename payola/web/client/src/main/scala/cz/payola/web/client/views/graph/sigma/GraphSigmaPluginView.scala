package cz.payola.web.client.views.graph.sigma

import cz.payola.common.rdf
import rdf._
import s2js.compiler.javascript
import scala.collection.mutable.ListBuffer
import cz.payola.web.client.views.elements.Text
import cz.payola.web.client.views.graph.sigma.properties.DrawingProperties
import s2js.adapters.js.sigma.Node
import cz.payola.web.client.models.PrefixApplier

class GraphSigmaPluginView(prefixApplier: Option[PrefixApplier]) extends SigmaPluginView("Sigma.js", prefixApplier) {

    val popUp: Any = null //used only in the javascript of functions showVertexInfo and hideVertexInfo
    val popUpWidth = 300
    val popUpHeight = 300
    private var animationRunning = false

    def setDrawingProperties() {
        sigmaInstance.get.drawingProperties(new DrawingProperties)
    }

    def fillGraph(graphOpt: Option[rdf.Graph]) {

        graphOpt.foreach{ graph =>
           //var vertexNum = 1
            graph.vertices.foreach{ vertex =>
                vertex match{
                    case i: rdf.IdentifiedVertex =>
                        createVertexView(prefixApplier.map(_.applyPrefix(i.uri)).getOrElse(i.uri),
                            i, countEdges(i, graph.edges), getAttributes(i, graph.edges))
                        setRdfType(i.uri, getRdfTypeForVertexView(graph.edges, i.uri))
                        //vertexNum += 1
                }
            }

            graph.edges.foreach{ edge =>
                createEdgeView(edge)
            }
        }
        sigmaInstance.get.bind("overnodes", showVertexInfo(_))
        sigmaInstance.get.bind("outnodes", hideVertexInfo(_))

        sigmaInstance.get.bind("upgraph", hideFixedVertexInfo(_))

        sigmaInstance.get.bind("overnodes", hideVertices(_))
        sigmaInstance.get.bind("outnodes", showVertices(_))

        //sigmaInstance.get.activateFishEye()
        //sigmaInstance.get.startForceAtlas2();
    }

    /**
     * Gets rdf type to specify the type required for drawing and getting drawing configuration based on an ontology.
     * @param edges to search for an edge with Edge.rdfTypeEdge uri
     * @param vertexURI for which the type is being searched
     * @return the found type or an empty string
     */
    private def getRdfTypeForVertexView(edges: Seq[Edge], vertexURI: String): String = {

        edges.find { e => (e.origin.uri == vertexURI) && (e.uri == Edge.rdfTypeEdge) }.map(_.destination.toString).getOrElse("")

    }

    /*private def stopAnimation() {
        if (sigmaInstance.isDefined) {
            sigmaInstance.get.stopForceAtlas2()
            animationStartStopButton.subViews.foreach{ child =>
                child match {
                    case i: Text =>
                        i.text = "Start"
                        i.destroy()
                        i.render(animationStartStopButton.htmlElement)
                }
            }
        }
    }

    private def startAnimation() {
        if (sigmaInstance.isDefined){
            sigmaInstance.get.startForceAtlas2()
            animationStartStopButton.subViews.foreach{ child =>
                child match {
                    case i: Text =>
                        i.text = "Stop"
                        i.destroy()
                        i.render(animationStartStopButton.htmlElement)
                }
            }
        }
    }

    animationStartStopButton.mouseClicked += {
        e =>
            if (animationRunning) {
                animationRunning = false
                stopAnimation()
            } else {
                animationRunning = true
                startAnimation()
            }

            false
    }*/



    //TODO javascript -> scala
    @javascript(
        "self.popUp && self.popUp.remove(); "+
        " var node;\n "+
        " self.sigmaInstance.get().iterNodes(function(n){ node = n; },[event.content[0]]);\n "+
        " var nodeContent = node['attr']['value']; \n" +

        " if(typeof nodeContent == 'undefined' || nodeContent == null || nodeContent.isEmpty())\n" +
        "   return; \n" +

        " var popUpX = self.getPopUpX(node.displayX);\n" +
        " var popUpY = self.getPopUpY(node.displayY);\n" +

        " self.popUp = $( '<div class=\"node-info-popup\" onclick=" +
            "\"cz.payola.web.client.views.graph.sigma.GraphSigmaPluginView.prototype.showFixedVertexInfo(this);\"" +
            "></div>').append(\n"+
        " self.attributesToString(nodeContent)).attr(\n"+
        " 'class', 'resizable vertex-info').attr(\n" +
        " 'id', 'node-info'+self.sigmaInstance.get().getID()).css({\n" +
        "'left': popUpX,'top': popUpY, 'width': self.popUpWidth+'px', 'height': self.popUpHeight+'px',});\n" +
        "$('ul',self.popUp).css('margin','0 0 0 20px');$(self.sigmaPluginWrapper.htmlElement).append(self.popUp);\n\n")
    private def showVertexInfo(event: Unit) {}


    @javascript("if(element.className.indexOf(\" visible\") == -1) {\n" +
        "element.className += \" visible\"; }\n")
    private def showFixedVertexInfo(element: Unit) {}

    private def getPopUpX(nodeLocationX: Int): Int = {
        if (nodeLocationX + popUpWidth > sigmaPluginWrapper.htmlElement.clientWidth) {
            nodeLocationX - popUpWidth
        } else {
            nodeLocationX
        }
    }

    private def getPopUpY(nodeLocationY: Int): Int = {
        if (nodeLocationY + popUpHeight > sigmaPluginWrapper.htmlElement.clientHeight) {
            nodeLocationY - popUpHeight
        } else {
            nodeLocationY + 15
        }
    }

    //TODO javascript -> scala
    @javascript("if(self.popUp) self.popUp.attr('class', self.popUp.attr('class').replace(\" visible\", \"\"));\n"+
        "  self.hideVertexInfo(event);\n")
    private def hideFixedVertexInfo(event: Unit) {}

    //TODO javascript -> scala
    @javascript("if(self.popUp && self.popUp.attr('class').indexOf(\" visible\") == -1) { self.popUp.remove(); self.popUp = false; }\n")
    private def hideVertexInfo(event: Unit) {}

    //TODO javascript -> scala
    @javascript(
        "var nodes = event.content;\n" +
            "    var neighbors = {};\n" +
            "    self.sigmaInstance.get().iterEdges(function(e){\n" +
            "      if(nodes.indexOf(e.source)<0 && nodes.indexOf(e.target)<0){\n" +
            "        if(!e.attr['grey']){\n" +
            "          e.attr['true_color'] = e.color;\n" +
            "          e.color = 'grey';\n" +
            "          e.attr['grey'] = 1;\n" +
            "        }\n" +
            "      }else{\n" +
            "        e.color = e.attr['grey'] ? e.attr['true_color'] : e.color;\n" +
            "        e.attr['grey'] = 0;\n\n" +
            "        neighbors[e.source] = 1;\n" +
            "        neighbors[e.target] = 1;\n      }\n" +
            "    });\n" +
            "   self.sigmaInstance.get().iterNodes(function(n){\n" +
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


    @javascript(
        "   var node = self.sigmaInstance.get().getNodes(uri);\n" +
        "   node.attr['rdfType'] = rdfType;\n")
    private def setRdfType(uri: String, rdfType: String) {}

    @javascript(
        "   var rdfType = node.attr['rdfType'];\n" +
        "   if(rdfType == null)" +
        "       return '';\n" +
        "   return rdfType;"
    )
    override def getRdfType(node: Node): String = ""

    private def getAttributes(vertex: rdf.IdentifiedVertex, edges: Seq[rdf.Edge]): ListBuffer[(String, _)] = {

        val result: ListBuffer[(String, _)] = new ListBuffer[(String, _)]()
        edges.foreach{ edge =>
            if(edge.origin == vertex) {
                edge.destination match {
                    case i: rdf.LiteralVertex =>
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
        var resultList = "<ul>"
        attr.foreach { record =>
            resultList += "<li>" + record._1 + " : " + record._2 + "</li>"
        }
        resultList += "</ul>"
        resultList
    }
}
