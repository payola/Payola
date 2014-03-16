package cz.payola.web.client.views.graph.visual.techniques.gravity

import collection.mutable.ListBuffer
import s2js.adapters.js.Date
import cz.payola.web.client.views.graph.visual.graph._
import cz.payola.web.client.views.graph.visual.animation.Animation
import cz.payola.web.client.views.graph.visual.techniques.BaseTechnique
import cz.payola.web.client.views.algebra._
import cz.payola.web.client.views.graph.visual.graph.positioning.GraphPositionHelper
import cz.payola.web.client.models.PrefixApplier

/**
 * Visual plug-in technique that places the vertices based on their edges.
 * Every vertex causes a negative force on every other vertex, that makes
 * vertices push each other away. Every edge causes a positive force on
 * both vertices, that share the edge, that makes vertices push each other
 * closer. The final positions of the vertices is reached when all vertices
 * have "small enough" velocity.
 */
class GravityTechnique(prefixApplier: Option[PrefixApplier]) extends BaseTechnique("Gravity Visualization", prefixApplier)
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
    private val velocitiesStabilization = 3

    protected def getTechniquePerformer(component: Component, animate: Boolean): Animation[_] = {
        if (animate) {
            animationStopForced = false
            val animationOfThis = new Animation(
                runningAnimation, component, None, redrawQuick, redrawQuick, Some(70))

            val graphCenterCorrector = new GraphPositionHelper(() => topLayer.size, component.getCenter)
            val centeringAnimation = new Animation(Animation.moveGraphByFunction,
                (graphCenterCorrector, component.vertexViewElements), Some(animationOfThis), redrawQuick, redraw, None)

            new Animation(basicTreeStructure, component.vertexViewElements, Some(centeringAnimation),
                redrawQuick, redraw, None)
        } else {
            val animationOfThis = new Animation(
                runningAnimation, component, None, redrawQuick, redrawQuick, Some(0))

            val graphCenterCorrector = new GraphPositionHelper(() => topLayer.size, component.getCenter)
            val centeringAnimation = new Animation(Animation.moveGraphByFunction,
                (graphCenterCorrector, component.vertexViewElements), Some(animationOfThis), redrawQuick, redraw,
                Some(0))

            new Animation(basicTreeStructure, component.vertexViewElements, Some(centeringAnimation), redrawQuick, redraw, Some(0))
        }
    }

    private def runningAnimation(componentToAnimate: Component, followingAnimation: Option[Animation[_]],
        redrawQuick: () => Unit, redrawFinal: () => Unit, animationStepLength: Option[Int]) {

        val vertexViewPacks = buildVertexViewsWorkingStructure(componentToAnimate.vertexViewElements)
        val edgeViewPacks = buildEdgeViewsWorkingStructure(vertexViewPacks, componentToAnimate.edgeViews)

        vertexViewPacks.foreach { vPack =>
            vPack.currentPosition = vPack.value.position
        }


        var needToContinue = true
        val compStartTime = new Date()
        var currentTime = new Date()

        //run the calculation for the specified time in miliseconds
        // or just run it at once (if the animation step length is 0 or not defined)
        while (((animationStepLength.isDefined && needToContinue &&
            compStartTime.getTime + animationStepLength.get > currentTime.getTime)
            ||
            (animationStepLength.isDefined && animationStepLength.get == 0 && needToContinue)
            ||
            (animationStepLength.isEmpty && needToContinue)) && !animationStopForced) {

            needToContinue = run(vertexViewPacks, edgeViewPacks)
            currentTime = new Date()
        }

        val toMove = ListBuffer[(VertexViewElement, Point2D)]()
        vertexViewPacks.foreach { vVPack =>
            toMove += ((vVPack.value, vVPack.currentPosition))
        }

        //create animation to center the graph
        val graphCenterCorrector = new GraphPositionHelper(() => topLayer.size, componentToAnimate.getCenter)
        val centeringAnimation = new Animation(Animation.moveGraphByFunction,
            (graphCenterCorrector, componentToAnimate.vertexViewElements), followingAnimation, redrawQuick, redraw, None)

        val moveVerticesAnimation =
            new Animation(Animation.moveVertices, toMove, Some(centeringAnimation), redrawQuick, redrawQuick,
                animationStepLength)
        if (needToContinue && !animationStopForced) {
            //if the calculation is not finished yet

            //center the graph
            val graphCenterCorrector = new GraphPositionHelper(() => topLayer.size, componentToAnimate.getCenter)
            val nextRoundAnimation = new Animation(Animation.moveGraphByFunction,
                    (graphCenterCorrector, componentToAnimate.vertexViewElements), None, redrawQuick, redraw, None)
            //add next round of gravity animation
            nextRoundAnimation.addFollowingAnimation(
                new Animation(runningAnimation, componentToAnimate, followingAnimation,
                redrawQuick, redrawQuick, animationStepLength))
            moveVerticesAnimation.setFollowingAnimation(nextRoundAnimation)
            moveVerticesAnimation.run()
        } else {

            moveVerticesAnimation.run()
        }
    }

    /**
     * Constructus a structure of vertexViewPacks, that the gravity algorithm works with.
     * @param vertices to create vertexViewPacks from
     * @return created vertexViewPacks
     */
    private def buildVertexViewsWorkingStructure(vertices: ListBuffer[VertexViewElement]): ListBuffer[VertexViewPack] = {
        var workingStructure = ListBuffer[VertexViewPack]()

        vertices.foreach { vertex =>
            workingStructure += new VertexViewPack(vertex)
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
        edgeViews.foreach { view =>
            val origin = vertexViewPacks.find { element =>
                element.value.represents(view.originView.getFirstContainedVertex)
            }
            val destination = vertexViewPacks.find { element =>
                element.value.represents(view.destinationView.getFirstContainedVertex)
            }
            if(!workingStructure.exists{ edgePack =>
                origin.get.value.represents(
                    edgePack.value.edgeModel.origin) && destination.get.value.represents(
                    edgePack.value.edgeModel.destination)
            }) {
                workingStructure += new EdgeViewPack(view, origin.get, destination.get)
            }
        }

        workingStructure
    }

    /**
     * Gravity model algorithm. Use it carefully, the computation complexity is quite high
     * (vertices^2 + edges + vertices) * "something strange".
     * The main loop ends if sum of velocities of vertices is less than 0.5. The inner loops compute forces effecting
     * vertices. Vertices push away
     * each other and their edges push them together. In the first loop are computed repulsions. In the
     * second are computed attractions. And in the last loop are the forces applied.
     * @param vertexViewPacks
     * @param edgeViewPacks
     */
    private def run(vertexViewPacks: ListBuffer[VertexViewPack], edgeViewPacks: ListBuffer[EdgeViewPack]): Boolean = {
        vertexViewPacks.foreach { pushed =>
            pushed.force = Vector2D(0, 0)

            //set repulsion by all other vertices
            vertexViewPacks.foreach { pushing =>
                if (!pushed.value.represents(pushing.value.getFirstContainedVertex)) {

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
        edgeViewPacks.foreach { edgeViewPack =>
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
            if (!moved.value.isSelected) {
                moved.velocity = (moved.force + moved.velocity) / (vertexViewPacks.length - moved.value.edges.length)

                stabilization += scala.math.abs(moved.velocity.x) + scala.math.abs(moved.velocity.y)
                moved.currentPosition = moved.currentPosition + moved.velocity
            }
        }
        stabilization >= velocitiesStabilization
    }
}
