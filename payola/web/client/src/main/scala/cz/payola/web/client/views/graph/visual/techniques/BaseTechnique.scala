package cz.payola.web.client.views.graph.visual.techniques

import collection.mutable.ListBuffer
import cz.payola.web.client.views.algebra._
import cz.payola.web.client.views.graph.visual.VisualPluginView
import cz.payola.common.rdf._
import cz.payola.web.client.views.graph.visual.graph._
import cz.payola.web.client.views.graph.visual.graph.positioning._
import cz.payola.web.client.views.graph.visual.animation._
import cz.payola.web.client.models.PrefixApplier

abstract class BaseTechnique(name: String, prefixApplier: Option[PrefixApplier]) extends VisualPluginView(name, prefixApplier)
{
    private val treeVerticesDistance = 100

    private val circleLevelsDistance = 150

    override def setMainVertex(vertex: Vertex) {
        graphView.foreach{ graph =>
            graph.putVertexToTop(vertex)
            performPositioning(graph)
        }
    }

    override def updateGraph(graph: Option[Graph], contractLiterals: Boolean = true) {
        super.updateGraph(graph, contractLiterals)
    }

    override def drawGraph() {
        graphView.foreach(performPositioning(_))
    }

    private def performPositioning(graphView: GraphView) {

        var firstAnimation: Option[Animation[_]] = None

        var previousComponent: Option[Component] = None

        graphView.components.foreach { component =>

            if (firstAnimation.isEmpty) {
                firstAnimation = Some(getTechniquePerformer(component, true))
            } else {
                firstAnimation.get.addFollowingAnimation(getTechniquePerformer(component, true))
            }

            firstAnimation.get.addFollowingAnimation(new Animation(
                Animation.flipGraph, ((new GraphCenterHelper(graphView.getGraphCenter), component.vertexViewElements)), None,
                redrawQuick, redraw, None))

            if (graphView.components.length != 1) {
                val componentPositionDesc = new ComponentPositionHelper(() => topLayer.size,
                    component.getCenter, previousComponent)

                firstAnimation.get.addFollowingAnimation(new Animation(
                    Animation.moveGraphByFunction, (componentPositionDesc, component.vertexViewElements), None, redrawQuick,
                    redraw, None))
            }

            previousComponent = Some(component)
        }
        if (firstAnimation.isDefined) {
            //finally move the whole graph to the center of the window
            val graphCenterCorrector = new GraphPositionHelper(() => topLayer.size, graphView.getGraphCenter)
            firstAnimation.get.addFollowingAnimation(
                new Animation(Animation.moveGraphByFunction,
                    (graphCenterCorrector, graphView.getAllVertices), None, redrawQuick, redraw, None))

            //disable animationStop button
            firstAnimation.get.addFollowingAnimation(new Animation(
                Animation.emptyAnimation, new AfterAnimationWithParams(animationStopButton.setIsEnabled, false), None,
                redrawQuick, redraw, None))

            animationStopButton.setIsEnabled(true)
            firstAnimation.get.run()
        }
    }

    /**
     * Runs the vertex positioning algorithm and moves the vertices to "more suitable" positions.
     */
    protected def getTechniquePerformer(component: Component,
        animated: Boolean): Animation[_]

