package cz.payola.web.client.views.plugins.visual.techniques.gravity

import collection.mutable.ListBuffer
import cz.payola.web.client.views.plugins.visual.graph.{EdgeView, VertexView}
import cz.payola.web.client.views.plugins.visual.techniques._
import cz.payola.web.client.views.plugins.visual.{Point, Vector}
import s2js.adapters.js.dom.Date

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
    private val velocitiesStabilization = 0.8

    override def clean() {
        super.clean()
    }

    def getName:String = {
        "gravity visualisation"
    }

    def performTechnique() {

        val moveToCorner = new Animation(Animation.moveGraphToUpperLeftCorner, graphView.get.vertexViews,
            None, redrawQuick, redraw, None)
        val flip = new Animation(Animation.flipGraph, graphView.get.vertexViews,
            Some(moveToCorner), redrawQuick, redraw, None)


        val animationOfThis = new Animation(runningAnimation,
            graphView.get.vertexViews, Some(flip), redrawQuick, redrawQuick, Some(50))

        basicTreeStructure(graphView.get.vertexViews, true, Some(animationOfThis))
    }
    
    private def runningAnimation(vertexViewsToAnimate: ListBuffer[VertexView], followingAnimation: Option[Animation],
        redrawQuick: () => Unit, redrawFinal: () => Unit, runDuration: Option[Int]) {


        val vertexViewPacks = buildVertexViewsWorkingStructure(graphView.get.vertexViews)
        val edgeViewPacks = buildEdgeViewsWorkingStructure(vertexViewPacks, graphView.get.edgeViews)

        vertexViewPacks.foreach{ vPack =>
            vPack.currentPosition = vPack.value.position
        }


        var needToContinue = true
        val compStartTime = new Date()
        var currentTime = new Date()

        //run the calculation for the specified time in miliseconds or just run it at once
        while((runDuration.isDefined && needToContinue &&
            compStartTime.getTime() + runDuration.get > currentTime.getTime())
            ||
            (runDuration.isEmpty && needToContinue)) {

            needToContinue = run(vertexViewPacks, edgeViewPacks)
            currentTime = new Date()
        }

        val toMove = ListBuffer[(VertexView, Point)]()
        vertexViewPacks.foreach{ vVPack =>
            toMove += ((vVPack.value, vVPack.currentPosition))
        }

        if(needToContinue) { //if the calculation is not finished yet
            val nextRoundAnimation = new Animation(runningAnimation, graphView.get.vertexViews, followingAnimation,
                redrawQuick, redrawQuick, runDuration)
            Animation.moveVertices(toMove, Some(nextRoundAnimation), redrawQuick, redrawQuick)

        } else {
            Animation.moveVertices(toMove, followingAnimation, redrawQuick, redrawQuick)
        }
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
    private def run(vertexViewPacks: ListBuffer[VertexViewPack], edgeViewPacks: ListBuffer[EdgeViewPack]): Boolean = {

        var repeat = true
        //while(repeat) {

            vertexViewPacks.foreach {pushed =>
                pushed.force = Vector(0, 0)

                //set repulsion by all other vertices
                vertexViewPacks.foreach {pushing =>
                    if (pushed.value.vertexModel ne pushing.value.vertexModel) {

                        //minus repulsion of vertices
                        val forceElimination = repulsion / (
                            scala.math.pow(pushed.currentPosition.x - pushing.currentPosition.x, 2) +
                                scala.math.pow(pushed.currentPosition.y - pushing.currentPosition.y, 2))
                        pushed.force = pushed.force +
                            (pushed.currentPosition.toVector - pushing.currentPosition.toVector) * forceElimination
                    }
                }
            }

            //set attraction by edges
            edgeViewPacks.foreach {edgeViewPack =>
                val origin = edgeViewPack.originVertexViewPack
                val destination = edgeViewPack.destinationVertexViewPack

                origin.force = origin.force +
                    (destination.currentPosition.toVector - origin.currentPosition.toVector) * attraction
                destination.force = destination.force +
                    (origin.currentPosition.toVector - destination.currentPosition.toVector) * attraction
            }

            var stabilization: Double = 0

            //move vertices by the calculated vertices
            vertexViewPacks.foreach { moved =>
                if (!moved.value.selected) {
                    moved.velocity = (moved.force + moved.velocity) / (vertexViewPacks.length - moved.value.edges.length)

                    stabilization += scala.math.abs(moved.velocity.x) + scala.math.abs(moved.velocity.y)
                    moved.currentPosition = moved.currentPosition + moved.velocity
                }
            }
            repeat = stabilization >= velocitiesStabilization
        repeat
        //}
    }
}
