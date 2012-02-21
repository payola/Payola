package cz.payola.web.client.views.graph.algorithms

import collection.mutable.ListBuffer
import cz.payola.web.client.views.graph.{EdgeView, VertexView}
import cz.payola.web.client.views.Vector

class GravityModel extends AlgoBase {

    /**
      * How much vertices push away each other
      */
    private val repulsion: Double = 300

    /**
      * How much edges make vertices attracted to each other
      */
    private val attraction: Double = 0.05

    def perform(vertexViews: ListBuffer[VertexView], edgeViews: ListBuffer[EdgeView]) {
        treeLikeVerticesPositioning(vertexViews)
        run(vertexViews, edgeViews)
        moveGraphToUpperLeftCorner(vertexViews)
        flip(vertexViews)
    }

    /**
      * Gravity model algorithm. Use it carefully, the computation complexity is quite high
      * (vertices^2 + edges + vertices) * "something strange". The main loop ends if sum of velocities of
      * vertices is less than 0.5. The inner loops compute forces effecting vertices. Vertices push away
      * each other and their edges push them together. In the first loop are computed repulsions. In the
      * second are computed attractions. And in the last loop are the forces applied.
      * @param vertexViews
      * @param edgeViews
      */
    def run(vertexViews: ListBuffer[VertexView], edgeViews: ListBuffer[EdgeView]) {
        var repeat = true
        var stabilization: Double = 0;

        while(repeat){
            vertexViews.foreach{ v1: VertexView =>

                v1.force = Vector(0, 0)
                stabilization = 0

                //set repulsion by all other vertices
                vertexViews.foreach{ v2: VertexView =>
                    if (v1.vertexModel.uri != v2.vertexModel.uri){

                        //minus repulsion of vertices
                        val forceElimination = repulsion / (scala.math.pow(v1.position.x - v2.position.x, 2) +
                            scala.math.pow(v1.position.y - v2.position.y, 2))
                        v1.force = v1.force + (v1.position.toVector - v2.position.toVector) * forceElimination
                    }
                }
            }

            //set attraction by edges
            edgeViews.foreach{ eView =>
                val origin = eView.originView
                val destination = eView.destinationView

                origin.force = origin.force +
                    (destination.position.toVector - origin.position.toVector) * attraction
                destination.force = destination.force +
                    (origin.position.toVector - destination.position.toVector) * attraction
            }

            //move vertices by the calculated vertices
            vertexViews.foreach{ v =>
                if (!v.selected){

                    v.velocity = (v.force + v.velocity) / (vertexViews.length - v.edges.length)
                    stabilization += scala.math.abs(v.velocity.x) + scala.math.abs(v.velocity.y)
                    v.position = v.position + v.velocity

                }
            }
            repeat = stabilization >= 0.5;
        }
    }
}
