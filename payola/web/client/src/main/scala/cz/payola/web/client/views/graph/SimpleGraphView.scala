package cz.payola.web.client.views.graph

import cz.payola.web.client.views._
import cz.payola.web.client.views.graph.visual.techniques.tree.TreeTechnique
import cz.payola.common.rdf._
import cz.payola.common.entities.settings._
import s2js.adapters.html.Element
import scala.collection.mutable.ArrayBuffer
import cz.payola.common.visual.Color
import s2js.runtime.client.scala.collection.mutable.HashMap
import cz.payola.web.client.models.VertexVariableNameGenerator
import cz.payola.common.rdf.Vertex
import cz.payola.web.client.events._
import s2js.compiler.javascript

/**
 * Pattern selection view, currently not generic, DataCube only. It offers the patternUpdated Event, which is triggered
 * when the number of selected vertices reaches the value specified in verticesCount.
 *
 * The results is in refVertices and vertices fields.
 *
 * @param placeholder element to render to
 * @param verticesCount number of vertices for selection
 * @author Jiri Helmich
 */
class SimpleGraphView(placeholder: ElementView[Element], verticesCount: Int) extends GraphView
{
    val technique = new TreeTechnique()

    technique.render(placeholder.htmlElement)

    val vertices = new ArrayBuffer[Vertex]()

    val refVertices = new ArrayBuffer[Vertex]()

    val edges = new ArrayBuffer[Edge]()

    var newPath = true

    val map = new HashMap[Vertex, String]()
    val generator = new VertexVariableNameGenerator()

    val patternUpdated = new UnitEvent[SimpleGraphView, EventArgs[SimpleGraphView]]()

    @javascript("""jQuery(".datacube-infobar .message").hide(); jQuery(".datacube-infobar .message").eq(i).show()""")
    def showMessage(i: Int) {}

    /**
     * we need to select a connected component.
     */
    technique.vertexSelected += {
        e =>

            if (vertices.size == 0) {
                vertices += e.vertex
                newPath = false
                technique.updateVertexColor(e.vertex, Some(Color.Green))
            } else {

                val addRef = { () =>
                    if (!refVertices.contains(e.vertex)){
                        refVertices += e.vertex
                        technique.updateVertexColor(e.vertex, Some(Color.Red))
                    }
                }

                if (vertices.contains(e.vertex)) {

                    addRef()

                } else {
                    currentGraph.map { g =>
                        val appendEdge = { edge: Edge =>
                            vertices += e.vertex
                            technique.updateVertexColor(e.vertex, Some(Color.Green))
                            edges += edge
                        }

                        vertices.map { v =>
                            g.getIncomingEdges(e.vertex).find(_.origin == v).map(appendEdge)
                            g.getOutgoingEdges(e.vertex).find(_.destination == v).map(appendEdge)
                        }
                    }
                }

            }

            showMessage(refVertices.size)

            if (verticesCount == refVertices.size) {
                patternUpdated.trigger(new EventArgs[SimpleGraphView](this))
            }
    }

    private def getVertexName(map: HashMap[Vertex, String], vertex: Vertex,
        generator: VertexVariableNameGenerator): String = {
        if (!map.isDefinedAt(vertex)) {
            map.put(vertex, generator.nextName)
        }

        "?" + map.get(vertex).get
    }

    /**
     * Double-clicked vertices.
     * @return variables names
     */
    def getSignificantVertices = {
        getPattern
        refVertices.map("?"+map.get(_).get)
    }

    /**
     * @return Selected SPARQL pattern
     */
    def getPattern: String = {
        val generator = new VertexVariableNameGenerator()

        edges.map {
            e =>
                val originVar = getVertexName(map, e.origin, generator)
                val destinationVar = getVertexName(map, e.destination, generator)

                originVar + " <" + e.uri + "> " + destinationVar + " ."
        }.mkString("\n")
    }

    override def update(graph: Option[Graph], customization: Option[DefinedCustomization], serializedGraph: Option[Any]) {
        super.update(graph, customization, None)
        technique.update(graph, customization, None)
        technique.drawGraph()

        showMessage(0)
    }

    override def updateGraph(graph: Option[Graph], contractLiterals: Boolean) {
        super.updateGraph(graph, false)
        technique.updateGraph(graph, false)
        technique.drawGraph()

        showMessage(0)
    }

    override def updateCustomization(customization: Option[DefinedCustomization]) {
        super.updateCustomization(customization)
        technique.updateCustomization(customization)

        showMessage(0)
    }

    override def updateVertexColor(vertex: Vertex, color: Option[Color]) {
        technique.updateVertexColor(vertex, color)
    }
}
