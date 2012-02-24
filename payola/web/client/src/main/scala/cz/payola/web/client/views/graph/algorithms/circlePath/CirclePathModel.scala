package cz.payola.web.client.views.graph.algorithms.circlePath

import collection.mutable.ListBuffer
import cz.payola.web.client.views.graph.{EdgeView, VertexView}
import cz.payola.web.client.views.Point
import cz.payola.web.client.views.graph.algorithms.AlgorithmBase

class CirclePathModel extends AlgorithmBase
{
    def perform(vertexViews: ListBuffer[VertexView], edgeViews: ListBuffer[EdgeView]) {
        run(vertexViews)
        moveGraphToUpperLeftCorner(vertexViews)
        flip(vertexViews)
    }

    private def run(vertexViews: ListBuffer[VertexView]) {
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
                            val i = 0 //TODO bez nejakeho blabolu v podmince jeste s += to po prekladu nefunguje
                        }
                    } else {
                        if (!existsVertex(e.originView, alreadyOut)
                            && !existsVertex(e.originView, level2) && !existsVertex(e.originView, level1)) {
                            level2 += e.originView
                            val i = 0
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
}
