package cz.payola.web.client.views.graph

import s2js.adapters.js.dom.CanvasRenderingContext2D
import cz.payola.web.client.views.Constants._
import cz.payola.web.client.model.graph.Edge
import cz.payola.web.client.views.{Color, Point}

private object Quadrant
{
    val RightBottom = 1

    val LeftBottom = 2

    val LeftTop = 3

    val RightTop = 4
}

class EdgeView(val edgeModel: Edge, val originView: VertexView, val destinationView: VertexView) extends View {

    val information: InformationView = InformationView(edgeModel)

    def draw(context: CanvasRenderingContext2D, color: Color, positionCorrection: Point) {
        val A = originView.position
        val B = destinationView.position

        val ctrl1 = Point(0, 0)
        val ctrl2 = Point(0, 0)

        val diff = Point(scala.math.abs(A.x - B.x), scala.math.abs(A.y - B.y))

        //quadrant of coordinate system
        val quadrant = {
            //quadrant of destination
            if (A.x <= B.x) {
                if (A.y <= B.y) {
                    Quadrant.RightBottom
                } else {
                    Quadrant.RightTop
                }
            } else {
                if (A.y <= B.y) {
                    Quadrant.LeftBottom
                } else {
                    Quadrant.LeftTop
                }
            }
        }

        if (diff.x >= diff.y) {
            //connecting left/right sides of vertices
            quadrant match {
                case Quadrant.RightBottom | Quadrant.RightTop =>
                    //we are in (0, pi/4] or in (pi7/4, 2pi]
                    ctrl1.x = A.x + diff.x / EdgeSIndex
                    ctrl1.y = A.y
                    ctrl2.x = B.x - diff.x / EdgeSIndex
                    ctrl2.y = B.y
                case Quadrant.LeftBottom | Quadrant.LeftTop =>
                    //we are in (pi3/4, pi] or in (pi, pi5/4]
                    ctrl1.x = A.x - diff.x / EdgeSIndex
                    ctrl1.y = A.y
                    ctrl2.x = B.x + diff.x / EdgeSIndex
                    ctrl2.y = B.y
            }
        } else {
            //connecting top/bottom sides of vertices
            quadrant match {
                case Quadrant.RightBottom | Quadrant.LeftBottom =>
                    //we are in (pi/4, pi/2] or in (pi/2, pi3/4]
                    ctrl1.x = A.x
                    ctrl1.y = A.y + diff.y / EdgeSIndex
                    ctrl2.x = B.x
                    ctrl2.y = B.y - diff.y / EdgeSIndex
                case Quadrant.RightTop | Quadrant.LeftTop =>
                    //we are in (pi5/4, pi3/2] or in (pi3/2, pi7/4]
                    ctrl1.x = A.x
                    ctrl1.y = A.y - diff.y / EdgeSIndex
                    ctrl2.x = B.x
                    ctrl2.y = B.y + diff.y / EdgeSIndex
            }
        }

        if(color != null) {
            context.strokeStyle = color.toString
        } else if (originView.selected || destinationView.selected) {
            context.strokeStyle = ColorEdgeSelect.toString
        } else {
            context.strokeStyle = ColorEdge.toString
        }
        context.lineWidth = EdgeWidth
        
        val correction = if(positionCorrection != null) {
            positionCorrection
        } else {
            Point(0,0)
        }
        
        context.beginPath()
        context.moveTo(A.x + correction.x, A.y + correction.y)
        context.bezierCurveTo(ctrl1.x + correction.x, ctrl1.y + correction.y,
            ctrl2.x + correction.x, ctrl2.y + correction.y, B.x + correction.x, B.y + correction.y)
        //layerEdges.context.lineTo(B.x + correctionX, B.y + correctionY)
        context.stroke()
    }
}
