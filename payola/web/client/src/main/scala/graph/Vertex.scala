package cz.payola.web.client.graph

class Vertex(val id: Int, val x: Double, val y: Double, val text: String, var neighbours: Array[Vertex]) {
    var selected = false;
}
