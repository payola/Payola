package cz.payola.web.client

case class Point(x: Double, y: Double) {
    // TODO add support for overriden opertators like "+" to compiler.
    def add(v: Vector): Point = {
        Point(x + v.x, y + v.y)
    }

    def subtract(p: Point): Vector = {
        Vector(x - p.x, y - p.y)
    }

    def toVector: Vector = {
        Vector(x, y)
    }
}

object Point {
    val Zero = Point(0, 0)
}
