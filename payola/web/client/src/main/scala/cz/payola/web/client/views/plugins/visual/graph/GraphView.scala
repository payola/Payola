package cz.payola.web.client.views.plugins.visual.graph

import collection.mutable.ListBuffer
import s2js.adapters.js.dom.{Element, CanvasRenderingContext2D}
import cz.payola.web.client.views.plugins.visual._
import settings.components.visualsetup.VisualSetup
import s2js.adapters.js.browser.window
import cz.payola.web.client.mvvm_api.element.CanvasPack
import cz.payola.common.rdf.{IdentifiedVertex, Vertex, Graph}

/**
  * Graphical representation of Graph object.
  * @param container the space where the graph should be visualised
  */
class GraphView(val container: Element, val settings: VisualSetup) extends View {

    /**
     * During update vertices with higher age than this value are removed from this graph.
     */
    private val vertexHighestAge = 2

    val canvasPack = createCanvasPack(container)

    /**
     * Components containing vertices and their edges. A component is a part of graph, for which does not exist
     * a path via edges and vertices, that would connect it to any other component. Graph may consist of only one
     * component.
     */
    var components = ListBuffer[Component]()

    def isSelected: Boolean = {
        false
    }

    //###################################################################################################################
    //graph construction and update routines#############################################################################
    //###################################################################################################################

    /**
      * Replaces the current graph with new one and marks all new vertices as selected.
      * @param graph to replace the current graph representation with
      */
    def update(graph: Graph) {
        if (graph == null) {
            return
            window.alert("graph is null and I still continue in GraphView.update")
        }

        //create vertexViews from the input
        val newVertexViews = createVertexViews(graph)
        //get vertexViews from the current (this) graphView
        val oldVertexViews = rebuildOldVertices(newVertexViews)

        //window.alert("new vertices length: "+newVertexViews.length+"; old vertices length: "+oldVertexViews.length)
        var vertexViews = newVertexViews ++ oldVertexViews
        //window.alert("new vertices length: "+newVertexViews.length+"; rebuild vertices length: "+oldVertexViews.length+"; old vertices length: "+getAllVertices.length)
        vertexViews = zkusNajitDuplicityV(vertexViews)

        //create edgeViews from the input
        val newEdgeViews = createEdgeViews(graph, vertexViews)
        //get edgeViews from the current (this) graphView
        val oldEdgeViews = rebuildOldEdges(newEdgeViews, vertexViews)

        //window.alert("new edges length: "+newEdgeViews.length+"; rebuild edges length: "+oldEdgeViews.length+"; old edges length: "+getAllEdges.length)
        var edgeViews = newEdgeViews ++ oldEdgeViews
        edgeViews = zkusNajitDuplicityE(edgeViews)

        fillVertexViewsEdges(vertexViews, edgeViews)


        splitToComponents(vertexViews, edgeViews)


        //if this is the first drawn graph, make some vertices selected from the start
        if(oldVertexViews.isEmpty) {
            components.foreach{ component =>
                component.selectVertex(component.vertexViews.head)
            }
        }
    }

    private def zkusNajitDuplicityV(vertexViews: ListBuffer[VertexView]): ListBuffer[VertexView] = {
        var ble = ListBuffer[VertexView]()
        var p1 = 0
        while(p1 < vertexViews.length) {
            var p2 = p1 + 1
            var duplicita = false
            while(p2 < vertexViews.length) {
                if(vertexViews(p1).vertexModel.toString eq vertexViews(p2).vertexModel.toString) {
                    //window.alert("duplicita: "+vertexViews(p1).toString+" == "+vertexViews(p2).toString)
                    duplicita = true
                }
                p2 += 1
            }
            if(!duplicita) {
                ble += vertexViews(p1)
            }
            p1 += 1
        }
        ble
    }

    private def zkusNajitDuplicityE(edgeViews: ListBuffer[EdgeView]) : ListBuffer[EdgeView] = {
        var ble = ListBuffer[EdgeView]()
        var p1 = 0
        while(p1 < edgeViews.length) {
            var p2 = p1 + 1
            var duplicita = false
            while(p2 < edgeViews.length) {
                if((edgeViews(p1).edgeModel.toString eq edgeViews(p2).edgeModel.toString)
                    && (((edgeViews(p1).originView.vertexModel.toString eq edgeViews(p2).originView.vertexModel.toString)
                        && (edgeViews(p1).destinationView.vertexModel.toString eq edgeViews(p2).destinationView.vertexModel.toString))
                    ||((edgeViews(p1).originView.vertexModel.toString eq edgeViews(p2).destinationView.vertexModel.toString)
                        && (edgeViews(p1).destinationView.vertexModel.toString eq edgeViews(p2).originView.vertexModel.toString)))) {

                    duplicita = true
                }
                p2 += 1
            }
            if(!duplicita) {
                ble += edgeViews(p1)
            }
            p1 += 1
        }
        ble
    }

