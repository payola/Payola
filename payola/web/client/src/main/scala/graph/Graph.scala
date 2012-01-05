package cz.payola.web.client.graph

import cz.payola.web.client.graph.Constants._
import cz.payola.web.client.{Vector, Point}
//import s2js.runtime.scala.collection.immutable.List

class Graph (var vertices: List[Vertex]) {

    var selectedVertexCount = 0

    private def init(): List[Vertex] = {
        val v0 = new Vertex(0, Point(15, 15), "0", null)
        val v1 = new Vertex(1, Point(120, 40), "1", null)
        val v2 = new Vertex(2, Point(50, 120), "2", null)
        val v3 = new Vertex(3, Point(180, 60), "3", null)
        val v4 = new Vertex(4, Point(240, 110), "4", null)
        val v5 = new Vertex(5, Point(160, 160), "5", null)
        val v6 = new Vertex(6, Point(240, 240), "6", null)
        val v7 = new Vertex(7, Point(270, 320), "7", null)
        val v8 = new Vertex(8, Point(160, 240), "8", null)
        val v9 = new Vertex(9, Point(120, 400), "9", null)
        val v10 = new Vertex(10, Point(300, 80), "10", null)
        val v11 = new Vertex(11, Point(320, 30), "11", null)
        val v12 = new Vertex(12, Point(300, 200), "12", null)
        val v13 = new Vertex(13, Point(350, 210), "13", null)
        val v14 = new Vertex(14, Point(300, 400), "14", null)
        val v15 = new Vertex(15, Point(80, 310), "15", null)
        val v16 = new Vertex(16, Point(15, 240), "16", null)
        val v17 = new Vertex(17, Point(15, 300), "17", null)
        val v18 = new Vertex(18, Point(400, 15), "18", null)
        val v19 = new Vertex(19, Point(400, 120), "19", null)

        v0.neighbours = List(v1, v2, v9, v11, v16)
        v1.neighbours = List(v0, v5, v6)
        v2.neighbours = List(v0, v3, v5, v6, v8)
        v3.neighbours = List(v2, v4, v5, v11)
        v4.neighbours = List(v3, v7, v8, v11)
        v5.neighbours = List(v1, v2, v3, v6, v12)
        v6.neighbours = List(v1, v2, v5, v7, v9)
        v7.neighbours = List(v4, v6, v9)
        v8.neighbours = List(v2, v4, v9, v16)
        v9.neighbours = List(v0, v6, v7, v8, v10, v13, v15)
        v10.neighbours = List(v9, v11, v12)
        v11.neighbours = List(v0, v3, v4, v10, v13, v18, v19)
        v12.neighbours = List(v5, v10, v13)
        v13.neighbours = List(v9, v11, v12, v14, v19)
        v14.neighbours = List(v13)
        v15.neighbours = List(v9, v16, v17)
        v16.neighbours = List(v0, v8, v15, v17)
        v17.neighbours = List(v15, v16)
        v18.neighbours = List(v11)
        v19.neighbours = List(v11, v13)

        List(v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19)
    }
    
    def getGraph: List[Vertex] = {
        if(vertices == null)
            vertices = init()
        vertices
    }

    def setVertexSelection(vertex: Vertex, selected: Boolean): Boolean = {
        if (vertex.selected != selected) {
            selectedVertexCount += (if (selected) 1 else -1)
            vertex.selected = selected
            true
        } else {
            false
        }
    }

    def selectVertex(vertex: Vertex): Boolean = {
        setVertexSelection(vertex, true)
    }

    def deselectVertex(vertex: Vertex): Boolean = {
        setVertexSelection(vertex, false)
    }

    def invertVertexSelection(vertex: Vertex): Boolean = {
        setVertexSelection(vertex, !vertex.selected)
    }

    def deselectAll(graph: Graph): Boolean = {
        var somethingChanged = false
        if (selectedVertexCount > 0) {
            graph.getGraph.foreach{ vertex =>
                somethingChanged = deselectVertex(vertex) || somethingChanged
            }
        }
        somethingChanged
    }

    def getTouchedVertex(p: Point): Option[Vertex] = {
        val bottomRight = Vector(VertexWidth / 2, VertexHeight / 2)
        val topLeft = bottomRight.multiply(-1)
        vertices.find { vertex => isPointInRect(p, vertex.position.add(topLeft), vertex.position.add(bottomRight))
        }
    }

    def isPointInRect(p: Point, topLeft: Point, bottomRight: Point): Boolean = {
        p.x >= topLeft.x && p.x <= bottomRight.x && p.y >= topLeft.y && p.y <= bottomRight.y
    }
    
    def exists(value: Vertex): Boolean = {
        getGraph.exists{ value =>
            var exists = false
            getGraph.foreach{ vertex =>
                if(value.id == vertex.id)
                    exists = true
            }
            exists
        }
    }
}