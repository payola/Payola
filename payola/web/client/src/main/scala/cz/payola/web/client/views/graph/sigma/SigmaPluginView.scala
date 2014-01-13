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

abstract class SigmaPluginView(name: String, prefixApplier: Option[PrefixApplier]) extends PluginView[rdf.Graph](name, prefixApplier){
    protected var sigmaPluginWrapper = new Div().setAttribute("style", "padding: 0 5px; min-width: 200px; min-height: 200px;")

    private var edgesNum = 0

    protected var sigmaInstance: Option[sigma.Sigma] = None

    //protected val animationStartStopButton = new Button(new Text("Start"), "pull-right", new Icon(Icon.refresh)).setAttribute("style", "margin: 0 5px;")

    def createSubViews = List(sigmaPluginWrapper)

    override def render(parent: html.Element) {
        super.render(parent)
        updateSigmaPluginSize(parent)
    }

    override def renderControls(toolbar: html.Element) {
        //animationStartStopButton.render(toolbar)
        //animationStartStopButton.setIsEnabled(true)
    }

    override def destroyControls() {
        //animationStartStopButton.destroy()
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

    def getRdfType(node: Node): String

    override def updateCustomization(newCustomization: Option[DefinedCustomization]) {

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
                sigmaInstance.get.iterNodes{ node =>
                    updateNode(None, node)
                }
                sigmaInstance.get.iterEdges{ edge =>
                    updateEdge(None, edge)
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

    def fillGraph(graph: Option[rdf.Graph])

    protected def createVertexView(label: String, vertex: rdf.IdentifiedVertex, edgesCount: Int,
        attributes: ListBuffer[(String, _)]) {

        val props = new NodeProperties
        props.size = scala.math.min(props.size + edgesCount, 20)
        props.label = label
        props.value = attributes
        sigmaInstance.get.addNode(vertex.uri, props)
    }

    protected def createEdgeView(edge: rdf.Edge ) {
        edge.destination match {
            case i: rdf.IdentifiedVertex =>
                sigmaInstance.get.addEdge(edgesNum+":"+edge.origin.uri+":"+edge.uri, edge.origin.uri, i.uri, new EdgeProperties())
                edgesNum += 1
        }
    }

    override def isAvailable(availableTransformators: List[String], evaluationId: String,
        success: () => Unit, fail: () => Unit) {

        IdentityTransformator.getSmapleGraph(evaluationId) { sample =>
            if(sample.isEmpty && availableTransformators.exists(_.contains("IdentityTransformator"))) {
                success()
            }
        }
        { error =>
            fail()
            val modal = new FatalErrorModal(error.toString())
            modal.render()
        }
    }

    override def loadDefaultCachedGraph(evaluationId: String, updateGraph: rdf.Graph => Unit) {
        IdentityTransformator.transform(evaluationId)(updateGraph(_))
        { error =>
            val modal = new FatalErrorModal(error.toString())
            modal.render()
        }
    }
}