    /**
     * splits vertices to components according to accessibility between vertices (two vertices are in the same component
     * only if a series of edges and vertices connecting them exists)
     * @param vertexViews
     * @param edgeViews
     */
    private def splitToComponents(vertexViews: ListBuffer[VertexView], edgeViews: ListBuffer[EdgeView]) {

        components = ListBuffer[Component]()

        var remainingVertices = vertexViews
        while(!remainingVertices.isEmpty) {

            var currentVertex = remainingVertices.head
            remainingVertices -= currentVertex

            var currentNeighbours = ListBuffer[VertexView]()
            var currentComponentsVertices = ListBuffer[VertexView]()
            var currentComponentsEdges = ListBuffer[EdgeView]()

            var plk = true
            while(plk) {

                val neighbours = getNeighbours(currentVertex)

                currentNeighbours ++= neighbours

                currentNeighbours --= currentNeighbours -- remainingVertices //if there is an error, check this first
                //^remove vertices from currNeighb that are not present in remVerts

                remainingVertices -= currentVertex

                currentComponentsVertices += currentVertex

                currentComponentsEdges ++= currentVertex.edges -- currentComponentsEdges

                if(currentNeighbours.isEmpty) {
                    plk = false
                } else {
                    currentVertex = currentNeighbours.head
                    currentNeighbours -= currentVertex
                }
            }

            components += new Component(currentComponentsVertices, currentComponentsEdges)
        }

    }

    /**
     * returns list of vertices, that are neighbours to the ofVertex
     * @param ofVertex
     * @return
     */
    private def getNeighbours(ofVertex: VertexView): ListBuffer[VertexView] = {
        var neighbours = ListBuffer[VertexView]()

        ofVertex.edges.foreach{ edgeOfCurrentVertex =>

            if(edgeOfCurrentVertex.originView.vertexModel eq ofVertex.vertexModel) {
                neighbours += edgeOfCurrentVertex.destinationView
            } else {
                neighbours += edgeOfCurrentVertex.originView
            }
        }

        neighbours
    }

    /**
     * Constructs a list of vertexViews based on the _graphModel parameter.
     * @param _graphModel to build from
     * @return container with packed Vertex objects in VertexView objects
     */
    private def createVertexViews(_graphModel: Graph): ListBuffer[VertexView] = {

        val buffer = ListBuffer[VertexView]()
        var counter = 0

        _graphModel.vertices.foreach { vertexModel =>

            buffer += new VertexView(vertexModel, Point(300, 300), settings.vertexModel, settings.textModel)
            //TODO should be center of the canvas or something like that
            counter += 1
        }

        buffer
    }

    /**
     * Removes edges of the old vertices that are not too old (and are not in the newVertexViews container), that the
     * graph can be rebuild.
     * Vertices in the newVertexViews container are updated if they are found in the old edges (old edges found this way
     * are removed to be replaced by the ne vertices)
     * @param newVertexViews
     * @return
     */
    private def rebuildOldVertices(newVertexViews: ListBuffer[VertexView]): ListBuffer[VertexView] = {
        var newOldVertexViews = ListBuffer[VertexView]()

        getAllVertices.foreach{ oldVertexView =>

            val vertexInNews = newVertexViews.find{ _.vertexModel.toString eq oldVertexView.vertexModel.toString } //TODO may be dangerous

            /*if(vertexInNews.isDefined) {
                //vertexInNews.get.selected = oldVertexView.selected
                //vertexInNews.get.position = oldVertexView.position
            } else */ if(vertexInNews.isEmpty && oldVertexView.getCurrentAge + 1 <= vertexHighestAge) { //filter out too old vertices
                val plk = new VertexView(oldVertexView.vertexModel, Point(300, 300), settings.vertexModel, settings.textModel)
                plk.setCurrentAge(oldVertexView.getCurrentAge + 1)
                if(oldVertexView.selected) {
                    plk.selected = true
                }

                newOldVertexViews += plk
            }
        }

        newOldVertexViews
    }

    /**
     * Constructs a list of edgeViews based on the _graphModel and verticesView variables.
     * @param newGraphModel to build from
     * @param vertexViews list of vertexViews in which to search for vertexViews,
     * that are supposed to be connected by the created edgeViews
     * @return container with packed
     */
    private def createEdgeViews(newGraphModel: Graph, vertexViews: ListBuffer[VertexView]): ListBuffer[EdgeView] = {

        if(vertexViews.isEmpty) {
            ListBuffer[EdgeView]()
        }


        val newEdgeViews = ListBuffer[EdgeView]()

        //create new edgeViews
        newGraphModel.edges.foreach { edgeModel =>
            val origin = findVertexView(edgeModel.origin, vertexViews)
            val destination = findVertexView(edgeModel.destination, vertexViews)
            if(destination.isDefined && origin.isDefined) { //this should be successful always... hopefully :-)
                newEdgeViews += new EdgeView(edgeModel, origin.get, destination.get, settings.edgesModel,
                    settings.textModel)
            }
        }

        newEdgeViews
    }

