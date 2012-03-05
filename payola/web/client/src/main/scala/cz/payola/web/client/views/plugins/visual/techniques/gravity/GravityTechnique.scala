package cz.payola.web.client.views.plugins.visual.techniques.gravity

import collection.mutable.ListBuffer
import cz.payola.web.client.views.plugins.visual.graph.{EdgeView, VertexView}
import cz.payola.web.client.views.plugins.visual.techniques.BaseTechnique
import cz.payola.web.client.views.plugins.visual.Vector

/**
  * Visual plug-in technique that places the vertices based on their edges.
  * Every vertex causes a negative force on every other vertex, that makes
  * vertices push each other away. Every edge causes a positive force on
  * both vertices, that share the edge, that makes vertices push each other
  * closer. The final positions of the vertices is reached when all vertices
  * have "small enough" velocity.
  */
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

    /**
      * The "small enough" constant. The computation ends when
      * Sum(vertexViewPacks.velocities) is less than this number.
      * 0.5 is well tested, change it carefully.
      */
    private val velocitiesStabilization = 0.5

    def performTechnique() {
        basicTreeStructure(graphView.get.vertexViews)
        val vertexViewPacks = buildVertexViewsWorkingStructure(graphView.get.vertexViews)
        val edgeViewPacks = buildEdgeViewsWorkingStructure(vertexViewPacks, graphView.get.edgeViews)
        run(vertexViewPacks, edgeViewPacks)
        moveGraphToUpperLeftCorner(graphView.get.vertexViews)
        flip(graphView.get.vertexViews)
    }

    /**
      * Constructus a structure of vertexViewPacks, that the gravity algorithm works with.
      * @param vertexViews to create vertexViewPacks from
      * @return created vertexViewPacks
      */
    private def buildVertexViewsWorkingStructure(vertexViews: ListBuffer[VertexView]): ListBuffer[VertexViewPack] = {
        var workingStructure = ListBuffer[VertexViewPack]()

        vertexViews.foreach {view =>
            workingStructure += new VertexViewPack(view)
        }

        workingStructure
    }

    /**
      * Constructs a structure of edgeViewPacks, that the gravity algorithm works with.
      * @param vertexViewPacks to search for connected vertices
      * @param edgeViews to create edgeViewPacks from
      * @return created edgeViewPacks
      */
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
      * (vertices^2 + edges + vertices) * "something strange".
      * The main loop ends if sum of velocities of vertices is less than 0.5. The inner loops compute forces effecting vertices. Vertices push away
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
            repeat = stabilization >= velocitiesStabilization;
        }
    }
}
