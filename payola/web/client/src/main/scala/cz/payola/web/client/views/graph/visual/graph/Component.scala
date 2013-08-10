package cz.payola.web.client.views.graph.visual.graph

import collection.mutable.ListBuffer
import cz.payola.web.client.views.algebra._
import cz.payola.common.rdf._
import cz.payola.web.client.models.PrefixApplier

/**
 * Representation of a graph component (part of all vertices and edges from which does not exist a path to another
 * component of the graph)
 * @param vertexViewElements contained in this component
 * @param edgeViews contained in this component
 * @param componentNumber determines position of this component in whole graph (can be
 */
class Component(private var _vertexViewElements: ListBuffer[VertexViewElement], val edgeViews: ListBuffer[EdgeView],
    val componentNumber: Int, prefixApplier: Option[PrefixApplier])
{
    /**
     * vertexViews marked as selected
     */
    private var selectedVertexViews = vertexViewElements.filter(_.isSelected)

    def vertexViewElements = _vertexViewElements

    private def vertexViewElements_=(newVerticesList: ListBuffer[VertexViewElement]) {
        _vertexViewElements = newVerticesList
    }
    /**
     * Getter of count of selected vertexViews in this component
     * @return selected vertexViews count
     */
    def getSelectedCount: Int = {
        selectedVertexViews.length
    }

    def getSelected: List[VertexViewElement] = {
        selectedVertexViews.toList
    }


    /**
     * @return true if the component contains any vertexViews
     */
    def isEmpty: Boolean = {
        vertexViewElements.isEmpty
    }

    /**
     * Moves the parameter vertex to top of the inner list of vertices. If the vertex is contained in a group
     * the whole group is moved to the top.
     * @param vertex
     * @return
     */
    def moveVertexToTop(vertex: Vertex): Boolean = {
        val res = vertexViewElements.find(_.represents(vertex))

        if (res.isDefined) {
            vertexViewElements -= res.get
            vertexViewElements.prepend(res.get)
            true
        } else {
            false
        }

    }

    def createGroup(newPosition: Point2D): Boolean = {

        var selected = ListBuffer[VertexViewElement]()

        getSelected.foreach { viewElement => selected += viewElement } //clone

        val group = new VertexViewGroup(newPosition, prefixApplier)
        group.addVertices(selected.toList, edgeViews.toList)

        deselectAll() //empty the selected container

        //replace vertexViewElements with the new group
        vertexViewElements --= selected
        vertexViewElements += group

        //all (previously) selected vertexViews are now in the group -> select the group
        selectVertex(group)

        getSelected.isEmpty
    }

    def removeFromGroup(vertex: VertexViewElement, newPosition: Point2D): Boolean = {
        var groupsToRemove = ListBuffer[VertexViewElement]()
        var verticesToSelect = ListBuffer[VertexViewElement]()

        var allGroups = ListBuffer[VertexViewGroup]()
        vertexViewElements.foreach (_ match {case i: VertexViewGroup => allGroups += i} )

        val smthRemoved = vertexViewElements.find{ element =>
            element match {
                case group: VertexViewGroup =>
                    val isContained = group.contains(vertex)
                    if (isContained) {
                        allGroups -= group
                        group.vertexViews.foreach (_ match {case i: VertexViewGroup => allGroups += i} )

                        group.removeVertex(vertex, edgeViews.toList, allGroups.toList)
                        vertex.position = newPosition
                        vertexViewElements += vertex
                        verticesToSelect += vertex

                        if (group.vertexViews.length < 2) { //delete group if there is only one vertex
                            deselectVertex(group) //the group is marked selected and in the container of selected vertices

                            allGroups = ListBuffer[VertexViewGroup]()
                            group.vertexViews.foreach (_ match {case i: VertexViewGroup => allGroups += i} )

                            val removed = group.removeAll(edgeViews.toList, allGroups.toList)
                            removed.foreach(_.position = newPosition)
                            vertexViewElements ++= removed

                            verticesToSelect ++= removed

                            groupsToRemove += group
                            group.destroy()
                        }
                    }
                    isContained
                case _ =>
                    false
            }
        }.isDefined

        vertexViewElements --= groupsToRemove
        verticesToSelect.foreach(vertexToSelect => selectVertex(vertexToSelect))
        smthRemoved
    }

    /**
     * @return position of the bottom right corner of the rectangle that contains all vertexViews in this component
     */
    def getBottomRight(): Point2D = {
        var bottom = Double.MinValue
        var right = Double.MinValue

        vertexViewElements.foreach { vertexViewElement =>
            if (bottom < vertexViewElement.position.y) {
                bottom = vertexViewElement.position.y
            }
            if (right < vertexViewElement.position.x) {
                right = vertexViewElement.position.x
            }
        }

        Point2D(right, bottom)
    }

    /**
     * @return position of the bottom left corner of the rectangle that contains all vertexViews in this component
     */
    def getBottomLeft(): Point2D = {
        var bottom = Double.MinValue
        var left = Double.MaxValue

        vertexViewElements.foreach { vertexViewElement =>
            if (bottom < vertexViewElement.position.y) {
                bottom = vertexViewElement.position.y
            }
            if (left > vertexViewElement.position.x) {
                left = vertexViewElement.position.x
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

        vertexViewElements.foreach { vertexViewElement =>
            if (top > vertexViewElement.position.y) {
                top = vertexViewElement.position.y
            }
            if (right < vertexViewElement.position.x) {
                right = vertexViewElement.position.x
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

        vertexViewElements.foreach { vertexViewElement =>
            if (top > vertexViewElement.position.y) {
                top = vertexViewElement.position.y
            }
            if (left > vertexViewElement.position.x) {
                left = vertexViewElement.position.x
            }
        }

        Point2D(left, top)
    }

    /**
     * @return vertexView's position with the lowest x-coordinate position
     */
    def getLeftmostPosition(): Point2D = {
        var result = vertexViewElements.head.position

        vertexViewElements.foreach { vertexViewElement =>
            if (result.x > vertexViewElement.position.x) {
                result = vertexViewElement.position
            }
        }

        result
    }

    /**
     * @return vertexView's position with the lowest y-coordinate position
     */
    def getTopmostPosition(): Point2D = {
        var result = vertexViewElements.head.position

        vertexViewElements.foreach { vertexViewElement =>
            if (result.y > vertexViewElement.position.y) {
                result = vertexViewElement.position
            }
        }

        result
    }

    /**
     * @return vertexView's position with the highest x-coordinate position
     */
    def getRightmostPosition(): Point2D = {
        var result = vertexViewElements.head.position

        vertexViewElements.foreach { vertexViewElement =>
            if (result.x < vertexViewElement.position.x) {
                result = vertexViewElement.position
            }
        }

        result
    }

    /**
     * @return vertexView's position with the highest y-coordinate position
     */
    def getBottommostPosition(): Point2D = {
        var result = vertexViewElements.head.position

        vertexViewElements.foreach { vertexViewElement =>
            if (result.y < vertexViewElement.position.y) {
                result = vertexViewElement.position
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
        selectedVertexViews.foreach { vertexViewElement =>
            if (vertexViewElement.isSelected) {
                vertexViewElement.position += difference
            }
        }
    }

    /**
     * Moves all vertexViews in this component by the difference vector2D
     * @param difference to move vertexViews by
     */
    def moveAllVertices(difference: Vector2D) {
        vertexViewElements.foreach { vertexViewElement =>
            vertexViewElement.position += difference
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
    def getTouchedVertex(point: Point2D): Option[VertexViewElement] = {
        vertexViewElements.find(v => v.isPointInside(point))
    }

    /**
     * Marks the input vertex as NOT selected by calling setVertexSelection(vertex, false)
     * @param vertex to change its selected attribute
     * @return true if the vertex is in this component and the selected attribute of the vertex has changed
     */
    def deselectVertex(vertex: VertexViewElement): Boolean = {
        if (vertexViewElements.find{vertexElement =>
                vertexElement.isEqual(vertex) || vertexElement.contains(vertex)
            }.isDefined) {
            setVertexSelection(vertex, false)
        } else {
            false
        }
    }

    /**
     * Sets all vertexViews attribute selected to false
     * @return vertexViews that were deselected by this
     */
    def deselectAll(): ListBuffer[VertexViewElement] = {
        val deselected = ListBuffer[VertexViewElement]()
        selectedVertexViews.foreach{ element => deselected += element }

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
    def selectVertex(vertex: VertexViewElement): Boolean = {
        setVertexSelection(vertex, true)
    }

    /**
     * @return true is all vertices are selected
     */
    def isSelected: Boolean = {
        vertexViewElements.find { vertex => !vertex.isSelected}.isEmpty
    }

    /**
     * Switches the selected attribute of the input vertex to the opposite value.
     * @param vertex to switch its selected attribute
     * @return true if the vertex is in this component and the selected attribute has changed
     */
    def invertVertexSelection(vertex: VertexViewElement): Boolean = {
        if (vertexViewElements.find{vertexElement =>
                vertexElement.isEqual(vertex) || vertexElement.contains(vertex)
            }.isDefined) {
            setVertexSelection(vertex, !vertex.isSelected)
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
    def setVertexSelection(vertex: VertexViewElement, selected: Boolean): Boolean = {
        if (vertexViewElements.find{vertexElement =>
                vertexElement.isEqual(vertex) || vertexElement.contains(vertex)
            }.isDefined && //check if the vertex is from this component
            vertex.isSelected != selected) {

            if (selected) {
                selectedVertexViews += vertex
            } else {
                selectedVertexViews -= vertex
            }
            vertex.setSelected(selected)
            true
        } else {
            false
        }
    }
}