    /**
     * For every edge of this graph a new edge is created if its origin and destination exist in the vertexViews
     * parameter. Container of these renewed "valid" edges is returned.
     * @param newEdgeViews
     * @param vertexViews
     * @return
     */
    private def rebuildOldEdges(newEdgeViews: ListBuffer[EdgeView], vertexViews: ListBuffer[VertexView]):
    ListBuffer[EdgeView] = {

        if(vertexViews.isEmpty) {
            ListBuffer[EdgeView]()
        }

        val newOldEdgeViews = ListBuffer[EdgeView]()
        getAllEdges.foreach{ oldEdgeView =>

            //val edgeInNews = newEdgeViews.find(_.edgeModel.toString eq oldEdgeView.edgeModel.toString) //TODO may be dangerous

            //if(edgeInNews.isEmpty) {

                val origin = findVertexView(oldEdgeView.edgeModel.origin, vertexViews)
                val destination = findVertexView(oldEdgeView.edgeModel.destination, vertexViews)
                if(destination.isDefined && origin.isDefined) { //this may not happen always
                    newOldEdgeViews +=
                        new EdgeView(oldEdgeView.edgeModel, origin.get, destination.get, settings.edgesModel,
                        settings.textModel)
                }
            //}
        }
        newOldEdgeViews
    }

    /**
     * finds all edges of every vertex, that have this vertex as origin or destination and set its vertex.edges
     * attribute
     * @param vertexViews
     * @param edgeViews
     */
    private def fillVertexViewsEdges(vertexViews: ListBuffer[VertexView], edgeViews: ListBuffer[EdgeView]) {

        vertexViews.foreach { vertexView =>
            vertexView.edges = getEdgesOfVertex(vertexView, edgeViews)
            if(vertexView.edges.length == 0) {
                window.alert("vertex: "+vertexView.vertexModel.toString+" ; "+edgeViews.toString())
            }
            //window.alert(vertexView.toString+": "+vertexView.edges.length+" "+vertexView.edges.toString())
        }
    }

    /**
     * Searches for all edges in the _edgeViews parameter, that have the vertexView parameter as its origin or
     * destination and returns all these edges in a container.
     * @param vertexView to searche the edges container for
     * @param edgeViews container of edges to search in
     * @return container fith found edges
     */
    private def getEdgesOfVertex(vertexView: VertexView, edgeViews: ListBuffer[EdgeView]): ListBuffer[EdgeView] = {

        edgeViews.filter { edgeView =>
            ((edgeView.originView.vertexModel.toString eq vertexView.vertexModel.toString) ||
                (edgeView.destinationView.vertexModel.toString eq vertexView.vertexModel.toString))
        }
    }

    /**
     * Finds a vertexView that is a representation of the vertexModel object.
     * @param vertexModel to be searched
     * @return found graphica; representation of the input vertex from the model
     */
    private def findVertexView(vertexModel: Vertex, vertexViews: ListBuffer[VertexView]): Option[VertexView] = {
        vertexViews.find(_.vertexModel.toString eq vertexModel.toString)
    }

    //###################################################################################################################
    //selection and whatever routines####################################################################################
    //###################################################################################################################

    def getTouchedVertex(position: Point): Option[VertexView] = {

        var result: Option[VertexView] = None
        var componentsPointer = 0

        while(result.isEmpty && componentsPointer < components.length) {
            result = components(componentsPointer).getTouchedVertex(position)
            componentsPointer += 1
        }

        result
    }

    def invertVertexSelection(vertexView: VertexView): Boolean = {

        var componentPointer = 0
        var selectionInverted = false

        while(!selectionInverted && componentPointer < components.length) {
            selectionInverted = components(componentPointer).invertVertexSelection(vertexView)
            componentPointer += 1
        }

        selectionInverted
    }

    def selectVertex(vertexView: VertexView): Boolean = {

        var componentPointer = 0
        var selectionChanged = false

        while(!selectionChanged && componentPointer < components.length) {
            selectionChanged = components(componentPointer).setVertexSelection(vertexView, true)
            componentPointer += 1
        }

        selectionChanged
    }

    /**
      * Adds the input vector to positions of all vertices in this graph visualisation.
      * @param difference to move the vertices
      */
    def moveAllSelectedVertices(difference: Vector) {
        components.foreach{
            _.moveAllSelectedVertices(difference)
        }
    }

