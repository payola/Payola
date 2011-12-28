package cz.payola.web.client

case class Vector(x: Double, y: Double) {
    def add(v: Vector): Vector = {
        Vector(x + v.x, y + v.y)
    }
    
    def multiply(d: Double): Vector = {
        Vector(x * d, y * d)
    }
}
