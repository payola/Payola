package cz.payola.web.client.graph

import s2js.adapters.js.dom.CanvasRenderingContext2D
import Constants._

class Drawer(val context: CanvasRenderingContext2D) {
    def drawEdge(vertex1: Vertex, vertex2: Vertex, colorToUse: Color) {
        context.strokeStyle = colorToUse.toString
        context.lineWidth = EDGE_WIDTH
        context.beginPath()
        
        val x1 = vertex1.x
        val y1 = vertex1.y
        val x2 = vertex2.x
        val y2 = vertex2.y

        var ctrl1X: Double = 0
        var ctrl1Y: Double = 0
        var ctrl2X: Double = 0
        var ctrl2Y: Double = 0

        val diffX = scala.math.abs(x1 - x2)
        //diffX = diffX < 0 ? (diffX*(-1)) : diffX
        val diffY = scala.math.abs(y1 - y2)
        //diffY = diffY < 0 ? (diffY*(-1)) : diffY

        //quadrant of coordinate system: 1) right bottom 2) left bottom 3) left top 4) right top
        // TODO use an enum instead of integers.
        val quadrant = {
            //quadrant of destination
            if (x1 <= x2) {
                if (y1 <= y2) {
                    1
                } else {
                    4
                }
            } else {
                if (y1 <= y2) {
                    2
                } else {
                    3
                }
            }
        }

        if (diffX >= diffY) {
            //connecting left/right sides of vertices
            quadrant match {
                case 1 | 4 => //we are in (0, pi/4] or in (pi7/4, 2pi]
                    ctrl1X = x1 + diffX / EDGE_S_INDEX
                    ctrl1Y = y1
                    ctrl2X = x2 - diffX / EDGE_S_INDEX
                    ctrl2Y = y2
                case 2 | 3 => //we are in (pi3/4, pi] or in (pi, pi5/4]
                    ctrl1X = x1 - diffX / EDGE_S_INDEX
                    ctrl1Y = y1
                    ctrl2X = x2 + diffX / EDGE_S_INDEX
                    ctrl2Y = y2
            }
        } else {
            //connecting top/bottom sides of vertices
            quadrant match {
                case 1 | 2 => //we are in (pi/4, pi/2] or in (pi/2, pi3/4]
                    ctrl1X = x1
                    ctrl1Y = y1 + diffY / EDGE_S_INDEX
                    ctrl2X = x2
                    ctrl2Y = y2 - diffY / EDGE_S_INDEX
                case 3 | 4 => //we are in (pi5/4, pi3/2] or in (pi3/2, pi7/4]
                    ctrl1X = x1
                    ctrl1Y = y1 - diffY / EDGE_S_INDEX
                    ctrl2X = x2
                    ctrl2Y = y2 + diffY / EDGE_S_INDEX
            }
        }

        context.moveTo(x1, y1)
        context.bezierCurveTo(ctrl1X, ctrl1Y, ctrl2X, ctrl2Y, x2, y2)
        context.stroke()
    }

    def drawVertex(vertex: Vertex, text: String, colorToUse: Color) {
        //theory:
        //	context.quadraticCurveTo(
        //		bend X coord (control point), bend Y coord (control point),
        //		end point X coord, end point Y)

        //size of global.vertexRadius:drawEdge
        //	the bigger, the corners are rounder
        //	if x > Min(global.vertexWidth, global.vertexHeight) / 2, draws edges
        //		of the vertex over the original rectangle dimensions
        //	if x < 0, draws the arches mirrored to the edges of the original rectangle
        //		(in the mirrored quadrant)...ehm, looks interesting :-)

        
        // TODO refactor - extract method roundedRectangle (or some similar name) and use it in the drawVertex method.
        val x1 = vertex.x - VERTEX_WIDTH / 2
        val y1 = vertex.y - VERTEX_HEIGHT / 2

        context.beginPath()

        var aX = x1 + VERTEX_RADIUS
        var aY = y1
        context.moveTo(aX, aY)

        aX = x1
        aY = y1
        context.quadraticCurveTo(aX, aY, aX, aY + VERTEX_RADIUS) //upper left

        aX = x1
        aY = y1 + VERTEX_HEIGHT
        context.lineTo(aX, aY - VERTEX_RADIUS)
        context.quadraticCurveTo(aX, aY, aX + VERTEX_RADIUS, aY) //lower left


        aX = x1 + VERTEX_WIDTH
        aY = y1 + VERTEX_HEIGHT
        context.lineTo(aX - VERTEX_RADIUS, aY)
        context.quadraticCurveTo(aX, aY, aX, aY - VERTEX_RADIUS) //lower right

        aX = x1 + VERTEX_WIDTH
        aY = y1
        context.lineTo(aX, aY + VERTEX_RADIUS)
        context.quadraticCurveTo(aX, aY, aX - VERTEX_RADIUS, aY) //upper right

        context.closePath()

        context.fillStyle = colorToUse.toString
        context.fill()

        context.fillStyle = COLOR_TEXT.toString
        context.font = "18px Sans"
        context.textAlign = "center"
        context.fillText(text, vertex.x + TEXT_COORD_CORRECTION_X, vertex.y + TEXT_COORD_CORRECTION_Y)
    }

    def drawSelectionByRect(x1: Double, y1: Double, x2: Double, y2: Double, colorToUse: Color) {
        // TODO if the context supports it, use rectangle.
        context.strokeStyle = colorToUse.toString
        context.lineWidth = SELECT_LINE_WIDTH
        context.beginPath()
        context.moveTo(x1, y1)
        context.lineTo(x1, y2)
        context.lineTo(x2, y2)
        context.lineTo(x2, y1)
        context.lineTo(x1, y1)
        context.closePath()
        context.stroke()
    }

    def drawGraph(graph: List[Vertex]) {
        // Need to draw edges first, so that edges would not be drawn "over" vertices.
        graph.foreach { vertex =>
            vertex.neighbours.foreach { neighbourVertex =>
                // If the edge isn't already drawn, then draw it.
                if (vertex.id < neighbourVertex.id) {
                    val edgeColor = if (vertex.selected || neighbourVertex.selected) COLOR_EDGE_SELECT else COLOR_EDGE
                    drawEdge(vertex, neighbourVertex, edgeColor)
                }
            }
        }

        val somethingSelected = graph.exists(_.selected)
        graph.foreach { vertex =>
            val neighbourSelected = vertex.neighbours.exists(_.selected);
            if (vertex.selected) {
                drawVertex(vertex, vertex.text, COLOR_VERTEX_HIGH)
            } else if (neighbourSelected) {
                drawVertex(vertex, "", COLOR_VERTEX_MEDIUM)
            } else if (!somethingSelected) {
                drawVertex(vertex, vertex.text, COLOR_VERTEX)
            } else {
                // No neighbour nor this vertex is selected, but something else is.
                drawVertex(vertex, "", COLOR_VERTEX_LOW)
            }
        }
    }

    def clear(x: Double, y: Double, width: Double, height: Double) {
        context.fillStyle = COLOR_BACKGROUND.toString
        context.fillRect(x, y, width, height)
    }

    def redraw(graph: List[Vertex]) {
        clear(0, 0, 700, 700) //TODO podminene vykreslovani jen casti, ktere je nutne predelat
        drawGraph(graph)
    }
}
