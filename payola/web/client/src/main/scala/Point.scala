package cz.payola.web.client

case class Point(var x: Double, var y: Double) {
    // TODO add support for overridden operators like "+" to compiler.
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
