package cz.payola.web.client.views.graph

import s2js.adapters.js.dom.CanvasRenderingContext2D
import cz.payola.web.client.views.{Color, Vector, Point}

trait View {
    def draw(context: CanvasRenderingContext2D, color: Color, position: Point)

    /**
      * Draws a rectangle with rounded corners, depending on the radius parameter to the input canvas context.
      */
    protected def drawRoundedRectangle(context: CanvasRenderingContext2D, position: Point, size: Vector, radius: Double) {
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

        var aX = position.x + radius
        var aY = position.y
        context.moveTo(aX, aY)

        aX = position.x
        aY = position.y
        context.quadraticCurveTo(aX, aY, aX, aY + radius) //upper left corner

        aX = position.x
        aY = position.y + size.y
        context.lineTo(aX, aY - radius)
        context.quadraticCurveTo(aX, aY, aX + radius, aY) //lower left corner


        aX = position.x + size.x
        aY = position.y + size.y
        context.lineTo(aX - radius, aY)
        context.quadraticCurveTo(aX, aY, aX, aY - radius) //lower right corner

        aX = position.x + size.x
        aY = position.y
        context.lineTo(aX, aY + radius)
        context.quadraticCurveTo(aX, aY, aX - radius, aY) //upper right corner

        context.closePath()
    }
}