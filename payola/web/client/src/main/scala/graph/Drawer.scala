package cz.payola.web.client.graph

import cz.payola.web.client.{Layer, Vector, Point}
import s2js.adapters.js.dom.CanvasRenderingContext2D
import Constants._

object Quadrant { //TODO is this correct enumeration?!?!
    val RightBottom = 1
    val LeftBottom = 2
    val LeftTop = 3
    val RightTop = 4
}

class Drawer(val layerEdges: Layer, val layerVertices: Layer, val layerText: Layer) {

    /**
     * Draws to the Context an edge (a line) connecting the two vertices.
     */
    private def drawEdge(vertexA: Vertex, vertexB: Vertex, colorToUse: Color) {
        layerEdges.context.strokeStyle = colorToUse.toString
        layerEdges.context.lineWidth = EdgeWidth
        layerEdges.context.beginPath()

        val A = vertexA.position
        val B = vertexB.position

        val ctrl1 = Point.Zero
        val ctrl2 = Point.Zero

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
                case Quadrant.RightBottom | Quadrant.RightTop => //we are in (0, pi/4] or in (pi7/4, 2pi]
                    ctrl1.x = A.x + diff.x / EdgeSIndex
                    ctrl1.y = A.y
                    ctrl2.x = B.x - diff.x / EdgeSIndex
                    ctrl2.y = B.y
                case Quadrant.LeftBottom | Quadrant.LeftTop => //we are in (pi3/4, pi] or in (pi, pi5/4]
                    ctrl1.x = A.x - diff.x / EdgeSIndex
                    ctrl1.y = A.y
                    ctrl2.x = B.x + diff.x / EdgeSIndex
                    ctrl2.y = B.y
            }
        } else {
            //connecting top/bottom sides of vertices
            quadrant match {
                case Quadrant.RightBottom | Quadrant.RightTop => //we are in (pi/4, pi/2] or in (pi/2, pi3/4]
                    ctrl1.x = A.x
                    ctrl1.y = A.y + diff.y / EdgeSIndex
                    ctrl2.x = B.x
                    ctrl2.y = B.y - diff.y / EdgeSIndex
                case Quadrant.LeftBottom | Quadrant.LeftTop => //we are in (pi5/4, pi3/2] or in (pi3/2, pi7/4]
                    ctrl1.x = A.x
                    ctrl1.y = A.y - diff.y / EdgeSIndex
                    ctrl2.x = B.x
                    ctrl2.y = B.y + diff.y / EdgeSIndex
            }
        }

        layerEdges.context.moveTo(A.x, A.y)
        layerEdges.context.bezierCurveTo(ctrl1.x, ctrl1.y, ctrl2.x, ctrl2.y, B.x, B.y)
        layerEdges.context.stroke()
    }

    private  def drawVertex(vertex: Vertex, text: String, colorToUse: Color) {
        drawRoundedRectangle(layerVertices.context,
            vertex.position.add(Vector(VertexWidth / 2, VertexHeight / 2)), //TODO check if this does NOT change the actual coordinates
            VertexWidth, VertexHeight, VertexCornerRadius);
        layerVertices.context.fillStyle = colorToUse.toString
        layerVertices.context.fill()

        drawText(text, vertex.position, "18px Sans", "center");
    }
    
    private def drawText(text: String, position: Point, textAlign: String, textFont: String) {
        layerText.context.fillStyle = ColorText.toString
        layerText.context.font = textFont
        layerText.context.textAlign = textAlign
        layerText.context.fillText(text, position.x + TextCoordCorrectionX, position.y + TextCoordCorrectionY)
    }

    /**
     * Draws a rectangle with rounded corners, depending on the radius parameter to the input canvas context.
     */
    private def drawRoundedRectangle(context: CanvasRenderingContext2D, coord: Point, width: Double, height: Double, radius: Double) {
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

        context.beginPath()

        var aX = coord.x + radius
        var aY = coord.y
        layerEdges.context.moveTo(aX, aY)

        aX = coord.x
        aY = coord.y
        context.quadraticCurveTo(aX, aY, aX, aY + radius) //upper left corner

        aX = coord.x
        aY = coord.y + height
        context.lineTo(aX, aY - radius)
        context.quadraticCurveTo(aX, aY, aX + radius, aY) //lower left corner


        aX = coord.x + width
        aY = coord.y + height
        context.lineTo(aX - radius, aY)
        context.quadraticCurveTo(aX, aY, aX, aY - radius) //lower right corner

        aX = coord.x + width
        aY = coord.y
        context.lineTo(aX, aY + radius)
        context.quadraticCurveTo(aX, aY, aX - radius, aY) //upper right corner

        context.closePath()
    }

    def drawSelectionByRect(origin: Point, direction: Point, colorToUse: Color) {
        //TODO for the reader, please delete one of the rectangle drawing approaches (writer of the code likes the second one, but somebody (ehm) does not)
        layerEdges.context.strokeStyle = colorToUse.toString
        layerEdges.context.lineWidth = SelectLineWidth

        //using context.rect(..) is more readable, but the content of the call may be quite confusing
        layerEdges.context.rect(scala.math.min(origin.x, direction.x), scala.math.min(origin.y, direction.y),
            scala.math.abs(origin.x - direction.x), scala.math.abs(origin.y - direction.y))
        /*
        using the context.moveTo(..) and context.lineTo(..) is quite unreadable, but does not contain the math
        Problem is when the DIRECTION point may not be not in the lower right quadrant from the ORIGIN; context.rect(..)
        requires x and y to be the topmost and the leftmost coordinates.
        context.beginPath()
        context.moveTo(origin.x, origin.y)
        context.lineTo(origin.x, direction.y)
        context.lineTo(direction.x, direction.y)
        context.lineTo(direction.x, origin.y)
        context.lineTo(origin.x, origin.y)
        context.closePath()*/
        layerEdges.context.stroke()
    }

    def drawGraph(graph: Graph) {
        // Need to draw edges first, so that edges would not be drawn "over" vertices.
        graph.getGraph.foreach { (vertex: Vertex) =>
            vertex.neighbours.foreach { neighbourVertex =>
                // If the edge isn't already drawn, then draw it.
                if (vertex.id < neighbourVertex.id) {
                    val edgeColor = if (vertex.selected || neighbourVertex.selected) ColorEdgeSelect else ColorEdge
                    drawEdge(vertex, neighbourVertex, edgeColor)
                }
            }
        }

        val somethingSelected = graph.getGraph.exists(_.selected)
        graph.getGraph.foreach { vertex: Vertex =>
            val neighbourSelected = vertex.neighbours.exists(_.selected);
            if (vertex.selected) {
                drawVertex(vertex, vertex.text, ColorVertexHigh)
            } else if (neighbourSelected) {
                drawVertex(vertex, "", ColorVertexMedium)
            } else if (!somethingSelected) {
                drawVertex(vertex, vertex.text, ColorVertexDefault)
            } else {
                // No neighbour nor this vertex is selected, but something else is.
                drawVertex(vertex, "", ColorVertexLow)
            }
        }
    }

    def clear(context: CanvasRenderingContext2D, x: Double, y: Double, width: Double, height: Double) {
        context.fillStyle = ColorBackground.toString
        context.fillRect(x, y, width, height)
    }

    def redraw(graph: Graph) {
        //TODO conditional redrawing of "redraw-required-sections"
        clear(layerEdges.context, 0, 0, layerEdges.getWidth, layerEdges.getHeight)
        clear(layerVertices.context, 0, 0, layerVertices.getWidth, layerVertices.getHeight)
        clear(layerText.context, 0, 0, layerText.getWidth, layerText.getWidth)
        drawGraph(graph)
    }
}
