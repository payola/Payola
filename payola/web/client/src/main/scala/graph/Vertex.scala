package cz.payola.web.client.graph

import cz.payola.web.client.{Vector, Point}
import s2js.adapters.js.dom.CanvasRenderingContext2D
import Constants._

class Vertex(val id: Int, var position: Point, text: String, var neighbours: List[Vertex]) {
    var selected = false
    val information: Information = Information(text, position)

    def x: Double = {
        position.x
    }

    def y: Double = {
        position.y
    }

    def draw(context: CanvasRenderingContext2D) {
        drawRoundedRectangle(context,
            position.add(Vector(VertexWidth / -2, VertexHeight / -2)),
            VertexWidth, VertexHeight, VertexCornerRadius)

        var colorToUse: Color = Color(0, 0, 0, 0)
        if(selected) {
            colorToUse = ColorVertexHigh 
        } else if(neighbours.find(vertex => vertex.selected) == Some) {
            colorToUse = ColorVertexMedium
        } else if(true){ //TODO if no vertex in the graph is selected condition
            colorToUse = ColorVertexDefault
        } else {
            colorToUse = ColorVertexLow
        }

        context.fillStyle = colorToUse.toString
        context.fill()
        /*if(text != ""){
            //TODO uncomment the text if needed
            drawText(/*text + */" [" + position.x + ";" + position.y + "]",
                vertex.position, "12px Sans", "center")
        }*/
    }

    /**
      * Draws a rectangle with rounded corners, depending on the radius parameter to the input canvas context.
      */
    private def drawRoundedRectangle(context: CanvasRenderingContext2D, coord: Point, width: Double, height: Double, radius: Double) {
        //TODO move to parent Drawable class
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
        context.moveTo(aX, aY)

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
}
