package cz.payola.web.client.views.graph.visual.graph

import s2js.adapters.browser._
import s2js.adapters.html
import s2js.adapters.html.elements
import cz.payola.common.visual.Color
import cz.payola.web.client.views.algebra._
import cz.payola.common.entities.settings._
import s2js.adapters.html

/**
 * Structure used during draw function of EdgeView. Helps to indicate position of vertices to each other.
 */
private object Quadrant
{
    val RightBottom = 1

    val LeftBottom = 2

    val LeftTop = 3

    val RightTop = 4
}

trait View[A]
{
    /**
     * Indicator of selection.
     * @return true if marked as selected.
     */
    def isSelected: Boolean

    /**
     * Indicator if the View is drawn and can be selected
     * @return true if hidden
     */
    def isHidden = hidden

    def hide() {
        hidden = true
    }

    def show() {
        hidden = false
    }

    protected var hidden = false

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
     * Setter of the ontology visual configuration to default.
     */
    def resetConfiguration()

    /**
     * Setter of the ontology visual configuration.
     * @param newCustomization new configuration for drawing
     */
    def setConfiguration(newOntoCustomization: Option[DefinedCustomization])

    /**
     * Draws a rectangle with rounded corners, depending on the radius parameter to the input canvas context.
     * @param context to which to draw
     * @param position to which location to draw
     * @param size of the drawn "rounded rectangle"
     * @param radius of the corners of the rectangle
     */
    protected def drawRoundedRectangle(context: elements.CanvasRenderingContext2D, position: Point2D,
        size: Vector2D, radius: Double) {
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
     * Calculates the the control points and draws a bezier curve from origin to destination
     * @param context to which to draw
     * @param origin where to start the line
     * @param destination where to end the line
     * @param color of the line
     * @param lineWidth width of the drawn curve
     * @param straightenIndex the higher the straighter the line is, number from interval (0, 6) is expected
     */
    protected def drawBezierCurve(context: elements.CanvasRenderingContext2D, origin: Point2D,
        destination: Point2D, color: Color, lineWidth: Double, straightenIndex: Double) {
        val A = origin
        val B = destination

        val ctrl1 = Point2D(0, 0)
        val ctrl2 = Point2D(0, 0)

        val diff = Point2D(scala.math.abs(A.x - B.x), scala.math.abs(A.y - B.y))

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
                    ctrl1.x = A.x + diff.x / straightenIndex
                    ctrl1.y = A.y
                    ctrl2.x = B.x - diff.x / straightenIndex
                    ctrl2.y = B.y
                case Quadrant.LeftBottom | Quadrant.LeftTop =>
                    //we are in (pi3/4, pi] or in (pi, pi5/4]
                    ctrl1.x = A.x - diff.x / straightenIndex
                    ctrl1.y = A.y
                    ctrl2.x = B.x + diff.x / straightenIndex
                    ctrl2.y = B.y
            }
        } else {
            //connecting top/bottom sides of vertices
            quadrant match {
                case Quadrant.RightBottom | Quadrant.LeftBottom =>
                    //we are in (pi/4, pi/2] or in (pi/2, pi3/4]
                    ctrl1.x = A.x
                    ctrl1.y = A.y + diff.y / straightenIndex
                    ctrl2.x = B.x
                    ctrl2.y = B.y - diff.y / straightenIndex
                case Quadrant.RightTop | Quadrant.LeftTop =>
                    //we are in (pi5/4, pi3/2] or in (pi3/2, pi7/4]
                    ctrl1.x = A.x
                    ctrl1.y = A.y - diff.y / straightenIndex
                    ctrl2.x = B.x
                    ctrl2.y = B.y + diff.y / straightenIndex
            }
        }