    /**
      * Marks all verices in this graph graphical representation as NOT selected.
      * @return list of deselected vertices if some were deselected, else None
      */
    def deselectAll(): Option[ListBuffer[VertexView]] = {

        var deselected = ListBuffer[VertexView]()

        components.foreach{ component =>

            deselected ++= component.deselectAll()
        }

        if(deselected.isEmpty) {
            None
        } else {
            Some(deselected)
        }
    }

    //###################################################################################################################
    //drawing############################################################################################################
    //###################################################################################################################

    def draw(context: CanvasRenderingContext2D, color: Option[Color], positionCorrection: Vector) {

        var colorVertex: Option[Color] = Some(Color.Black)

        val vertexViews = getAllVertices
        val edgeViews = getAllEdges

        vertexViews.foreach {vertexView =>

            if (color != None) {
                colorVertex = Some(color.get)
            } else if (vertexView.isSelected) {
                colorVertex = Some(settings.vertexModel.colorHigh)
            } else if (edgeViews.exists(edgeView =>
                TODO_RenameThisMethod(edgeView, vertexView))) {
                colorVertex = Some(settings.vertexModel.colorMed)
            } else if (getAllSelectedVerticesCount == 0) {
                colorVertex = None
            } else {
                colorVertex = Some(settings.vertexModel.colorLow)
            }

            canvasPack.draw(vertexView, colorVertex, positionCorrection)
        }

        edgeViews.foreach {edgeView =>

            if(edgeView.areBothVerticesSelected) {
                edgeView.information.setSelectedForDrawing()
            }

            canvasPack.draw(edgeView, color, positionCorrection)
        }

        canvasPack.dirty()
    }

    private def TODO_RenameThisMethod(edgeView: EdgeView, vertexView: VertexView): Boolean = {
        (edgeView.originView.eq(vertexView) && edgeView.destinationView.selected) ||
            (edgeView.destinationView.eq(vertexView) && edgeView.originView.selected)
    }

    def drawQuick(context: CanvasRenderingContext2D, color: Option[Color], positionCorrection: Vector) {

        val vertexViews = getAllVertices
        val edgeViews = getAllEdges

        vertexViews.foreach { vertexView =>

            val colorToUseVertex = if (color != None) {
                Some(color.get)
            } else if (vertexView.selected) {
                Some(settings.vertexModel.colorHigh)
            } else {
                Some(settings.vertexModel.colorMed)
            }

            canvasPack.drawQuick(vertexView, colorToUseVertex, positionCorrection)
        }

        edgeViews.foreach {edgeView =>

            canvasPack.drawQuick(edgeView, color, positionCorrection)
        }

        canvasPack.dirty()
    }

    /**
      * Prepares the layers for drawing and calls the draw routine of this graph based on the graphOperation parameter.
      * If the Movement operation is used only layers for selected objects are redrawn. If the selection operation
      * is used all layers are redrawn.
      * @param graphOperation specifying which layers should be redrawn
      */
    def redraw(graphOperation: Int) {
        graphOperation match {
            case RedrawOperation.Movement =>
                canvasPack.clearForMovement()
                draw(null, None, Vector.Zero)
                //^because elements are drawn into separate layers, redraw(..) does not know to which context to draw

            case RedrawOperation.Selection =>
                redrawAll()

            case RedrawOperation.Animation =>
                canvasPack.clear()
                drawQuick(null, None, Vector.Zero)

            case _ =>
                redrawAll()
        }
    }

    /**
      * Redraws the whole graph from scratch. All layers are cleared and all elements of the graph are drawn again.
      */
    def redrawAll() {
        canvasPack.clear()
        draw(null, None, Vector.Zero)
        //^because elements are drawn into separate layers, redraw(..) does not know to which context to draw
    }

    def clean() {
        while(container.childNodes.length > 0) {
            container.removeChild(container.firstChild)
        }
    }

    //###################################################################################################################
    //component together putters#########################################################################################
    //###################################################################################################################

    private def getAllVertices: ListBuffer[VertexView] = {
        var allVertices = ListBuffer[VertexView]()
        components.foreach{ component =>
            allVertices ++= component.vertexViews
        }

        allVertices
    }

    private def getAllEdges: ListBuffer[EdgeView] = {
        var allEdges = ListBuffer[EdgeView]()
        components.foreach{ component =>
            allEdges ++= component.edgeViews
        }

        allEdges
    }

    private def getAllSelectedVerticesCount: Int = {
        var allSelectedCount = 0
        components.foreach{ component =>
            allSelectedCount += component.getSelectedCount
        }
        allSelectedCount
    }

    /**
     * Empty graph indication function.
     * @return true if no vertices are present in this.vertexViews variable
     */
    def isEmpty: Boolean = {
        var result = true
        components.foreach{ component =>
            result = component.isEmpty && result
        }

        result
    }
}