    /**
     * Moves the vertices to a tree like structure. The first element of input is placed in the root located
     * in coordinates [0, 0]. All children of the root are vertices connected via an edge to the root. Every
     * level of the "tree" are vertices connected by one edge to a vertex in the previous level. Vertices
     * in next level have set higher number to the y-coordinate. Vertices in a level have set x-coordinate
     * to appear in a line next to each other. Placed vertices are ignored for next levels construction.
     * @param vertexElements vertices to place in the "tree" structure
     * @param nextAnimation that will be launched after this operation is performed
     * @param quickDraw method to redraw the vertices quickly
     * @param finalDraw method to redraw the vertices after the last animation
     * @param animationStepLength defining this parameter with 0 makes the animation to perform the operation instantly
     *                            (skipping the animation)
     */
    def basicTreeStructure(vertexElements: ListBuffer[VertexViewElement], nextAnimation: Option[Animation[_]],
        quickDraw: () => Unit, finalDraw: () => Unit, animationStepLength: Option[Int]) {

        var levels = ListBuffer[ListBuffer[VertexViewElement]]()
        var level = ListBuffer[VertexViewElement]()
        var levelNext = ListBuffer[VertexViewElement]()
        var alreadyOut = ListBuffer[VertexViewElement]()
        val availableGroups = vertexElements.filter(_.isInstanceOf[VertexViewGroup]).map(_.asInstanceOf[VertexViewGroup])

        level += vertexElements.head

        //create level structure
        while (level.length != 0) {

            level.foreach { l1: VertexViewElement =>
                l1.edges.foreach { e: EdgeView =>
                    if (e.originView.represents(l1.getFirstContainedVertex)) {
                        val toAdd = availableGroups.find(_.contains(e.destinationView)).getOrElse(e.destinationView)
                        if (!existsVertex(toAdd, alreadyOut) &&
                            !existsVertex(toAdd, levelNext) && !existsVertex(toAdd, level)) {

                            levelNext += toAdd
                        }
                    } else {
                        val toAdd = availableGroups.find(_.contains(e.originView)).getOrElse(e.originView)
                        if (!existsVertex(toAdd, alreadyOut) &&
                            !existsVertex(toAdd, levelNext) && !existsVertex(toAdd, level)) {

                            levelNext += toAdd
                        }
                    }
                }
                alreadyOut += l1
            }

            levels += level
            level = ListBuffer[VertexViewElement]()
            level = levelNext
            levelNext = ListBuffer[VertexViewElement]()
        }

        val origin = vertexElements.head.position
        var levelNum = 0
        var vertexNumInLevel = 0
        val lastLevelSize = levels.last.length
        val toMove = ListBuffer[(VertexViewElement, Point2D)]()

        //build structure of vertices and their destinations
        levels.foreach { elements =>

            vertexNumInLevel = 0
            val currentLevelSize = elements.length
            elements.foreach { element =>

                val destination = Point2D(origin.x +
                    (vertexNumInLevel * treeVerticesDistance) + treeVerticesDistance * (lastLevelSize -
                    currentLevelSize) / 2,
                    origin.y + (levelNum * treeVerticesDistance))

                toMove += ((element, destination))

                vertexNumInLevel += 1
            }
            levelNum += 1
        }

        Animation.moveVertices(toMove, nextAnimation, quickDraw, finalDraw, animationStepLength)
    }

    /**
     * Moves the vertices to a tree like structure. The first element of input is placed in the root located
     * in coordinates [0, 0]. All children of the root are vertices connected via an edge to the root. Every
     * level of the "tree" are vertices connected by one edge to a vertex in the previous level. Vertices
     * in next level are placed to a circle with a bigger diameter than the vertices in the previous level.
     * Vertices are placed in the circle regularly, that the vertices have the same distances between each other.
     * Placed vertices are ignored for next levels construction.
     * @param vViews vertices to place in the "tree" structure
     * @param nextAnimation that will be launched after this operation is performed
     * @param quickDraw method to redraw the vertices quickly
     * @param finalDraw method to redraw the vertices after the last animation
     * @param animationStepLength defining this parameter with 0 makes the animation to perform the operation instantly
     *                            (skipping the animation)
     */
    def basicTreeCircledStructure(vViews: ListBuffer[VertexViewElement], nextAnimation: Option[Animation[_]],
        quickDraw: () => Unit,
        finalDraw: () => Unit, animationStepLength: Option[Int]): Animation[ListBuffer[(VertexViewElement, Point2D)]] = {
        var level1 = ListBuffer[(VertexViewElement, Point2D)]()
        var level2 = ListBuffer[(VertexViewElement, Point2D)]()
        var alreadyOut = ListBuffer[VertexViewElement]()
        var levelNum = 0
        val availableGroups = vViews.filter(_.isInstanceOf[VertexViewGroup]).map(_.asInstanceOf[VertexViewGroup])

        val toMove = ListBuffer[(VertexViewElement, Point2D)]()

        level1 += ((vViews.head, vViews.head.position))
        //create level structure and for each of them calculate their destinations for moving
        while (level1.length != 0) {

            //place current level
            toMove ++=
                placeVerticesOnCircle(levelNum * 3, levelNum * circleLevelsDistance, vViews.head.position, level1)

            //get vertices in next level
            level1.foreach { l1 =>
                l1._1.edges.foreach { e: EdgeView =>
                    if (e.originView.represents(l1._1.getFirstContainedVertex)) {
                        val toAdd = availableGroups.find(_.contains(e.destinationView)).getOrElse(e.destinationView)
                        if (!existsVertex(toAdd, alreadyOut)
                            && !existsVertexStruct(toAdd, level2) &&
                            !existsVertexStruct(toAdd, level1)) {

                            level2 += ((toAdd, toAdd.position))
                        }
                    } else {
                        val toAdd = availableGroups.find(_.contains(e.originView)).getOrElse(e.originView)
                        if (!existsVertex(toAdd, alreadyOut)
                            && !existsVertexStruct(toAdd, level2) &&
                            !existsVertexStruct(toAdd, level1)) {

                            level2 += ((toAdd, toAdd.position))
                        }
                    }
                }
                alreadyOut += l1._1
            }

            //switch levels
            level1 = ListBuffer[(VertexViewElement, Point2D)]()
            level1 = level2
            level2 = ListBuffer[(VertexViewElement, Point2D)]()
            levelNum += 1
        }

        new Animation(Animation.moveVertices, toMove, nextAnimation, quickDraw, finalDraw, animationStepLength)
    }

