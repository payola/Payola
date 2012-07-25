package cz.payola.web.client.views.graph.visual.graph

import s2js.adapters.js.browser._
import s2js.adapters.js.dom._
import s2js.adapters.js.dom.Element
import cz.payola.web.client.views.graph.visual.Color
import cz.payola.web.client.views.algebra._

trait View[A]
{
    /**
      * Indicator of selection.
      * @return true if marked as selected.
      */
    def isSelected: Boolean

    /**
      * Routine for drawing the graphical representation of graphs objects.
      * @param context to which container to draw
      * @param positionCorrection to modify the position of the drawn object
      */
    def draw(context: A, positionCorrection: Vector2D)

    /**
      * Routine for fast drawing of the graphical representation of graphs objects. Should be used for animation
      * @param context to which container to draw
      * @param positionCorrection to modify the position of the drawn object
      */
    def drawQuick(context: A, positionCorrection: Vector2D)

    /**
      * Draws a rectangle with rounded corners, depending on the radius parameter to the input canvas context.
      * @param context to which to draw
      * @param position to which location to draw
      * @param size of the drawn "rounded rectangle"
      * @param radius of the corners of the rectangle
      */
    protected def drawRoundedRectangle(context: CanvasRenderingContext2D, position: Point2D, size: Vector2D,
        radius: Double) {
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

        val a = position
        context.moveTo(a.x + radius, a.y)

        context.quadraticCurveTo(a.x, a.y, a.x, a.y + radius) //upper left corner

        context.lineTo(a.x, a.y + size.y - radius)
        context.quadraticCurveTo(a.x, a.y + size.y, a.x + radius, a.y + size.y) //lower left corner


        context.lineTo(a.x + size.x - radius, a.y + size.y)
        context.quadraticCurveTo(a.x + size.x, a.y + size.y, a.x + size.x, a.y + size.y - radius) //lower right corner

        context.lineTo(a.x + size.x, a.y + radius)
        context.quadraticCurveTo(a.x + size.x, a.y, a.x + size.x - radius, a.y) //upper right corner

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
    protected def drawBezierCurve(context: CanvasRenderingContext2D, control1: Point2D, control2: Point2D,
        origin: Point2D, destination: Point2D, lineWidth: Double, color: Color) {
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
    protected def drawStraightLine(context: CanvasRenderingContext2D, origin: Point2D, destination: Point2D,
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
    protected def drawCircle(context: CanvasRenderingContext2D, center: Point2D, radius: Double,
        lineWidth: Double, color: Color) {
        context.lineWidth = lineWidth
        context.strokeStyle = color.toString

        context.beginPath()
        context.arc(center.x, center.y, radius, 0, scala.math.Pi * 2, true)
        context.stroke()
    }

    protected def drawArrow(context: CanvasRenderingContext2D, origin: Point2D, destination: Point2D,
        offsetOrigin: Double, offsetDestination: Double, lineWidth: Double, color: Color) {

        var arrowPointingFrom: Option[Point2D] = None
        var arrowPointingTo: Option[Point2D] = None

        if(origin.distance(destination) >= offsetDestination + offsetOrigin) {

            arrowPointingFrom = getArrowOriginPoint(origin - destination, origin, offsetOrigin)
            arrowPointingTo = getArrowOriginPoint(destination - origin, destination, offsetDestination)
        } else {
            arrowPointingFrom = getArrowOriginPoint(destination - origin, destination, offsetOrigin)
            arrowPointingTo = getArrowOriginPoint(origin - destination, origin, offsetDestination)
        }

        if(arrowPointingTo.isDefined && arrowPointingFrom.isDefined) {
            drawStraightLine(context, arrowPointingFrom.get, arrowPointingTo.get, lineWidth, color)
            drawArrowEnd(context, arrowPointingTo.get - arrowPointingFrom.get, arrowPointingTo.get, lineWidth, color)
        } else {
            drawStraightLine(context, origin, destination, lineWidth, color)
        }
    }


    protected def drawArrowEnd(context: CanvasRenderingContext2D, direction: Vector2D, destination: Point2D,
        lineWidth: Double, color: Color) {

        val arrowLength = lineWidth*4
        val arrowWidth = lineWidth*4

        val originPoint = getArrowOriginPoint(direction, destination, arrowLength)

        if(originPoint.isDefined) {
            var side1:Option[Point2D] = None
            var side2: Option[Point2D] = None

            val u = direction

            if(u.x != 0) {
                val a = math.pow(u.y, 2) + math.pow(u.x, 2)
                val b = -2*math.pow(u.y, 2)*originPoint.get.y - 2*originPoint.get.y*math.pow(u.x, 2)
                val c = math.pow(u.y*originPoint.get.y, 2) + math.pow(originPoint.get.y*u.x, 2)- math.pow(
                    arrowWidth*u.x, 2)

                val D = math.pow(b, 2) - 4*a*c
                if(D >= 0) {
                    val Dsqrt = math.sqrt(D)

                    val y1 = (-b + Dsqrt) / (2*a)
                    val y2 = (-b - Dsqrt) / (2*a)
                    val x1 = (-u.y*y1 + u.x*originPoint.get.x + u.y*originPoint.get.y)/u.x
                    val x2 = (-u.y*y2 + u.x*originPoint.get.x + u.y*originPoint.get.y)/u.x

                    side1 = Some(Point2D(x1, y1))
                    side2 = Some(Point2D(x2, y2))
                } else {
                    //something went somewhere terribly wrong
                    //window.alert("D < 0 while getting sides of an arrow")
                }
            } else {
                val y = destination.y
                val x1 = arrowLength + originPoint.get.x
                val x2 = -arrowLength + originPoint.get.x

                side1 = Some(Point2D(x1, y))
                side2 = Some(Point2D(x2, y))
            }

            if(side1.isDefined && side2.isDefined) {
                drawStraightLine(context, side1.get, destination, lineWidth, color)
                drawStraightLine(context, side2.get, destination, lineWidth, color)
            }
        }
    }

    private def getArrowOriginPoint(direction: Vector2D, destination: Point2D, arrowLength: Double): Option[Point2D] = {

        var originPoint: Option[Point2D] = None
        if(direction.y != 0) {
            val a = math.pow(direction.x, 2) + math.pow(direction.y, 2)
            val b = -2*math.pow(direction.x, 2)*destination.y - 2*destination.y*math.pow(direction.y, 2)
            val c = math.pow(direction.x*destination.y, 2) + math.pow(destination.y*direction.y, 2)- math.pow(
                arrowLength*direction.y, 2)

            val D = math.pow(b, 2) - 4*a*c
            if(D >= 0) {
                val Dsqrt = math.sqrt(D)

                val y1 = (-b + Dsqrt) / (2*a)
                val y2 = (-b - Dsqrt) / (2*a)
                val x1 = (direction.x*y1 + direction.y*destination.x - direction.x*destination.y)/direction.y
                val x2 = (direction.x*y2 + direction.y*destination.x - direction.x*destination.y)/direction.y

                val pointToCompare = destination + (-direction) //the result has to be closer to this point
                if(pointToCompare.distance(Point2D(x1, y1)) <= pointToCompare.distance(Point2D(x2, y2))) {
                    originPoint = Some(Point2D(x1, y1))
                } else {
                    originPoint = Some(Point2D(x2, y2))
                }
            } else {
                //something went somewhere terribly wrong
                //window.alert("D < 0 while getting second point of an arrow")
            }
        } else {
            val y = destination.y
            val x1 = arrowLength + destination.x
            val x2 = -arrowLength + destination.x

            val pointToCompare = destination + (-direction) //the result has to be closer to this point
            if(pointToCompare.distance(Point2D(x1, y)) <= pointToCompare.distance(Point2D(x2, y))) {
                originPoint = Some(Point2D(x1, y))
            } else {
                originPoint = Some(Point2D(x2, y))
            }
        }

        originPoint
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
    protected def drawText(context: CanvasRenderingContext2D, text: String, origin: Point2D,
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
    protected def drawImage(context: CanvasRenderingContext2D, image: Element, location: Point2D, dimensions: Vector2D) {
        context.drawImage(image, location.x, location.y, dimensions.x, dimensions.y)
    }

    protected def prepareImage(colorToUse: Color, imagePath: String): Canvas = {
        val imageSize = Vector2D(20, 20)
        val canvas = document.createElement[Canvas]("canvas")
        canvas.width = imageSize.x
        canvas.height = imageSize.y
        val context = canvas.getContext[CanvasRenderingContext2D]("2d")

        //nakreslim do lokalniho canvasu

        val imageElement = document.createElement[Image]("img")
        imageElement.src = imagePath

        drawImage(context, imageElement, Point2D(0, 0), imageSize)

        //nakreslim do globalniho canvasu lokalni canvas
        val imageData = context.getImageData(0, 0, imageSize.x, imageSize.y);
        val canvasPixelArray = imageData.data;

        var pixelPointer = 0
        while (pixelPointer < canvasPixelArray.length) {

            canvasPixelArray(pixelPointer) = colorToUse.red
            canvasPixelArray(pixelPointer + 1) = colorToUse.green
            canvasPixelArray(pixelPointer + 2) = colorToUse.blue
            // alpha is unchanged to keep the shape of the image
            pixelPointer += 4
        }
        context.putImageData(imageData, 0, 0);
        canvas
    }

    /**
      * Indicator if the point is inside of the rectangle.
      * @param point to ask for location
      * @param topLeft point of the rectangle
      * @param bottomRight point of the rectangle
      * @return true if the point is inside the rectangle
      */
    protected def isPointInRect(point: Point2D, topLeft: Point2D, bottomRight: Point2D): Boolean = {
        point >= topLeft && point <= bottomRight
    }
}
