package cz.payola.web.client.views.graph.algorithms

import collection.mutable.ListBuffer
import cz.payola.web.client.views.graph.{EdgeView, VertexView}
import cz.payola.web.client.views.{Vector, Point}

trait AlgorithmBase {

    def perform(vertexViews: ListBuffer[VertexView], edgeViews: ListBuffer[EdgeView])

    /**
      * Moves whole graph closer to the [0,0] coordinate, that it won't float somewhere away and makes it visible.
      * @param vViews
      */
    protected def moveGraphToUpperLeftCorner(vViews: ListBuffer[VertexView]) {
        var vector = Vector(Double.MaxValue, Double.MaxValue)
        //search for the minimum
        vViews.foreach{ v: VertexView =>
            if(v.position.x < vector.x) {
                vector = Vector(v.position.x, vector.y)
            }
            if(v.position.y < vector.y) {
                vector = Vector(vector.x, v.position.y)
            }
        }

        //move the graph...actually to the [50,50] coordinate, that no vertices are cut off by the screen edge
        vector = (vector)* (-1) + Vector(50, 50)

        vViews.foreach{ v: VertexView =>
            v.position = v.position + vector
        }
    }

    /**
      * Moves the vertices to a tree like structure. The first element of input is placed in the root located
      * in coordinates [0, 0]. All children of the root are vertices connected via an edge to the root. Every
      * level of the "tree" are vertices connected by one edge to a vertex in the previous level. Vertices
      * once placed to the structure are ignored for next levels construction.
      * @param vViews
      */
    protected def treeLikeVerticesPositioning(vViews: ListBuffer[VertexView]) {
        var levels = ListBuffer[ListBuffer[VertexView]]()
        var level = ListBuffer[VertexView]()
        var levelNext = ListBuffer[VertexView]()
        var alreadyOut = ListBuffer[VertexView]()

        level += vViews.head

        while(level.length != 0) {

            level.foreach{ l1: VertexView =>
                l1.edges.foreach{ e: EdgeView =>
                    if(e.originView.vertexModel eq l1.vertexModel) {
                        if(!existsVertex(e.destinationView, alreadyOut)
                            && !existsVertex(e.destinationView, levelNext) && !existsVertex(e.destinationView, level)) {
                            levelNext += e.destinationView
                            val i = 0
                        }
                    } else {
                        if(!existsVertex(e.originView, alreadyOut)
                            && !existsVertex(e.originView, levelNext) && !existsVertex(e.originView, level)) {
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



        val distance = 70
        var levelNum = 0
        var vertexNumInLevel = 0
        val lastLevelSize = levels.last.length
        levels.foreach{ elements =>

            vertexNumInLevel = 0
            val currentLevelSize = elements.length
            elements.foreach{ element =>

                element.position = Point(
                    scala.math.random/10 + (vertexNumInLevel * distance) + distance*(lastLevelSize - currentLevelSize)/2,
                    scala.math.random/10 + (levelNum * distance))
                vertexNumInLevel += 1
            }
            levelNum += 1
        }
    }

    /**
      * Rotates the whole graph around line x=y if the height of the graph if bigger than width
      * @param vViews
      */
    protected def flip(vViews: ListBuffer[VertexView]) {
        var maxX: Double = -1//Double.MinValue TODO Double.MinValue does some unexpected stuff in s2js transformation
        var minX: Double = Double.MaxValue
        var maxY: Double = -1//Double.MinValue
        var minY: Double = Double.MaxValue

        vViews.foreach{ v: VertexView =>
            if(v.position.x > maxX) {
                maxX = v.position.x
            } else if(v.position.x < minX) {
                minX = v.position.x
            }

            if(v.position.y > maxY) {
                maxY = v.position.y
            } else if(v.position.y < minY) {
                minY = v.position.y
            }
        }

        if(maxX - minX < maxY - minY) {
            vViews.foreach{ v: VertexView =>
                v.position = Point(v.position.y, v.position.x)
            }

            moveGraphToUpperLeftCorner(vViews)
        }
    }

    //TODO remove
    def existsVertex(whatToCheck: VertexView, whereToCheck: ListBuffer[VertexView]): Boolean = {
        whereToCheck.exists(element => element.vertexModel eq whatToCheck.vertexModel)
    }
}