    /**
     * Support method for basicTreeCircledStructure(..). This method takes all vertices in the vertexElements
     * container and puts them into one circle.
     * @param rotation modifies angle of the first placed vertex. This is an angle thats is between
     *                 vertexElements.head.point, [0, 0] and [1, 0].
     * @param radius distance of the vertices from center
     * @param center of the circle
     * @param vertexElements container of the vertices, that are placed to a circle
     */
    private def placeVerticesOnCircle(rotation: Double, radius: Double, center: Point2D,
        vertexElements: ListBuffer[(VertexViewElement, Point2D)]): ListBuffer[(VertexViewElement, Point2D)] = {
        val resultPositions = ListBuffer[(VertexViewElement, Point2D)]()
        val angle = 360 / vertexElements.length

        var counter = 0
        var angleAct: Double = 0
        vertexElements.foreach { vView =>

            angleAct = angle * counter + rotation
            if (angleAct > 360) {
                angleAct -= 360
            }

            val angleActRad = angleAct * math.Pi / 180

            val x = radius * math.cos(angleActRad) + center.x
            val pom = math.pow(radius, 2) - math.pow(x - center.x, 2)
            val y1 = center.y - math.sqrt(pom)
            val y2 = center.y + math.sqrt(pom)

            val newPosition = if (pom < 0) {
                //should never happen...but just to be safe
                vView._1.position
            } else if (0 <= angleAct && angleAct < 90) {
                Point2D(x, y1)
            } else if (90 <= angleAct && angleAct < 180) {
                Point2D(x, y1)
            } else if (180 <= angleAct && angleAct < 270) {
                Point2D(x, y2)
            } else {
                //if (270 <= angleAct && angleAct < 360) {
                Point2D(x, y2)
            }

            resultPositions += ((vView._1, newPosition))

            counter += 1
        }

        resultPositions
    }

    /**
     * Function for checking whether a vertex exists in a container
     * @param whatToCheck vertex to search for
     * @param whereToCheck container to search in
     * @return true if the vertex is present in the container
     */
    private def existsVertex(whatToCheck: VertexViewElement, whereToCheck: ListBuffer[VertexViewElement]): Boolean = {
        whereToCheck.exists(element => element.represents(whatToCheck.getFirstContainedVertex))
    }

    /**
     * Function for checking whether a vertex exists in a container
     * @param whatToCheck vertex to search for
     * @param whereToCheck container to search in
     * @return true if the vertex is present in the container
     */
    private def existsVertexStruct(whatToCheck: VertexViewElement, whereToCheck: ListBuffer[(VertexViewElement, Point2D)]): Boolean = {
        whereToCheck.exists(element => element._1.represents(whatToCheck.getFirstContainedVertex))
    }
}
