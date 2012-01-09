package cz.payola.web.client

case class Point(var x: Double, var y: Double) {
    def +(v: Vector): Point = {
        Point(x + v.x, y + v.y)
    }

    def -(p: Point): Vector = {
        Vector(x - p.x, y - p.y)
    }
    
    def <=(p: Point): Boolean = {
        x <= p.x && y <= p.y
    }

    def >=(p: Point): Boolean = {
        x >= p.x && y >= p.y
    }

    def toVector: Vector = {
        Vector(x, y)
    }
}

object Point {
    val Zero = Point(0, 0)
}
