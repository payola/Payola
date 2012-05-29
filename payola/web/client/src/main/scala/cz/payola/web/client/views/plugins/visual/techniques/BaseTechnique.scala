package cz.payola.web.client.views.plugins.visual.techniques

import collection.mutable.ListBuffer
import cz.payola.web.client.views.plugins.visual.{VisualPlugin, Point, Vector}
import cz.payola.common.rdf.Graph
import s2js.adapters.js.dom.Element
import cz.payola.web.client.views.plugins.visual.animation.Animation
import cz.payola.web.client.views.plugins.visual.settings.components.visualsetup.VisualSetup
import cz.payola.web.client.views.plugins.visual.graph._
import s2js.adapters.js.browser.window

abstract class BaseTechnique(settings: VisualSetup) extends VisualPlugin(settings)
{

    private val treeVerticesDistance = 100

    private val circleLevelsDistance = 150

    /**
      * Calls initialization of the parent class and if the graph is not empty performs the
      * vertex positioning technique.
      * @param container where to visualise
      */
    override def init(container: Element) {
        super.init(container)
    }
    
    override def update(graph: Graph) {
        super.update(graph)
        if(!graphView.get.isEmpty) { // graphView != None because this call is after update(..)
            performPositioning(graphView.get)
        }
    }

    private def performPositioning(graphView: GraphView) {
        //TODO might be useful to somehow calculate the highest bottom (y-coordinate) of components in a row

        var firstAnimation: Animation[ListBuffer[(VertexView, Point)]] = null
        var isFirstAnimation = true
        var componentNumber = 1

        var previousComponent: Option[Component] = None

        graphView.components.foreach{ component =>

            if(isFirstAnimation) {
                firstAnimation = getTechniquePerformer(component, true)
            } else {
                firstAnimation.addFollowingAnimation(getTechniquePerformer(component, true))
            }

            val componentPositionDesc =
                new LocationDescriptor.ComponentPositionHelper(
                    componentNumber, graphView.components.length, previousComponent)

            val move = new Animation(
                Animation.moveComponent, (componentPositionDesc, component.vertexViews), None, redrawQuick, redraw,
                None)
            firstAnimation.addFollowingAnimation(move)

            isFirstAnimation = false
            componentNumber += 1
            previousComponent = Some(component)
        }
        firstAnimation.addFollowingAnimation(
            new Animation(Animation.emptyAnimation, false, None, graphView.fitCanvas, redraw, None))

        firstAnimation.run()
    }

    /**
      * Runs the vertex positioning algorithm and moves the vertices to "more suitable" positions.
      */
    protected def getTechniquePerformer(component: Component, animated: Boolean): Animation[ListBuffer[(VertexView, Point)]]

