package cz.payola.web.client.views.visualPlugin

import scala.math._

case class Vector(x: Double, y: Double)
{
    def +(v: Vector): Vector = {
        Vector(x + v.x, y + v.y)
    }

    def -(v: Vector): Vector = {
        Vector(x - v.x, y - v.y)
    }

    def unary_-(): Vector = {
        Vector(-x, -y)
    }

    def *(d: Double): Vector = {
        Vector(x * d, y * d)
    }

    def /(d: Double): Vector = {
        Vector(x / d, y / d)
    }

    def length: Double = {
        sqrt(pow(x, 2) + pow(y, 2))
    }
}
