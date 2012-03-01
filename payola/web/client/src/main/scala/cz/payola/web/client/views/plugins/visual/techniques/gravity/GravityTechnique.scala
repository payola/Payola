package cz.payola.web.client.views.plugins.visual.techniques.gravity

import collection.mutable.ListBuffer
import cz.payola.web.client.views.plugins.visual.graph.{EdgeView, VertexView}
import cz.payola.web.client.views.plugins.visual.techniques.BaseTechnique
import cz.payola.web.client.views.plugins.visual.Vector
import cz.payola.common.rdf.Graph
import s2js.adapters.js.dom.Element

class GravityTechnique extends BaseTechnique
{
    /**
      * How much vertices push away each other
      */
    private val repulsion: Double = 300

    /**
      * How much edges make vertices attracted to each other
      */
    private val attraction: Double = 0.05

    override def init(graph: Graph, container: Element) {
        super.init(graph, container)
        performTechnique()
    }

    def performTechnique() {
        basicTreeStructure(graphView.get.vertexViews)
        val vertexViewPacks = buildVertexViewsWorkingStructure(graphView.get.vertexViews)
        val edgeViewPacks = buildEdgeViewsWorkingStructure(vertexViewPacks, graphView.get.edgeViews)
        run(vertexViewPacks, edgeViewPacks)
        moveGraphToUpperLeftCorner(graphView.get.vertexViews)
        flip(graphView.get.vertexViews)
    }

    private def buildVertexViewsWorkingStructure(vertexViews: ListBuffer[VertexView]): ListBuffer[VertexViewPack] = {
        var workingStructure = ListBuffer[VertexViewPack]()

        vertexViews.foreach {view =>
            workingStructure += new VertexViewPack(view)
        }

        workingStructure
    }

    private def buildEdgeViewsWorkingStructure(vertexViewPacks: ListBuffer[VertexViewPack],
        edgeViews: ListBuffer[EdgeView]): ListBuffer[EdgeViewPack] = {
        var workingStructure = ListBuffer[EdgeViewPack]()
        edgeViews.foreach {view =>
            val origin = vertexViewPacks.find {element =>
                element.value.vertexModel eq view.originView.vertexModel
            }
            val destination = vertexViewPacks.find {element =>
                element.value.vertexModel eq view.destinationView.vertexModel
            }
            workingStructure += new EdgeViewPack(view, origin.get, destination.get)
        }

        workingStructure
    }

    /**
      * Gravity model algorithm. Use it carefully, the computation complexity is quite high
      * (vertices^2 + edges + vertices) * "something strange". The main loop ends if sum of velocities of
      * vertices is less than 0.5. The inner loops compute forces effecting vertices. Vertices push away
      * each other and their edges push them together. In the first loop are computed repulsions. In the
      * second are computed attractions. And in the last loop are the forces applied.
      * @param vertexViewPacks
      * @param edgeViewPacks
      */
    def run(vertexViewPacks: ListBuffer[VertexViewPack], edgeViewPacks: ListBuffer[EdgeViewPack]) {
        var repeat = true
        var stabilization: Double = 0;

        while (repeat) {

            vertexViewPacks.foreach {pushed =>

                pushed.force = Vector(0, 0)

                //set repulsion by all other vertices
                vertexViewPacks.foreach {pushing =>
                    if (pushed.value.vertexModel ne pushing.value.vertexModel) {

                        //minus repulsion of vertices
                        val forceElimination = repulsion / (
                            scala.math.pow(pushed.value.position.x - pushing.value.position.x, 2) +
                                scala.math.pow(pushed.value.position.y - pushing.value.position.y, 2))
                        pushed.force = pushed.force +
                            (pushed.value.position.toVector - pushing.value.position.toVector) * forceElimination
                    }
                }
            }

            //set attraction by edges
            edgeViewPacks.foreach {edgeViewPack =>
                val origin = edgeViewPack.originVertexViewPack
                val destination = edgeViewPack.destinationVertexViewPack

                origin.force = origin.force +
                    (destination.value.position.toVector - origin.value.position.toVector) * attraction
                destination.force = destination.force +
                    (origin.value.position.toVector - destination.value.position.toVector) * attraction
            }

            stabilization = 0

            //move vertices by the calculated vertices
            vertexViewPacks.foreach {moved =>
                if (!moved.value.selected) {

                    moved.velocity = (moved.force + moved.velocity) / (vertexViewPacks.length - moved.value.edges
                        .length)
                    stabilization += scala.math.abs(moved.velocity.x) + scala.math.abs(moved.velocity.y)
                    moved.value.position = moved.value.position + moved.velocity
                }
            }
            repeat = stabilization >= 0.5;
        }
    }
}
