package cz.payola.web.client.views.algebra

/**
 * Representation of a position in 2-dimensional space.
 * @param x coordinate
 * @param y coordinate
 */
case class Point2D(var x: Double, var y: Double)
{
    /**
     * Move the position by a vector.
     * @param vector direction and distance of movement
     * @return new point with changed position
     */
    def +(vector: Vector2D): Point2D = {
        Point2D(x + vector.x, y + vector.y)
    }

    /**
     * Subtraction of two points
     * @param point to deduct
     * @return vector describing direction and distance to move this point to position of point parameter
     */
    def -(point: Point2D): Vector2D = {
        Vector2D(x - point.x, y - point.y)
    }

    /**
     * Returns the current point with coordinates multiplied by the specified value.
     */
    def *(value: Double): Point2D = {
        Point2D(x * value, y * value)
    }

    /**
     * Returns the current point with coordinates divided by the specified value.
     */
    def /(value: Double): Point2D = {
        Point2D(x / value, y / value)
    }

    /**
     * Comparison of two points.
     * @param point to compare with
     * @return true if both coordinates of this point are less than or equal to the parameter point
     */
    def <=(point: Point2D): Boolean = {
        x <= point.x && y <= point.y
    }

    /**
     * Comparison of two points.
     * @param point to compare with
     * @return true if both coordinates of this point are greater than or equal to the parameter point
     */
    def >=(point: Point2D): Boolean = {
        x >= point.x && y >= point.y
    }

    /**
     * Conversion to a Vector2D
     * @return new Vector2D object values set to equal to this point
     */
    def toVector: Vector2D = {
        Vector2D(x, y)
    }

    /**
     * Creates vector between two points
     * @param destination of the vector
     * @return direction from this point to the destination
     */
    def createVector(destination: Point2D): Vector2D = {
        Vector2D(destination.x - x, destination.y - y)
    }

    /**
     * Computes distance of this and point p.
     * @param p
     * @return
     */
    def distance(p: Point2D): Double = {
        math.sqrt(math.pow(x - p.x, 2) + math.pow(y - p.y, 2))
    }

    /**
     * Creates String representation of this point.
     * @return [x; y]
     */
    override def toString: String = {
        "[%d; %d]".format(x, y)
    }
}

/**
 * Frequently used constant points
 */
object Point2D
{
    val Zero = Point2D(0, 0)
}
