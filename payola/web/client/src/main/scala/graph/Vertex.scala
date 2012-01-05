package cz.payola.web.client.graph

import cz.payola.web.client.Point
//import s2js.runtime.scala.collection.immutable.List

class Vertex(val id: Int, var position: Point, val text: String, var neighbours: List[Vertex]) {
    var selected = false;

    def x: Double = {
        position.x
    }

    def y: Double = {
        position.y
    }
}
