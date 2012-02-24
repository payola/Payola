package cz.payola.web.client.views.graph

import cz.payola.web.client.views.{Layer, Color, Vector, Point}
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom.{Element, Canvas, CanvasRenderingContext2D}

trait View {
    def draw(context: CanvasRenderingContext2D, color: Option[Color], position: Option[Point])

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

    protected def drawBezierCurve(context: CanvasRenderingContext2D, control1: Point, control2: Point,
        origin: Point, destination: Point, lineWidth: Double, color: Color) {

        context.lineWidth = lineWidth
        context.strokeStyle = color.toString

        context.beginPath()
        context.moveTo(origin.x, origin.y)
        context.bezierCurveTo(control1.x, control1.y, control2.x, control2.y, destination.x, destination.y)
        context.stroke()
    }

    protected def drawStraightLine(context: CanvasRenderingContext2D, origin: Point, destination: Point,
        lineWidth: Double, color: Color) {

        context.lineWidth = lineWidth
        context.strokeStyle = color.toString

        context.beginPath()
        context.moveTo(origin.x, origin.y)
        context.lineTo(destination.x, destination.y)
        context.stroke()
    }

    protected def drawCircle(context: CanvasRenderingContext2D, center: Point, radius: Double,
        lineWidth: Double, color: Color) {

        context.lineWidth = lineWidth
        context.strokeStyle = color.toString

        context.beginPath()
        context.arc(center.x, center.y, radius, 0, scala.math.Pi*2, true)
        context.stroke()
    }

    protected def drawText(context: CanvasRenderingContext2D, text: String,  origin: Point,
        color: Color, font: String, align: String) {

        context.fillStyle = color.toString
        context.font = font
        context.textAlign = align

        context.fillText(text, origin.x, origin.y)
    }

    protected def fillCurrentSpace(context: CanvasRenderingContext2D, color: Color) {

        context.fillStyle = color.toString
        context.fill()
    }

    protected def clear(context: CanvasRenderingContext2D, topLeft: Point, size: Vector) {
        val bottomRight = topLeft + size
        context.clearRect(topLeft.x, topLeft.y, bottomRight.x, bottomRight.y)
    }

    protected def createLayer(container: Element): Layer = {
        val canvas = document.createElement[Canvas]("canvas")
        val context = canvas.getContext[CanvasRenderingContext2D]("2d")
        val layer = new Layer(canvas, context)

        container.appendChild(canvas)
        layer.setSize(Vector(1500, 1500)) //TODO take it from the "created element"
        layer
    }
}