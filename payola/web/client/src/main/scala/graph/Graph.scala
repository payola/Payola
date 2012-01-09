package cz.payola.web.client.graph

import cz.payola.web.client.graph.Constants._
import cz.payola.web.client.{Vector, Point}

class Graph(val vertices: List[Vertex], val edges: List[Edge])
{
    private var selectedVertexCount = 0

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
            vertices.foreach{ vertex =>
                somethingChanged = deselectVertex(vertex) || somethingChanged
            }
        }
        somethingChanged
    }

    def containsSelectedVertex: Boolean = {
        selectedVertexCount > 0
    }

    def getTouchedVertex(p: Point): Option[Vertex] = {
        vertices.find(v => isPointInRect(p, v.position + (VertexSize / -2), v.position + (VertexSize / 2)))
    }

    def isPointInRect(p: Point, topLeft: Point, bottomRight: Point): Boolean = {
        p >= topLeft && p <= bottomRight
    }
    
    def exists(value: Vertex): Boolean = {
        vertices.exists(_.id == value.id)
        /* TODO Nechapu co to ma delat, ale pokud to ma testovat existenci vrcholu v grafu, tak je to blbe.
        getVertices.exists{ value =>
            var exists = false
            getVertices.foreach{ vertex =>
                if(value.id == vertex.id)
                    exists = true
            }
            exists
        }*/
    }
}
