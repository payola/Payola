package cz.payola.web.client.views.graph.sigma

import cz.payola.common.rdf
import rdf._
import s2js.compiler.javascript
import scala.collection.mutable.ListBuffer
import cz.payola.web.client.views.elements._
import cz.payola.web.client.views.graph.sigma.properties._
import s2js.adapters.js.sigma.Node
import cz.payola.web.client.models.PrefixApplier
import cz.payola.web.client.views.bootstrap.modals.FatalErrorModal
import cz.payola.web.shared.transformators.IdentityTransformator
import s2js.adapters.html.Element
import cz.payola.common.entities.settings.DefinedCustomization
import cz.payola.web.client.views.graph.sigma.PropertiesSetter._
import cz.payola.web.client.views.elements.lists._
import cz.payola.web.client.View

class GraphSigmaPluginView(prefixApplier: Option[PrefixApplier]) extends SigmaPluginView("Sigma.js", prefixApplier) {

    val popUp: Any = null //used only in the javascript of functions showVertexInfo and hideVertexInfo
    val popUpWidth = 300
    val popUpHeight = 300
    private var atlasAnimationRunnig = false

    def setDrawingProperties() {
        sigmaInstance.get.drawingProperties(new DrawingProperties)
    }

    @javascript("""
                   self.sigmaInstance.get().graph.clear();
                   self.sigmaInstance.get().refresh();
                """)
    def clearGraph() {}

