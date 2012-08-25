package cz.payola.web.client.views.algebra

import scala.math._
import s2js.adapters.browser._

/**
 * Representation of a vector in 2-dimensional space.
 * @param x first value of the vector
 * @param y first value of the vector
 */
case class Vector2D(x: Double, y: Double)
{
    /**
     * Addition
     * @param vector to add
     * @return new vector with values of this vector plus the parameter vector
     */
    def +(vector: Vector2D): Vector2D = {
        Vector2D(x + vector.x, y + vector.y)
    }

    /**
     * Deduction
     * @param vector to deduct
     * @return new vector with values of this vector minus the parameter vector
     */
    def -(vector: Vector2D): Vector2D = {
        Vector2D(x - vector.x, y - vector.y)
    }

    /**
     * Constructs a new vector with the same direction as this and converts its size to the specified value size
     * @param size of the created vector
     * @return new resized vector based on this vector
     */
    def createVectorOfSize(size: Double): Vector2D = {
        this / this.length * size
    }

    /**
     * Times -1
     * @return new vector with values of this vector times -1
     */
    def unary_-(): Vector2D = {
        Vector2D(-x, -y)
    }

    /**
     * Multiplication
     * @param value to multiply this vectors values with
     * @return new vector with values multiplied by the parameter value
     */
    def *(value: Double): Vector2D = {
        Vector2D(x * value, y * value)
    }

    /**
     * Division
     * @param value to divide this vectors values with
     * @return new vector with values divided by the parameter value
     */
    def /(value: Double): Vector2D = {
        Vector2D(x / value, y / value)
    }

    /**
     * Counts length of this vector.
     * @return square root of added squares of values of this vector
     */
    def length: Double = {
        sqrt(pow(x, 2) + pow(y, 2))
    }

    /**
     * Creates String representation of this vector.
     * @return [x; y]
     */
    override def toString: String = {
        "[%d; %d]".format(x, y)
    }
}

/**
 * Frequently used constant vectors
 */
object Vector2D
{
    val Zero = Vector2D(0, 0)

    val One = Vector2D(1, 1)
}
