package cz.payola.web.client.views.plugins.visual

/**
  * Representation of a position in 2-dimensional space.
  * @param x coordinate
  * @param y coordinate
  */
case class Point(var x: Double, var y: Double)
{
    /**
      * Move the position by a vector.
      * @param vector direction and distance of movement
      * @return new point with changed position
      */
    def +(vector: Vector): Point = {
        Point(x + vector.x, y + vector.y)
    }

    /**
      * Deduction of two points
      * @param point to deduct
      * @return vector describing direction and distance to move this point to position of point parameter
      */
    def -(point: Point): Vector = {
        Vector(x - point.x, y - point.y)
    }

    /**
      * Comparison of two points.
      * @param point to compare with
      * @return true if both coordinates of this point are less than or equal to the parameter point
      */
    def <=(point: Point): Boolean = {
        x <= point.x && y <= point.y
    }

    /**
      * Comparison of two points.
      * @param point to compare with
      * @return true if both coordinates of this point are greater than or equal to the parameter point
      */
    def >=(point: Point): Boolean = {
        x >= point.x && y >= point.y
    }

    /**
      * Conversion to a Vector
      * @return new Vector object values set to equal to this point
      */
    def toVector: Vector = {
        Vector(x, y)
    }
}

/**
  * Frequently used constant points
  */
object Point
{
    val Zero = Point(0, 0)
}
