package cz.payola.web.client.views.plugins.visual.graph

import collection.mutable.ListBuffer
import cz.payola.web.client.views.plugins.visual.{Point, Vector}
import s2js.adapters.js.browser.window

class Component(val vertexViews: ListBuffer[VertexView], val edgeViews: ListBuffer[EdgeView]) {

    private var selectedVertexViews = vertexViews.filter(_.selected)

    def getSelectedCount: Int = {
        selectedVertexViews.length
    }

    def isEmpty: Boolean = {
        vertexViews.isEmpty //TODO may be length == 0
    }

    def moveAllSelectedVertices(difference: Vector) {
        selectedVertexViews.foreach { vertexView =>
            if(vertexView.selected) {
                vertexView.position += difference
            }
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
    def getTouchedVertex(point: Point): Option[VertexView] = {

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
     * Setter of the selected atribute for vertices.
     * @param vertex to which is the selected value is set
     * @param selected new value to be set to the vertex
     * @return true if the value of the vertex.selected attribute is changed
     */
    private def setVertexSelection(vertex: VertexView, selected: Boolean): Boolean = {
        if (vertex.selected != selected) {
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
