package cz.payola.web.client.views.plugins.visual.techniques

import collection.mutable.ListBuffer
import cz.payola.web.client.views.plugins.visual.{VisualPlugin, Point, Vector}
import cz.payola.web.client.views.plugins.visual.graph.{EdgeView, VertexView}

abstract class BaseTechnique extends VisualPlugin
{
    private val distance = 100

    def performTechnique()

    /**
      * Moves whole graph closer to the [0,0] coordinate, that it won't float somewhere away and makes it visible.
      * @param vViews
      */
    protected def moveGraphToUpperLeftCorner(vViews: ListBuffer[VertexView]) {
        var vector = Vector(Double.MaxValue, Double.MaxValue)
        //search for the minimum
        vViews.foreach {v: VertexView =>
            if (v.position.x < vector.x) {
                vector = Vector(v.position.x, vector.y)
            }
            if (v.position.y < vector.y) {
                vector = Vector(vector.x, v.position.y)
            }
        }

        //move the graph...actually to the [50,50] coordinate, that no vertices are cut off by the screen edge
        vector = (vector) * (-1) + Vector(50, 50)

        vViews.foreach {v: VertexView =>
            v.position = v.position + vector
        }
    }

    /**
      * Rotates the whole graph around line x=y if the height of the graph if bigger than width
      * @param vViews
      */
    protected def flip(vViews: ListBuffer[VertexView]) {
        var maxX: Double = 5.0//Double.MinValue
        var minX: Double = 5.0//Double.MaxValue
        var maxY: Double = 5.0//Double.MinValue
        var minY: Double = 5.0//Double.MaxValue

        vViews.foreach {v: VertexView =>
            if (v.position.x > maxX) {
                maxX = v.position.x
            } else if (v.position.x < minX) {
                minX = v.position.x
            }

            if (v.position.y > maxY) {
                maxY = v.position.y
            } else if (v.position.y < minY) {
                minY = v.position.y
            }
        }

        if (maxX - minX < maxY - minY) {
            vViews.foreach {v: VertexView =>
                v.position = Point(v.position.y, v.position.x)
            }

            moveGraphToUpperLeftCorner(vViews)
        }
    }

    /**
      * Moves the vertices to a tree like structure. The first element of input is placed in the root located
      * in coordinates [0, 0]. All children of the root are vertices connected via an edge to the root. Every
      * level of the "tree" are vertices connected by one edge to a vertex in the previous level. Vertices
      * once placed to the structure are ignored for next levels construction.
      * @param vViews
      */
    protected def basicTreeStructure(vViews: ListBuffer[VertexView]) {
        var levels = ListBuffer[ListBuffer[VertexView]]()
        var level = ListBuffer[VertexView]()
        var levelNext = ListBuffer[VertexView]()
        var alreadyOut = ListBuffer[VertexView]()

        level += vViews.head

        while (level.length != 0) {

            level.foreach {l1: VertexView =>
                l1.edges.foreach {e: EdgeView =>
                    if (e.originView.vertexModel eq l1.vertexModel) {
                        if (!existsVertex(e.destinationView, alreadyOut) &&
                            !existsVertex(e.destinationView, levelNext) && !existsVertex(e.destinationView, level)) {

                            levelNext += e.destinationView
                            val i = 0
                        }
                    } else {
                        if (!existsVertex(e.originView, alreadyOut) &&
                            !existsVertex(e.originView, levelNext) && !existsVertex(e.originView, level)) {

                            levelNext += e.originView
                            val i = 0
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


        var levelNum = 0
        var vertexNumInLevel = 0
        val lastLevelSize = levels.last.length
        levels.foreach {elements =>

            vertexNumInLevel = 0
            val currentLevelSize = elements.length
            elements.foreach {element =>

                element.position = Point(scala.math.random / 10 +
                        (vertexNumInLevel * distance) + distance * (lastLevelSize - currentLevelSize) / 2,
                    scala.math.random / 10 + (levelNum * distance))
                vertexNumInLevel += 1
            }
            levelNum += 1
        }
    }

    protected def basicTreeCircledStructure(vertexViews: ListBuffer[VertexView]) {
        var level1 = ListBuffer[VertexView]()
        var level2 = ListBuffer[VertexView]()
        var alreadyOut = ListBuffer[VertexView]()
        var levelNum = 0

        level1 += vertexViews.head

        while (level1.length != 0) {

            placeVerticesOnCircle(levelNum * 3, levelNum * 100, vertexViews.head.position, level1)

            level1.foreach {l1: VertexView =>
                l1.edges.foreach {e: EdgeView =>
                    if (e.originView.vertexModel eq l1.vertexModel) {
                        if (!existsVertex(e.destinationView, alreadyOut)
                            && !existsVertex(e.destinationView, level2) && !existsVertex(e.destinationView, level1)) {
                            level2 += e.destinationView
                        }
                    } else {
                        if (!existsVertex(e.originView, alreadyOut)
                            && !existsVertex(e.originView, level2) && !existsVertex(e.originView, level1)) {
                            level2 += e.originView
                        }
                    }
                }
                alreadyOut += l1
            }

            level1 = ListBuffer[VertexView]()
            level1 = level2
            level2 = ListBuffer[VertexView]()
            levelNum += 1
        }
    }

    private def placeVerticesOnCircle(rotation: Double, radius: Double, center: Point,
        vertexViews: ListBuffer[VertexView]) {
        val angle = 360 / vertexViews.length

        var counter = 0
        var angleAct: Double = 0
        var x: Double = 0
        var D: Double = 0
        var y1: Double = 0
        var y2: Double = 0
        vertexViews.foreach {vertexView =>

            angleAct = angle * counter + rotation
            if (angleAct > 360) {
                angleAct -= 360
            }
            x = center.x + radius * scala.math.cos(angleAct * scala.math.Pi / 180) //conversion to radians
            if (center.x - x <= radius) {
                D = scala.math.sqrt(-scala.math.pow(center.x, 2) + 2 * center.x +
                    scala.math.pow(radius, 2) - scala.math.pow(x, 2))
                y1 = center.y - D
                y2 = center.y + D

                if (0 <= angleAct && angleAct < 90) {
                    vertexView.position = Point(x, y1)
                } else if (90 <= angleAct && angleAct < 180) {
                    vertexView.position = Point(x, y1)
                } else if (180 <= angleAct && angleAct < 270) {
                    vertexView.position = Point(x, y2)
                } else if (270 <= angleAct && angleAct < 360) {
                    vertexView.position = Point(x, y2)
                }
            }


            counter += 1
        }
    }

    def existsVertex(whatToCheck: VertexView, whereToCheck: ListBuffer[VertexView]): Boolean = {
        whereToCheck.exists(element => element.vertexModel eq whatToCheck.vertexModel)
    }
}
