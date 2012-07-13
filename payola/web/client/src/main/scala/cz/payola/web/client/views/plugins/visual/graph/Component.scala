package cz.payola.web.client.views.plugins.visual.graph

import collection.mutable.ListBuffer
import cz.payola.web.client.views._

class Component(val vertexViews: ListBuffer[VertexView], val edgeViews: ListBuffer[EdgeView]) {

    private var selectedVertexViews = vertexViews.filter(_.selected)

    def getSelectedCount: Int = {
        selectedVertexViews.length
    }

    def isEmpty: Boolean = {
        vertexViews.isEmpty //TODO may be length == 0
    }

    def getLeftmostPosition(): Point2D = {
        var result = vertexViews.head.position

        vertexViews.foreach{ vertexView =>
            if(result.x < vertexView.position.x) {
                result = vertexView.position
            }
        }

        result
    }

    def getBottomRight(): Point2D = {
        var bottom = Double.MinValue
        var right = Double.MinValue

        var vals = ""
        vertexViews.foreach{ vertexView =>
            if(bottom < vertexView.position.y) {
                bottom = vertexView.position.y
            }
            if(right < vertexView.position.x) {
                right = vertexView.position.x
            }
            vals += vertexView.position.toString + " "
        }

        Point2D(right, bottom)
    }

    def getBottomLeft(): Point2D = {
        var bottom = Double.MinValue
        var left = Double.MaxValue

        vertexViews.foreach{ vertexView =>
            if(bottom < vertexView.position.y) {
                bottom = vertexView.position.y
            }
            if(left > vertexView.position.x) {
                left = vertexView.position.x
            }
        }

        Point2D(left, bottom)
    }

    def getTopRight(): Point2D = {
        var top = Double.MaxValue
        var right = Double.MinValue

        vertexViews.foreach{ vertexView =>
            if(top > vertexView.position.y) {
                top = vertexView.position.y
            }
            if(right < vertexView.position.x) {
                right = vertexView.position.x
            }
        }

        Point2D(right, top)
    }

    def getTopLeft(): Point2D = {
        var top = Double.MaxValue
        var left = Double.MaxValue

        vertexViews.foreach{ vertexView =>
            if(top > vertexView.position.y) {
                top = vertexView.position.y
            }
            if(left > vertexView.position.x) {
                left = vertexView.position.x
            }
        }

        Point2D(left, top)
    }

    def getTopmostPosition(): Point2D = {
        var result = vertexViews.head.position

        vertexViews.foreach{ vertexView =>
            if(result.y > vertexView.position.y) {
                result = vertexView.position
            }
        }

        result
    }

    def getRightmostPosition(): Point2D = {
        var result = vertexViews.head.position

        vertexViews.foreach{ vertexView =>
            if(result.x > vertexView.position.x) {
                result = vertexView.position
            }
        }

        result
    }

    def getBottommostPosition(): Point2D = {
        var result = vertexViews.head.position

        vertexViews.foreach{ vertexView =>
            if(result.y < vertexView.position.y) {
                result = vertexView.position
            }
        }

        result
    }

    def moveAllSelectedVertices(difference: Vector2D) {
        selectedVertexViews.foreach { vertexView =>
            if(vertexView.selected) {
                vertexView.position += difference
            }
        }
    }

    def moveAllVertices(difference: Vector2D) {
        vertexViews.foreach { vertexView =>
            vertexView.position += difference
        }
    }

    def deselectAll(): ListBuffer[VertexView] = {

        var deselected = ListBuffer[VertexView]()
        selectedVertexViews.foreach{ vertex =>
            deselected += vertex
        }

        while(!selectedVertexViews.isEmpty) {
            deselectVertex(selectedVertexViews.head)
        }

        deselected
    }


    //###################################################################################################################
    //selection and stuff################################################################################################
    //###################################################################################################################

    /**
     * Finds a vertex in this graphs vertexViews container, that has the input point inside its graphical
     * (rectangular) representation.
     * @param point to compare the locations of vertices with
     * @return vertexView, that has the input point "inside", if none is found None
     */
    def getTouchedVertex(point: Point2D): Option[VertexView] = {

        vertexViews.find(v => v.isPointInside(point))
    }

    /**
     * Marks the input vertex as NOT selected by calling setVertexSelection(vertex, false)
     * @param vertex to change its selected attribute
     * @return true if the vertex is in this component and the selected attribute of the vertex has changed
     */
    def deselectVertex(vertex: VertexView): Boolean = {
        if(vertexViews.find(_.vertexModel eq vertex.vertexModel).isDefined) {
            setVertexSelection(vertex, false)
        } else {
            false
        }
    }

    /**
     * Marks the input vertex as selected by calling setVertexSelection(vertex, true)
     * @param vertex to change its selected attribute
     * @return true if the vertex is in this component and the selected attribute of the vertex has changed
     */
    def selectVertex(vertex: VertexView): Boolean = {
        setVertexSelection(vertex, true)
    }

    /**
     * Switches the selected attribute of the input vertex to the opposite value.
     * @param vertex to switch its selected attribute
     * @return true if the vertex is in this component and the selected attribute has changed
     */
    def invertVertexSelection(vertex: VertexView): Boolean = {
        if(!vertex.selected || vertexViews.find(_.vertexModel eq vertex.vertexModel).isDefined) {
            setVertexSelection(vertex, !vertex.selected)
        } else {
            false
        }
    }

    /**
     * Setter of the selected attribute for vertices.
     * @param vertex to which is the selected value is set
     * @param selected new value to be set to the vertex
     * @return true if the value of the vertex.selected attribute is changed
     */
    def setVertexSelection(vertex: VertexView, selected: Boolean): Boolean = {

        if (vertexViews.find(_.vertexModel eq vertex.vertexModel).isDefined && //check if the vertex is from this component
            vertex.selected != selected) {

            if(selected) {
                selectedVertexViews += vertex
            } else {
                selectedVertexViews -= vertex
            }
            vertex.selected = selected
            true
        } else {
            false
        }
    }
}
