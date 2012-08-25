package cz.payola.web.client.views.graph.visual.graph

import collection.mutable.ListBuffer
import cz.payola.web.client.views.algebra._

/**
 * Representation of a graph component (part of all vertices and edges from which does not exist a path to another
 * component of the graph)
 * @param vertexViews contained in this component
 * @param edgeViews contained in this component
 * @param componentNumber determines position of this component in whole graph (can be
 */
class Component(val vertexViews: ListBuffer[VertexView], val edgeViews: ListBuffer[EdgeView], val componentNumber: Int)
{
    /**
     * vertexViews marked as selected
     */
    private var selectedVertexViews = vertexViews.filter(_.selected)

    /**
     * Getter of count of selected vertexViews in this component
     * @return selected vertexViews count
     */
    def getSelectedCount: Int = {
        selectedVertexViews.length
    }

    /**
     * @return true if the component contains any vertexViews
     */
    def isEmpty: Boolean = {
        vertexViews.isEmpty
    }

    /**
     * @return position of the bottom right corner of the rectangle that contains all vertexViews in this component
     */
    def getBottomRight(): Point2D = {
        var bottom = Double.MinValue
        var right = Double.MinValue

        var vals = ""
        vertexViews.foreach { vertexView =>
            if (bottom < vertexView.position.y) {
                bottom = vertexView.position.y
            }
            if (right < vertexView.position.x) {
                right = vertexView.position.x
            }
            vals += vertexView.position.toString + " "
        }

        Point2D(right, bottom)
    }

    /**
     * @return position of the bottom left corner of the rectangle that contains all vertexViews in this component
     */
    def getBottomLeft(): Point2D = {
        var bottom = Double.MinValue
        var left = Double.MaxValue

        vertexViews.foreach { vertexView =>
            if (bottom < vertexView.position.y) {
                bottom = vertexView.position.y
            }
            if (left > vertexView.position.x) {
                left = vertexView.position.x
            }
        }

        Point2D(left, bottom)
    }

    /**
     * @return position of the top right corner of the rectangle that contains all vertexViews in this component
     */
    def getTopRight(): Point2D = {
        var top = Double.MaxValue
        var right = Double.MinValue

        vertexViews.foreach { vertexView =>
            if (top > vertexView.position.y) {
                top = vertexView.position.y
            }
            if (right < vertexView.position.x) {
                right = vertexView.position.x
            }
        }

        Point2D(right, top)
    }

    /**
     * @return position of the top left corner of the rectangle that contains all vertexViews in this component
     */
    def getTopLeft(): Point2D = {
        var top = Double.MaxValue
        var left = Double.MaxValue

        vertexViews.foreach { vertexView =>
            if (top > vertexView.position.y) {
                top = vertexView.position.y
            }
            if (left > vertexView.position.x) {
                left = vertexView.position.x
            }
        }

        Point2D(left, top)
    }

    /**
     * @return vertexView's position with the lowest x-coordinate position
     */
    def getLeftmostPosition(): Point2D = {
        var result = vertexViews.head.position

        vertexViews.foreach { vertexView =>
            if (result.x > vertexView.position.x) {
                result = vertexView.position
            }
        }

        result
    }

    /**
     * @return vertexView's position with the lowest y-coordinate position
     */
    def getTopmostPosition(): Point2D = {
        var result = vertexViews.head.position

        vertexViews.foreach { vertexView =>
            if (result.y > vertexView.position.y) {
                result = vertexView.position
            }
        }

        result
    }

    /**
     * @return vertexView's position with the highest x-coordinate position
     */
    def getRightmostPosition(): Point2D = {
        var result = vertexViews.head.position

        vertexViews.foreach { vertexView =>
            if (result.x < vertexView.position.x) {
                result = vertexView.position
            }
        }

        result
    }

    /**
     * @return vertexView's position with the highest y-coordinate position
     */
    def getBottommostPosition(): Point2D = {
        var result = vertexViews.head.position

        vertexViews.foreach { vertexView =>
            if (result.y < vertexView.position.y) {
                result = vertexView.position
            }
        }

        result
    }

    def getCenter(): Point2D = {
        val topLeft = getTopLeft()
        val bottomRight = getBottomRight()

        Point2D(topLeft.x + (bottomRight.x - topLeft.x) / 2, topLeft.y + (bottomRight.y - topLeft.y) / 2)
    }

    /**
     * Moves all selected vertexViews in this component by the difference vector2D
     * @param difference to move vertexViews by
     */
    def moveAllSelectedVertices(difference: Vector2D) {
        selectedVertexViews.foreach { vertexView =>
            if (vertexView.selected) {
                vertexView.position += difference
            }
        }
    }

    /**
     * Moves all vertexViews in this component by the difference vector2D
     * @param difference to move vertexViews by
     */
    def moveAllVertices(difference: Vector2D) {
        vertexViews.foreach { vertexView =>
            vertexView.position += difference
        }
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
        if (vertexViews.find(_.vertexModel eq vertex.vertexModel).isDefined) {
            setVertexSelection(vertex, false)
        } else {
            false
        }
    }

    /**
     * Sets all vertexViews attribute selected to false
     * @return vertexViews that were deselected by this
     */
    def deselectAll(): ListBuffer[VertexView] = {
        var deselected = ListBuffer[VertexView]()
        selectedVertexViews.foreach { vertex =>
            deselected += vertex
        }

        while (!selectedVertexViews.isEmpty) {
            deselectVertex(selectedVertexViews.head)
        }

        deselected
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
     * @return true is all vertices are selected
     */
    def isSelected: Boolean = {
        vertexViews.find { vertexView => !vertexView.isSelected}.isEmpty
    }

    /**
     * Switches the selected attribute of the input vertex to the opposite value.
     * @param vertex to switch its selected attribute
     * @return true if the vertex is in this component and the selected attribute has changed
     */
    def invertVertexSelection(vertex: VertexView): Boolean = {
        if (!vertex.selected || vertexViews.find(_.vertexModel eq vertex.vertexModel).isDefined) {
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
        if (vertexViews.find(_.vertexModel eq vertex.vertexModel)
            .isDefined && //check if the vertex is from this component
            vertex.selected != selected) {

            if (selected) {
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