    /**
     * Moves the vertices to a tree like structure. The first element of input is placed in the root located
     * in coordinates [0, 0]. All children of the root are vertices connected via an edge to the root. Every
     * level of the "tree" are vertices connected by one edge to a vertex in the previous level. Vertices
     * in next level have set higher number to the y-coordinate. Vertices in a level have set x-coordinate
     * to appear in a line next to each other. Placed vertices are ignored for next levels construction.
     * @param vViews vertices to place in the "tree" structure
     * @param nextAnimation that will be launched after this operation is performed
     * @param quickDraw method to redraw the vertices quickly
     * @param finalDraw method to redraw the vertices after the last animation
     * @param animationStepLength defining this parameter with 0 makes the animation to perform the operation instantly
     *                            (skipping the animation)
     */
    def basicTreeStructure(vViews: ListBuffer[VertexView], nextAnimation: Option[Animation[_]], quickDraw: () => Unit,
        finalDraw: () => Unit, animationStepLength: Option[Int]): Animation[ListBuffer[(VertexView, Point)]] = {
        //TODO je tu nejaka chyba...testovaci graf se kresli spatne
        var levels = ListBuffer[ListBuffer[VertexView]]()
        var level = ListBuffer[VertexView]()
        var levelNext = ListBuffer[VertexView]()
        var alreadyOut = ListBuffer[VertexView]()

        level += vViews.head

        //create level structure
        while (level.length != 0) {

            level.foreach {l1: VertexView =>
                l1.edges.foreach {e: EdgeView =>
                    if (e.originView.vertexModel eq l1.vertexModel) {
                        if (!existsVertex(e.destinationView, alreadyOut) &&
                            !existsVertex(e.destinationView, levelNext) && !existsVertex(e.destinationView, level)) {

                            levelNext += e.destinationView
                        }
                    } else {
                        if (!existsVertex(e.originView, alreadyOut) &&
                            !existsVertex(e.originView, levelNext) && !existsVertex(e.originView, level)) {

                            levelNext += e.originView
                        }
                    }
                }
                alreadyOut += l1
            }

            levels += level
            level = ListBuffer[VertexView]()
            level = levelNext
            levelNext = ListBuffer[VertexView]()
        }

        val origin = vViews.head.position
        var levelNum = 0
        var vertexNumInLevel = 0
        val lastLevelSize = levels.last.length
        val toMove = ListBuffer[(VertexView, Point)]()

        //build structure of vertices and their destinations
        levels.foreach {elements =>

            vertexNumInLevel = 0
            val currentLevelSize = elements.length
            elements.foreach {element =>

                val destination = Point(/*scala.math.random / 10 +*/ origin.x +
                    (vertexNumInLevel * treeVerticesDistance) + treeVerticesDistance * (lastLevelSize - currentLevelSize) / 2,
                    /*scala.math.random / 10 +*/ origin.y + (levelNum * treeVerticesDistance))

                toMove += ((element, destination))

                vertexNumInLevel += 1
            }
            levelNum += 1
        }

        new Animation(Animation.moveVertices, toMove, nextAnimation, quickDraw, finalDraw, animationStepLength)
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
    def basicTreeCircledStructure(vViews: ListBuffer[VertexView], nextAnimation: Option[Animation[_]], quickDraw: () => Unit,
        finalDraw: () => Unit, animationStepLength: Option[Int]): Animation[ListBuffer[(VertexView, Point)]] = {
        //TODO je tu nejaka chyba...testovaci graf se kresli spatne

        var level1 = ListBuffer[(VertexView, Point)]()
        var level2 = ListBuffer[(VertexView, Point)]()
        var alreadyOut = ListBuffer[VertexView]()
        var levelNum = 0

        val toMove = ListBuffer[(VertexView, Point)]()

        level1 += ((vViews.head, vViews.head.position))
        //create level structure and for each of them calculate their destinations for moving
        while (level1.length != 0) {

            //place current level
            toMove ++=
                placeVerticesOnCircle(levelNum * 3, levelNum * circleLevelsDistance, vViews.head.position, level1)

            //get vertices in next level
            level1.foreach {l1 =>
                l1._1.edges.foreach {e: EdgeView =>
                    if (e.originView.vertexModel eq l1._1.vertexModel) {
                        if (!existsVertex(e.destinationView, alreadyOut)
                            && !existsVertexStruct(e.destinationView, level2) &&
                            !existsVertexStruct(e.destinationView, level1)) {

                            level2 += ((e.destinationView, e.destinationView.position))
                        }
                    } else {
                        if (!existsVertex(e.originView, alreadyOut)
                            && !existsVertexStruct(e.originView, level2) &&
                            !existsVertexStruct(e.originView, level1)) {

                            level2 += ((e.originView, e.originView.position))
                        }
                    }
                }
                alreadyOut += l1._1
            }

            //switch levels
            level1 = ListBuffer[(VertexView, Point)]()
            level1 = level2
            level2 = ListBuffer[(VertexView, Point)]()
            levelNum += 1
        }

        new Animation(Animation.moveVertices, toMove, nextAnimation, quickDraw, finalDraw, animationStepLength)
    }



    /**
     * Support method for basicTreeCircledStructure(..). This method takes all vertices in the vertexViews
     * container and puts them into one circle.
     * @param rotation modifies angle of the first placed vertex. This is an angle thats is between
     * vertexViews.head.point, [0, 0] and [1, 0].
     * @param radius distance of the vertices from center
     * @param center of the circle
     * @param vertexViews container of the vertices, that are placed to a circle
     */
    private def placeVerticesOnCircle(rotation: Double, radius: Double, center: Point,
        vertexViews: ListBuffer[(VertexView, Point)]): ListBuffer[(VertexView, Point)] = {

        val resultPositions = ListBuffer[(VertexView, Point)]()
        val angle = 360 / vertexViews.length

        var counter = 0
        var angleAct: Double = 0
        vertexViews.foreach { vView =>

            angleAct = angle * counter + rotation
            if (angleAct > 360) {
                angleAct -= 360
            }

            val angleActRad = angleAct * math.Pi / 180

            val x = radius * math.cos(angleActRad) + center.x
            val pom = math.pow(radius, 2) - math.pow(x - center.x, 2)
            val y1 = center.y - math.sqrt(pom)
            val y2 = center.y + math.sqrt(pom)

            val newPosition = if (pom < 0) { //should never happen...but just to be safe
                vView._1.position
            } else if (0 <= angleAct && angleAct < 90) {
                Point(x, y1)
            } else if (90 <= angleAct && angleAct < 180) {
                Point(x, y1)
            } else if (180 <= angleAct && angleAct < 270) {
                Point(x, y2)
            } else {//if (270 <= angleAct && angleAct < 360) {
                Point(x, y2)
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
    private def existsVertex(whatToCheck: VertexView, whereToCheck: ListBuffer[VertexView]): Boolean = {
        whereToCheck.exists(element => element.vertexModel eq whatToCheck.vertexModel)
    }

    /**
     * Function for checking whether a vertex exists in a container
     * @param whatToCheck vertex to search for
     * @param whereToCheck container to search in
     * @return true if the vertex is present in the container
     */
    private def existsVertexStruct(whatToCheck: VertexView, whereToCheck: ListBuffer[(VertexView, Point)]): Boolean = {
        whereToCheck.exists(element => element._1.vertexModel eq whatToCheck.vertexModel)
    }
}
