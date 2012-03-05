package cz.payola.web.client.views.plugins.visual

import scala.math._

/**
  * Representation of a vector in 2-dimensional space.
  * @param x first value of the vector
  * @param y first value of the vector
  */
case class Vector(x: Double, y: Double)
{
    /**
      * Addition
      * @param vector to add
      * @return new vector with values of this vector plus the parameter vector
      */
    def +(vector: Vector): Vector = {
        Vector(x + vector.x, y + vector.y)
    }

    /**
      * Deduction
      * @param vector to deduct
      * @return new vector with values of this vector minus the parameter vector
      */
    def -(vector: Vector): Vector = {
        Vector(x - vector.x, y - vector.y)
    }

    /**
      * Times -1
      * @return new vector with values of this vector times -1
      */
    def unary_-(): Vector = {
        Vector(-x, -y)
    }

    /**
      * Multiplication
      * @param value to multiply this vectors values with
      * @return new vector with values multiplied by the parameter value
      */
    def *(value: Double): Vector = {
        Vector(x * value, y * value)
    }

    /**
      * Division
      * @param value to divide this vectors values with
      * @return new vector with values divided by the parameter value
      */
    def /(value: Double): Vector = {
        Vector(x / value, y / value)
    }

    /**
      * Counts length of this vector.
      * @return square root of added squares of values of this vector
      */
    def length: Double = {
        sqrt(pow(x, 2) + pow(y, 2))
    }
}
