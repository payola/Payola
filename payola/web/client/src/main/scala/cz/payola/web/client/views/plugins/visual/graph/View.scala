package cz.payola.web.client.views.plugins.visual.graph

import s2js.adapters.js.browser._
import s2js.adapters.js.dom._
import cz.payola.web.client.views.plugins.visual._

trait View extends SetupLoader{
    /**
      * Routine for drawing the graphical representation of graphs objects.
      * @param context to which container to draw
      * @param color which color to use
      * @param position to which location to draw
      */
    def draw(context: CanvasRenderingContext2D, color: Option[Color], position: Option[Point])


    def updateSettings(settings: Element)

    /**
      * Draws a rectangle with rounded corners, depending on the radius parameter to the input canvas context.
      * @param context to which to draw
      * @param position to which location to draw
      * @param size of the drawn "rounded rectangle"
      * @param radius of the corners of the rectangle
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

    /**
      * Draws a bezier curve based on the parameters.
      * @param context to which to draw
      * @param control1 first point of bending the line
      * @param control2 second point of bending the line
      * @param origin where to start the line
      * @param destination where to end the line
      * @param lineWidth width of the line
      * @param color of the line
      */
    protected def drawBezierCurve(context: CanvasRenderingContext2D, control1: Point, control2: Point,
        origin: Point, destination: Point, lineWidth: Double, color: Color) {

        context.lineWidth = lineWidth
        context.strokeStyle = color.toString

        context.beginPath()
        context.moveTo(origin.x, origin.y)
        context.bezierCurveTo(control1.x, control1.y, control2.x, control2.y, destination.x, destination.y)
        context.stroke()
    }

    /**
      * Draws a straight line based on the parameters.
      * @param context to which to draw
      * @param origin where to start the line
      * @param destination where to end the line
      * @param lineWidth width of the line
      * @param color of the line
      */
    protected def drawStraightLine(context: CanvasRenderingContext2D, origin: Point, destination: Point,
        lineWidth: Double, color: Color) {

        context.lineWidth = lineWidth
        context.strokeStyle = color.toString

        context.beginPath()
        context.moveTo(origin.x, origin.y)
        context.lineTo(destination.x, destination.y)
        context.stroke()
    }

    /**
      * Draws a circle based on the parameters.
      * @param context to which to draw
      * @param center of the drawn circle
      * @param radius of the drawn circle
      * @param lineWidth width of the line of the circle
      * @param color of the line of the circle
      */
    protected def drawCircle(context: CanvasRenderingContext2D, center: Point, radius: Double,
        lineWidth: Double, color: Color) {

        context.lineWidth = lineWidth
        context.strokeStyle = color.toString

        context.beginPath()
        context.arc(center.x, center.y, radius, 0, scala.math.Pi*2, true)
        context.stroke()
    }

    /**
      * Draws text based on the parametes.
      * @param context to which to draw
      * @param text which to draw
      * @param origin where to start drawing
      * @param color of the text
      * @param font of the text
      * @param align of the text
      */
    protected def drawText(context: CanvasRenderingContext2D, text: String,  origin: Point,
        color: Color, font: String, align: String) {

        context.fillStyle = color.toString
        context.font = font
        context.textAlign = align

        context.fillText(text, origin.x, origin.y)
    }

    /**
      * Fills the last drawn object with color.
      * @param context where in to fill a space
      * @param color of the filling
      */
    protected def fillCurrentSpace(context: CanvasRenderingContext2D, color: Color) {

        context.fillStyle = color.toString
        context.fill()
    }

    /**
      * Draws the image to the specified position and resizes it to the specified dimensions.
      * @param context to where to draw
      * @param image to draw
      * @param location of drawing
      * @param dimensions to stretch the image to
      */
    protected def drawImage(context: CanvasRenderingContext2D, image: Element, location: Point, dimensions: Vector) {

        context.drawImage(image, location.x, location.y, dimensions.x, dimensions.y)
    }

    protected def prepareImage(colorToUse: Color, imagePath: String): Canvas = {

        val imageSize = Vector(20, 20)
        val canvas = document.createElement[Canvas]("canvas")
        canvas.width = imageSize.x
        canvas.height = imageSize.y
        val context = canvas.getContext[CanvasRenderingContext2D]("2d")

        //nakreslim do lokalniho canvasu
        val imageElement = document.createElement[Image]("img")
        imageElement.src = imagePath

        drawImage(context, imageElement, Point(0, 0), imageSize)

        //nakreslim do globalniho canvasu lokalni canvas
        val imageData = context.getImageData(0, 0, imageSize.x, imageSize.y);
        val canvasPixelArray = imageData.data;

        var pixelPointer = 0
        while(pixelPointer < canvasPixelArray.length) { //TODO recoloring of the icon

            canvasPixelArray(pixelPointer) = 255 // = 255   // red
            canvasPixelArray(pixelPointer + 1) = 255 // = 255   // green
            canvasPixelArray(pixelPointer + 2) = 255 // = 255   // blue
            // alpha
            pixelPointer += 4
        }
        context.putImageData(imageData, 0, 0);
        canvas
    }

    /**
      * Clears the specified area from all drawn elements
      * @param context where in to clear
      * @param topLeft corner of the cleared rectangle
      * @param size of the cleared rectangle
      */
    protected def clear(context: CanvasRenderingContext2D, topLeft: Point, size: Vector) {
        val bottomRight = topLeft + size
        context.clearRect(topLeft.x, topLeft.y, bottomRight.x, bottomRight.y)
    }

    /**
      * Indicator if the point is inside of the rectangle.
      * @param point to ask for location
      * @param topLeft point of the rectangle
      * @param bottomRight point of the rectangle
      * @return true if the point is inside the rectangle
      */
    protected def isPointInRect(point: Point, topLeft: Point, bottomRight: Point): Boolean = {
        point >= topLeft && point <= bottomRight
    }

    /**
      * Constructs a canvas context element as a child of the input container Element object.
      * @param container parent of the created canvas context
      * @return Layer object with a new canvas context
      */
    protected def createLayer(container: Element): Layer = {
        val canvas = document.createElement[Canvas]("canvas")
        val context = canvas.getContext[CanvasRenderingContext2D]("2d")
        val layer = new Layer(canvas, context)

        container.appendChild(canvas)
        layer.setSize(Vector(1500, 1500)) //TODO take it from the "created element"
        layer
    }
}