    def fillGraph(graphOpt: Option[rdf.Graph], wrapper: Element)  {

        graphOpt.foreach{ graph =>
            val nodeList = graph.vertices.filter(_.isInstanceOf[rdf.IdentifiedVertex]).map{ vertex =>
                val idVertex = vertex.asInstanceOf[rdf.IdentifiedVertex]
                val node = createVertexView(
                    prefixApplier.map(_.applyPrefix(idVertex.uri)).getOrElse(idVertex.uri),
                    idVertex, countEdges(idVertex, graph.edges), getAttributes(idVertex, graph.edges))
                setRdfType(node, getRdfTypeForVertexView(graph.edges, idVertex.uri))
                node
            }.toList
            val edgeList = graph.edges.filter(_.destination.isInstanceOf[rdf.IdentifiedVertex]).map(createEdgeView(_)).toList

            sigmaInstance = Some(initSigma(nodeList, edgeList, wrapper))

            sigmaInstance.get.bind("overNode", showVertexInfo(_))
            sigmaInstance.get.bind("outNode", hideVertexInfo(_))

            sigmaInstance.get.bind("clickNodes", showFixedVertexInfo(_))
            sigmaInstance.get.bind("clickStage", hideFixedVertexInfo(_))

            sigmaInstance.get.bind("overNode", hideVertices(_))
            sigmaInstance.get.bind("outNode", showVertices(_))

            sigmaInstance.get.refresh()
        }
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

    private def stopAnimation() {
        if (sigmaInstance.isDefined) {
            sigmaInstance.get.stopForceAtlas2()
            animationStartStopButton.subViews.foreach{ child =>
                child match {
                    case i: Text =>
                        i.text = "Start positioning"
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
                        i.text = "Stop positioning"
                }
            }
        }
    }

    animationStartStopButton.mouseClicked += { e =>
        if (atlasAnimationRunnig) {
            atlasAnimationRunnig = false
            stopAnimation()
        } else {
            atlasAnimationRunnig = true
            startAnimation()
        }
        false
    }

    @javascript("""
         if(self.popUp) { self.popUp.destroy(); self.popUp = false; }
         var nodeContent = event.data.node.value;
         if(typeof nodeContent == 'undefined' || nodeContent == null || nodeContent.isEmpty())
            return;
        self.popUp = self.createInfoTable(nodeContent, self.mouseX, self.mouseY, $(self.sigmaPluginWrapper.htmlElement).offset().top);
        self.popUp.render(self.sigmaPluginWrapper.htmlElement);""")
    private def showVertexInfo(event: Unit) {}

    private def createInfoTable(nodeContent: ListBuffer[(String, Any)], nodeX: Int, nodeY: Int, topOffset: Int): View = {
        val infoDiv = new Div(List(attributesToString(nodeContent)), "node-info-popup resizable vertex-info")
        infoDiv.mouseClicked += { e =>
            showFixedVertexInfo(e)
            false
        }

        infoDiv.setAttribute("id", "node-info"+sigmaInstance.get.id)
        infoDiv.setAttribute("style", "left: "+getPopUpX(nodeX)+"px; top: "+getPopUpY(nodeY, topOffset)+"px; width: "+popUpWidth+"px; height: "+popUpHeight+"px;")
        infoDiv
    }


    @javascript("""
          if(self.popUp && self.popUp.htmlElement.className.indexOf("visible") == -1) {
            self.popUp.htmlElement.className += " visible";
            //TODO uncomment after jQuery bug is resolved - scrollbar and resizable corner can not be pressed in FF and in IE mouse cursor sticks to scrollbar after scrolling
            //$(self.popUp.htmlElement).draggable();
          }""")
    private def showFixedVertexInfo(element: Unit) {}



    private def getPopUpX(nodeLocationX: Int): Int = {
        if (nodeLocationX - popUpWidth > 0)
            nodeLocationX - popUpWidth - 20
        else
            nodeLocationX + 20
    }

    private def getPopUpY(nodeLocationY: Int, offset: Int): Double = {
        if (nodeLocationY + popUpHeight - offset < sigmaPluginWrapper.htmlElement.clientHeight) {
            nodeLocationY - offset
        } else {
            sigmaPluginWrapper.htmlElement.clientHeight - popUpHeight - 10
        }
    }

    @javascript("""if(self.popUp) self.popUp.htmlElement.className = self.popUp.htmlElement.className.replace(" visible", ""); self.hideVertexInfo(event);""")
    private def hideFixedVertexInfo(event: Unit) {}

    @javascript("""
          if(self.popUp && self.popUp.htmlElement.className.indexOf("visible") == -1) {
            $(self.popUp.htmlElement).draggable({ disabled: true });
            self.popUp.destroy(); self.popUp = false;
          }""")
    private def hideVertexInfo(event: Unit) {}

    @javascript(
        """var node = event.data.node;
            var neighbors = {};
            self.sigmaInstance.get().graph.edges().foreach(function(e){
              if(node.id != e.source && node.id != e.target){
                if(!e.hidden){
                  e.trueColor = e.color;
                  e.color = '#999999';
                  e.hidden = 1;
                }
              }else{
                e.color = e.hidden ? e.trueColor : e.color;
                e.hidden = 0;
                neighbors[e.source] = 1;
                neighbors[e.target] = 1; }
            });
           self.sigmaInstance.get().graph.nodes().foreach(function(n){
              if(!neighbors[n.id]){
                if(!n.hidden){
                  n.trueColor = n.color;
                  n.color = '#999999';
                  n.hidden = 1;        }
              }else{
                n.color = n.hidden ? n.trueColor : n.color;
                n.hidden = 0;      }
            });
           self.sigmaInstance.get().refresh();""")
    private def hideVertices(event: Unit) {}

    @javascript("""
            self.sigmaInstance.get().graph.edges().foreach(function(e){
                e.color = e.hidden ? e.trueColor : e.color;
                e.hidden = 0;
            })
            self.sigmaInstance.get().graph.nodes().foreach(function(n){
                n.color = n.hidden ? n.trueColor : n.color;
                n.hidden = 0;
            })
            self.sigmaInstance.get().refresh();
            """)
    private def showVertices(event: Unit) {}


    @javascript(""" node.rdfType = rdfType; """)
    private def setRdfType(node: NodeProperties, rdfType: String) {}

    @javascript("""
           if(node.rdfType == null)
               return ''; 
           return node.rdfType;"""
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

    private def attributesToString(attributes: ListBuffer[(String, Any)]): UnorderedList = {

        new UnorderedList(attributes.map{ attr =>
            new ListItem(List(new Text(attr._1+": "+attr._2)))
        })
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
}
