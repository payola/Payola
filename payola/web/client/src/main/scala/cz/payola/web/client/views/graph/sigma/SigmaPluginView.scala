package cz.payola.web.client.views.graph.sigma

import cz.payola.web.client.views.graph.PluginView
import cz.payola.common.rdf
import cz.payola.web.client.views.elements._
import s2js.adapters.js.sigma
import s2js.adapters.js.sigma.Node
import s2js.adapters._
import s2js.adapters.browser.window
import scala.collection.mutable.ListBuffer
import cz.payola.common.entities.settings._
import cz.payola.web.client.views.bootstrap.Icon
import cz.payola.web.client.views.graph.sigma.properties._
import cz.payola.web.client.views.graph.sigma.PropertiesSetter._
import cz.payola.web.client.models.PrefixApplier
import cz.payola.web.shared.transformators.IdentityTransformator
import cz.payola.web.client.views.bootstrap.modals.FatalErrorModal
import s2js.compiler.javascript
import s2js.adapters.html.Element

abstract class SigmaPluginView(name: String, prefixApplier: Option[PrefixApplier]) extends PluginView[rdf.Graph](name, prefixApplier) {

    protected var sigmaPluginWrapper = new Div().setAttribute("style", "padding: 0 5px; min-width: 200px; min-height: 200px;")

    sigmaPluginWrapper.mouseMoved += { e =>
        mouseX = e.clientX
        mouseY = e.clientY
        false
    }

    protected var mouseX = 0.0
    protected var mouseY = 0.0

    private var edgesNum = 0

    protected var sigmaInstance: Option[sigma.Sigma] = None

    private var parentElement: Option[html.Element] = None

    protected val animationStartStopButton = new Button(new Text("Start positioning"), "pull-right", new Icon(Icon.refresh)).setAttribute("style", "margin: 0 5px;")

    def createSubViews = List(sigmaPluginWrapper)

    override def render(parent: html.Element) {
        super.render(parent)
        parentElement = Some(parent)
    }

    override def renderControls(toolbar: html.Element) {
        animationStartStopButton.render(toolbar)
        animationStartStopButton.setIsEnabled(true)
    }

    override def destroyControls() {
        animationStartStopButton.destroy()
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
            clearGraph()
        }
    }

    def getRdfType(node: Node): String

    override def updateCustomization(newCustomization: Option[DefinedCustomization]) {

        /*TODO
        if (sigmaInstance.isDefined) {
            if(newCustomization.isDefined) {
                sigmaInstance.get.iterNodes{ node =>
                    val foundCustomization = newCustomization.get.classCustomizations.find{_.uri == getRdfType(node)}
                    updateNode(foundCustomization, node) //update configuration of the node

                    foundCustomization.foreach{ classCustomization =>
                        sigmaInstance.get.iterEdges{ edge =>
                            if(edge.id.contains(node.label)){
                                val propertyCustomization = classCustomization.propertyCustomizations.find(_.uri == edge.label)
                                updateEdge(propertyCustomization, edge) //update configuration of the edge
                            }
                        }
                    }
                }
            } else {
                sigmaInstance.get.graph.nodes.foreach{ node =>
                    updateNode(None, node)
                }
                sigmaInstance.get.graph.edges.foreach{ edge =>
                    updateEdge(None, edge)
                }
            }


            sigmaInstance.get.refresh()
        }*/
    }

    override def updateGraph(graph: Option[rdf.Graph], contractLiterals: Boolean = true) {

        super.updateGraph(graph, false)

        parentElement.foreach(updateSigmaPluginSize(_))

        if (sigmaInstance.isEmpty && graph.isEmpty) {
            renderMessage(sigmaPluginWrapper.htmlElement, "The graph is empty...")
        } else {
            if(sigmaInstance.isDefined) {
                clearGraph()
            }
            fillGraph(graph, sigmaPluginWrapper.htmlElement)
        }
    }

    @javascript("""
                   return new sigma({
                        graph: {
                            nodes: nodeList,
                            edges: edgeList
                        },
                        container: wrapper,
                        settings: {
                            drawEdges: true,
                            defaultNodeColor: '#0088cc'
                        }
                   });
                """)
    protected def initSigma(nodeList: List[NodeProperties], edgeList: List[EdgeProperties], wrapper: Element): sigma.Sigma = null

    def clearGraph()

    def setDrawingProperties()

    def fillGraph(graph: Option[rdf.Graph], wrapper: Element)

    protected def createVertexView(label: String, vertex: rdf.IdentifiedVertex, edgesCount: Int,
        attributes: ListBuffer[(String, _)]): NodeProperties =  {

        val props = new NodeProperties
        props.size = scala.math.min(props.size + edgesCount, 20)
        props.label = label
        props.value = attributes
        props.id = vertex.uri

        props
    }

    protected def createEdgeView(edge: rdf.Edge ): EdgeProperties = {
        val props = new EdgeProperties
        props.id = edgesNum+":"+edge.origin.uri+":"+edge.uri
        props.source = edge.origin.uri
        props.target = edge.destination.toString()
        edgesNum += 1

        props
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
