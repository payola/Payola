package cz.payola.web.client.views.graph.sigma

import cz.payola.common.rdf
import rdf._
import s2js.compiler.javascript
import scala.collection.mutable.ListBuffer
import cz.payola.web.client.views.elements.Text
import cz.payola.web.client.views.graph.sigma.properties.DrawingProperties
import s2js.adapters.js.sigma.Node
import cz.payola.web.client.models.PrefixApplier
import cz.payola.web.client.views.bootstrap.modals.FatalErrorModal
import cz.payola.web.shared.transformators.IdentityTransformator

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
        """self.popUp && self.popUp.remove();
         var node;
         self.sigmaInstance.get().iterNodes(function(n){ node = n; },[event.content[0]]);
         var nodeContent = node['attr']['value']; 
        if(typeof nodeContent == 'undefined' || nodeContent == null || nodeContent.isEmpty())
          return;
        var popUpX = self.getPopUpX(node.displayX);
        var popUpY = self.getPopUpY(node.displayY);
        self.popUp = $( '<div class=\"node-info-popup\" onclick="cz.payola.web.client.views.graph.sigma.GraphSigmaPluginView.prototype.showFixedVertexInfo(this);></div>').append(
        self.attributesToString(nodeContent)).attr(
        'class', 'resizable vertex-info').attr(
        'id', 'node-info'+self.sigmaInstance.get().getID()).css({
       'left': popUpX,'top': popUpY, 'width': self.popUpWidth+'px', 'height': self.popUpHeight+'px'});
        $('ul',self.popUp).css('margin','0 0 0 20px');$(self.sigmaPluginWrapper.htmlElement).append(self.popUp);""")
    private def showVertexInfo(event: Unit) {}


    @javascript("""if(element.className.indexOf("visible") == -1) {element.className += " visible"; }""")
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
    @javascript("""if(self.popUp) self.popUp.attr('class', self.popUp.attr('class').replace(" visible", "")); self.hideVertexInfo(event);""")
    private def hideFixedVertexInfo(event: Unit) {}

    //TODO javascript -> scala
    @javascript("""if(self.popUp && self.popUp.attr('class').indexOf(" visible") == -1) { self.popUp.remove(); self.popUp = false; }""")
    private def hideVertexInfo(event: Unit) {}

    //TODO javascript -> scala
    @javascript(
        """var nodes = event.content;
            var neighbors = {};
            self.sigmaInstance.get().iterEdges(function(e){
              if(nodes.indexOf(e.source)<0 && nodes.indexOf(e.target)<0){
                if(!e.attr['grey']){
                  e.attr['true_color'] = e.color;
                  e.color = 'grey';
                  e.attr['grey'] = 1;
                }
              }else{
                e.color = e.attr['grey'] ? e.attr['true_color'] : e.color;
                e.attr['grey'] = 0;
                neighbors[e.source] = 1;
                neighbors[e.target] = 1; }
            });
           self.sigmaInstance.get().iterNodes(function(n){
              if(!neighbors[n.id]){
                if(!n.attr['grey']){
                  n.attr['true_color'] = n.color;
                  n.color = 'grey';
                  n.attr['grey'] = 1;        }
              }else{
                n.color = n.attr['grey'] ? n.attr['true_color'] : n.color;
                n.attr['grey'] = 0;      }
            });
           self.sigmaInstance.get().draw(5,5,5);""")
    private def hideVertices(event: Unit) {}

    //TODO javascript -> scala
    @javascript(
        """    self.sigmaInstance.get().iterEdges(function(e){
              e.color = e.attr['grey'] ? e.attr['true_color'] : e.color;
              e.attr['grey'] = 0;
            }).iterNodes(function(n){
              n.color = n.attr['grey'] ? n.attr['true_color'] : n.color;
              n.attr['grey'] = 0;
            }).draw(2,2,2); """)
    private def showVertices(event: Unit) {}


    @javascript(
        """   var node = self.sigmaInstance.get().getNodes(uri); node.attr['rdfType'] = rdfType; """)
    private def setRdfType(uri: String, rdfType: String) {}

    @javascript(
        """   var rdfType = node.attr['rdfType'];
           if(rdfType == null) 
               return ''; 
           return rdfType;"""
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

    override def isAvailable(availableTransformators: List[String], evaluationId: String,
        success: () => Unit, fail: () => Unit) {

            IdentityTransformator.getSmapleGraph(evaluationId) { sample =>
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
}
