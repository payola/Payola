package cz.payola.web.client.views.graph.sigma

import cz.payola.web.client.views.graph.PluginView
import cz.payola.common.rdf
import cz.payola.web.client.views.elements.Div
import s2js.adapters.js.sigma
import s2js.adapters._
import s2js.adapters.browser.window
import scala.collection.mutable.ListBuffer
import cz.payola.common.entities.settings.OntologyCustomization

abstract class SigmaPluginView(name: String) extends PluginView(name){
    protected var sigmaPluginWrapper = new Div().setAttribute("style", "padding: 0 5px; min-width: 200px; min-height: 200px;")

    private var edgesNum = 0

    protected var sigmaInstance: Option[sigma.Sigma] = None

    def createSubViews = List(sigmaPluginWrapper)

    override def render(parent: html.Element) {
        super.render(parent)
        updateSigmaPluginSize(parent)
    }

    private def updateSigmaPluginSize(parent: html.Element) {

        val width = window.innerWidth - parent.offsetLeft
        val height = window.innerHeight - parent.offsetTop

        sigmaPluginWrapper.setAttribute("style", "padding: 0 5px; min-width: "+
            width+"px; min-height: "+height+"px;")
    }

    override def destroy() {
        super.destroy()

        if(sigmaInstance.isDefined) {
            sigmaInstance.get.emptyGraph
        }
    }

    override def updateOntologyCustomization(newCustomization: Option[OntologyCustomization]) {

        if (sigmaInstance.isDefined) {
            sigmaInstance.get.iterEdges{ edge =>
                newCustomization.foreach{ newCust =>
                    val foundCustomization = newCust.classCustomizations.find{_.uri == edge.label}

                    foundCustomization.foreach { custom => edge.updateProperties(custom) }
                }
            }
            sigmaInstance.get.iterNodes{ node =>
                newCustomization.foreach{ newCust =>
                    val foundCustomization = newCust.classCustomizations.find{_.uri == node.label}

                    foundCustomization.foreach { custom => node.updateProperties(custom) }
                }

            }

            sigmaInstance.get.draw()
        }
    }

    override def updateGraph(graph: Option[rdf.Graph], contractLiterals: Boolean) {

        if (sigmaInstance.isEmpty && graph.isEmpty) {
            renderMessage(sigmaPluginWrapper.htmlElement, "The graph is empty...")
        } else {
            if(sigmaInstance.isDefined) { //drop the current graph
                sigmaInstance.get.emptyGraph
            } else {
                sigmaInstance = Some(sigma.init(sigmaPluginWrapper.htmlElement))
            }
            setDrawingProperties()
            fillGraph(graph)
            sigmaInstance.get.draw()
        }

        super.updateGraph(graph, false)
    }

    def setDrawingProperties()

    def setGraphProperties()

    def fillGraph(graph: Option[rdf.Graph])

    protected def createVertexView(vertex: rdf.IdentifiedVertex, edgesCount: Int, attributes: ListBuffer[(String, _)]) {

        val props = new NodeProperties
        props.size = scala.math.min(props.size + edgesCount, 20)
        props.label = vertex.uri
        props.value = attributes
        sigmaInstance.get.addNode(vertex.uri, props)
    }

    protected def createEdgeView(edge: rdf.Edge) {
        edge.destination match {
            case i: rdf.IdentifiedVertex =>
                sigmaInstance.get.addEdge(edgesNum+edge.uri, edge.origin.uri, i.uri,
                    new EdgeProperties())
        }
        edgesNum += 1
    }
}
