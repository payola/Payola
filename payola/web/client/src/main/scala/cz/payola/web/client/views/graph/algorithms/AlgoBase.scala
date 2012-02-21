package cz.payola.web.client.views.graph.algorithms

import collection.mutable.ListBuffer
import cz.payola.web.client.views.graph.{EdgeView, VertexView}
import cz.payola.web.client.views.{Vector, Point}

trait AlgoBase {

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
        val spaceBetweenVertices = 50
        var level1 = ListBuffer[VertexView]()
        var level2 = ListBuffer[VertexView]()
        var alreadyOut = ListBuffer[VertexView]()
        var levelNum = 0
        var vertexNumInLevel = 0

        level1 += vViews.head

        while(level1.length != 0) {
            vertexNumInLevel = 0

            level1.foreach{ l1: VertexView =>
                l1.edges.foreach{ e: EdgeView =>
                    if(e.originView.vertexModel.uri == l1.vertexModel.uri) {
                        if(!existsVertex(e.destinationView, alreadyOut)
                            && !existsVertex(e.destinationView, level2) && !existsVertex(e.destinationView, level1)) {
                            level2 += e.destinationView
                            val i = 0
                        }
                    } else {
                        if(!existsVertex(e.originView, alreadyOut)
                            && !existsVertex(e.originView, level2) && !existsVertex(e.originView, level1)) {
                            level2 += e.originView
                            val i = 0
                        }
                    }
                }

                l1.position = Point(scala.math.random/10 + (vertexNumInLevel * spaceBetweenVertices),
                    scala.math.random/10 + (levelNum * spaceBetweenVertices))
                alreadyOut += l1
                vertexNumInLevel += 1
            }

            level1 = ListBuffer[VertexView]()
            level1 = level2
            level2 = ListBuffer[VertexView]()
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

    def existsVertex(whatToCheck: VertexView, whereToCheck: ListBuffer[VertexView]): Boolean = {
        whereToCheck.exists(element => element.vertexModel.uri == whatToCheck.vertexModel.uri)
    }
}