        performBezierCurveDrawing(context, ctrl1, ctrl2, A, B, lineWidth, color)
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
    private def performBezierCurveDrawing(context: elements.CanvasRenderingContext2D, control1: Point2D,
        control2: Point2D, origin: Point2D, destination: Point2D, lineWidth: Double, color: Color) {
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
    protected def drawStraightLine(context: elements.CanvasRenderingContext2D, origin: Point2D,
        destination: Point2D, lineWidth: Double, color: Color) {
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
    protected def drawCircle(context: elements.CanvasRenderingContext2D, center: Point2D, radius: Double,
        lineWidth: Double, color: Color) {
        context.lineWidth = lineWidth
        context.strokeStyle = color.toString

        context.beginPath()
        context.arc(center.x, center.y, radius, 0, scala.math.Pi * 2, true)
        context.stroke()
    }

    protected def drawSquare(context: elements.CanvasRenderingContext2D, center: Point2D, size: Double,
        lineWidth: Double, color: Color) {

        context.lineWidth = lineWidth
        context.strokeStyle = color.toString

        context.beginPath()
        context.moveTo(center.x - size/2, center.y - size/2)
        context.lineTo(center.x + size/2, center.y - size/2)
        context.lineTo(center.x + size/2, center.y + size/2)
        context.lineTo(center.x - size/2, center.y + size/2)
        context.lineTo(center.x - size/2, center.y - size/2 - lineWidth/2)
        context.stroke()
    }

    /**
     * Draws a straight line between origin and destination points closed by an arrow at the destination side.
     * @param context to which to draw
     * @param origin from where the line goes
     * @param destination to where the line goes
     * @param offsetOrigin how long section of the line (starting at origin) should be skipped
     * @param offsetDestination how long section of the line (ending at destination) should be skipped
     * @param lineWidth width of the line
     * @param color of the line and the arrow
     */
    protected def drawArrow(context: elements.CanvasRenderingContext2D, origin: Point2D, destination: Point2D,
        offsetOrigin: Double, offsetDestination: Double, lineWidth: Double, color: Color) {
        var arrowPointingFrom: Option[Point2D] = None
        var arrowPointingTo: Option[Point2D] = None

        if (origin.distance(destination) >= offsetDestination + offsetOrigin) {

            arrowPointingFrom = getOffsetPoint(origin - destination, origin, offsetOrigin)
            arrowPointingTo = getOffsetPoint(destination - origin, destination, offsetDestination)
        } else {
            arrowPointingFrom = getOffsetPoint(destination - origin, destination, offsetOrigin)
            arrowPointingTo = getOffsetPoint(origin - destination, origin, offsetDestination)
        }

        if (arrowPointingTo.isDefined && arrowPointingFrom.isDefined) {
            val correctedArrowPointingTo = arrowPointingTo.get + (
                -arrowPointingFrom.get.createVector(arrowPointingTo.get).createVectorOfSize(lineWidth))


            drawStraightLine(context, arrowPointingFrom.get, correctedArrowPointingTo, lineWidth, color)
            drawArrowCap(context, arrowPointingTo.get - arrowPointingFrom.get, correctedArrowPointingTo, lineWidth,
                color)
        } else {
            drawStraightLine(context, origin, destination, lineWidth, color)
        }
    }

    /**
     * Draws an arrow cap (arrow without the center line - not ->, but only >).
     * @param context to which to draw
     * @param direction in which the arrow should be pointing
     * @param destination which should be the top point of the arrow
     * @param lineWidth width of the lines; also determines size of the arrow; the length is lineWidth * 4
     *                  and the distance between the two distant ends is lineWidth * 4
     * @param color of the drawn lines
     */
    private def drawArrowCap(context: elements.CanvasRenderingContext2D, direction: Vector2D,
        destination: Point2D, lineWidth: Double, color: Color) {
        val arrowSize = Vector2D(lineWidth, lineWidth) * 4

        val originPoint = getOffsetPoint(direction, destination, arrowSize.y)

        if (originPoint.isDefined) {
            var side1: Option[Point2D] = None
            var side2: Option[Point2D] = None

            val u = direction

            if (u.x != 0) {
                val a = math.pow(u.y, 2) + math.pow(u.x, 2)
                val b = -2 * math.pow(u.y, 2) * originPoint.get.y - 2 * originPoint.get.y * math.pow(u.x, 2)
                val c = math.pow(u.y * originPoint.get.y, 2) + math.pow(originPoint.get.y * u.x, 2) - math.pow(
                    arrowSize.x * u.x, 2)

                val D = math.pow(b, 2) - 4 * a * c
                if (D >= 0) {
                    val Dsqrt = math.sqrt(D)

                    val y1 = (-b + Dsqrt) / (2 * a)
                    val y2 = (-b - Dsqrt) / (2 * a)
                    val x1 = (-u.y * y1 + u.x * originPoint.get.x + u.y * originPoint.get.y) / u.x
                    val x2 = (-u.y * y2 + u.x * originPoint.get.x + u.y * originPoint.get.y) / u.x

                    side1 = Some(Point2D(x1, y1))
                    side2 = Some(Point2D(x2, y2))
                } else {
                    //something went somewhere terribly wrong
                    //window.alert("D < 0 while getting sides of an arrow")
                }
            } else {
                val y = if(u.y > 0) { destination.y - arrowSize.y } else { destination.y + arrowSize.y }
                val x1 = arrowSize.y + originPoint.get.x
                val x2 = -arrowSize.x + originPoint.get.x

                side1 = Some(Point2D(x1, y))
                side2 = Some(Point2D(x2, y))
            }

            if (side1.isDefined && side2.isDefined) {
                val destinationCorrection1 = -destination.createVector(side1.get).createVectorOfSize(lineWidth / 2)

                val destinationCorrection2 = -destination.createVector(side2.get).createVectorOfSize(lineWidth / 2)

                drawStraightLine(context, side1.get, destination + destinationCorrection1, lineWidth, color)
                drawStraightLine(context, side2.get, destination + destinationCorrection2, lineWidth, color)
            }
        }
    }

    /**
     * Calculates a new Point2D lying on the line - determined by the oposite direction of the vector - distant from
     * the destination point by offset.
     * @param direction opposite that the new point should lie
     * @param destination the point that lies on the imagined line
     * @param offset the distance in which the new point should be
     * @return the new point distant by offset from the destination lying on the line determined by opposite to direction
     */
    private def getOffsetPoint(direction: Vector2D, destination: Point2D, offset: Double): Option[Point2D] = {
        var originPoint: Option[Point2D] = None
        if (direction.y != 0) {
            val a = math.pow(direction.x, 2) + math.pow(direction.y, 2)
            val b = -2 * math.pow(direction.x, 2) * destination.y - 2 * destination.y * math.pow(direction.y, 2)
            val c = math.pow(direction.x * destination.y, 2) + math.pow(destination.y * direction.y, 2) - math.pow(
                offset * direction.y, 2)

            val D = math.pow(b, 2) - 4 * a * c
            if (D >= 0) {
                val Dsqrt = math.sqrt(D)

                val y1 = (-b + Dsqrt) / (2 * a)
                val y2 = (-b - Dsqrt) / (2 * a)
                val x1 = (direction.x * y1 + direction.y * destination.x - direction.x * destination.y) / direction.y
                val x2 = (direction.x * y2 + direction.y * destination.x - direction.x * destination.y) / direction.y

                val pointToCompare = destination + (-direction) //the result has to be closer to this point
                if (pointToCompare.distance(Point2D(x1, y1)) <= pointToCompare.distance(Point2D(x2, y2))) {
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
            val x1 = offset + destination.x
            val x2 = -offset + destination.x

            val pointToCompare = destination + (-direction) //the result has to be closer to this point
            if (pointToCompare.distance(Point2D(x1, y)) <= pointToCompare.distance(Point2D(x2, y))) {
                originPoint = Some(Point2D(x1, y))
            } else {
                originPoint = Some(Point2D(x2, y))
            }
        }

        originPoint
    }

    /**
     * Draws text.
     * @param context to which to draw
     * @param text which to draw
     * @param origin where to start drawing
     * @param color of the text
     * @param font of the text
     * @param align of the text
     */
    protected def drawText(context: elements.CanvasRenderingContext2D, text: String, origin: Point2D,
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
    protected def fillCurrentSpace(context: elements.CanvasRenderingContext2D, color: Color) {
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
    protected def drawImage(context: elements.CanvasRenderingContext2D, image: html.Element, location: Point2D,
        dimensions: Vector2D) {
        context.drawImage(image, location.x, location.y, dimensions.x, dimensions.y)
    }

    /**
     * Creates a new canvas and draws the image to it.
     * @param imagePath the image that should be drawn to the new canvas
     * @return canvas with the drawn image
     */
    protected def prepareImage(imagePath: String): html.elements.Canvas = {
        val imageSize = Vector2D(20, 20)
        val canvas = document.createElement[elements.Canvas]("canvas")
        canvas.width = imageSize.x
        canvas.height = imageSize.y
        val context = canvas.getContext[elements.CanvasRenderingContext2D]("2d")

        //nakreslim do lokalniho canvasu

        val imageElement = document.createElement[html.elements.Image]("img")
        imageElement.src = imagePath

        drawImage(context, imageElement, Point2D(0, 0), imageSize)
        canvas
    }

    /**
     * Sets color to all pixels within a rectangle in the canvas. The alpha value stays untouched.
     * @param canvas where to change colors.
     * @param startPoint  left top point of the rectangle in which the color should be set
     * @param endPoint right bottom point of the rectangle in which the color should be set
     * @param color that should be set to all pixels in the rectangle; the alpha value stays unchanged
     * @return canvas with set color
     */
    protected def setColorOfDrawnElements(canvas: elements.Canvas, startPoint: Point2D, endPoint: Point2D,
        color: Color): elements.Canvas = {
        val context = canvas.getContext[elements.CanvasRenderingContext2D]("2d")
        //nakreslim do globalniho canvasu lokalni canvas
        val imageData = context.getImageData(startPoint.x, startPoint.y, endPoint.x, endPoint.y)
        val canvasPixelArray = imageData.data

        var pixelPointer = 0
        while (pixelPointer < canvasPixelArray.length) {

            canvasPixelArray(pixelPointer) = color.red
            canvasPixelArray(pixelPointer + 1) = color.green
            canvasPixelArray(pixelPointer + 2) = color.blue
            // alpha is unchanged to keep the shape of the image
            pixelPointer += 4
        }
        context.putImageData(imageData, 0, 0)
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
