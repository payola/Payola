package cz.payola.web.client.views.plugins.visual.graph

import collection.mutable.ListBuffer
import s2js.adapters.js.dom.{Element, CanvasRenderingContext2D}
import cz.payola.web.client.views.plugins.visual._
import settings.components.visualsetup.VisualSetup
import s2js.adapters.js.browser.window
import cz.payola.common.rdf._
import cz.payola.web.client.views._
import scala.Some

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
        }

        //create vertexViews from the input
        val newVertexViews = createVertexViews(graph)
        //get vertexViews from the current (this) graphView
        val oldVertexViews = rebuildOldVertices(newVertexViews)
        val vertexViews = newVertexViews ++ oldVertexViews


        //create edgeViews from the input
        val newEdgeViews = createEdgeViews(graph, vertexViews)
        //get edgeViews from the current (this) graphView
        val oldEdgeViews = rebuildOldEdges(newEdgeViews, vertexViews)
        val edgeViews = newEdgeViews ++ oldEdgeViews


        fillVertexViewsEdges(vertexViews, edgeViews)

        splitToComponents(vertexViews, edgeViews)

        //if this is the first drawn graph, make some vertices selected from the start
        if(oldVertexViews.isEmpty) {
            components.foreach{ component =>
                component.selectVertex(component.vertexViews.head)
            }
        }
    }

    /**
     * splits vertices to components according to accessibility between vertices (two vertices are in the same component
     * only if a series of edges and vertices connecting them exists)
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

                currentNeighbours --= currentNeighbours -- remainingVertices
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
     * @param graphModel to build from
     * @return container with packed Vertex objects in VertexView objects
     */
    private def createVertexViews(graphModel: Graph): ListBuffer[VertexView] = {

        val buffer = ListBuffer[VertexView]()
        var counter = 0

        graphModel.vertices.foreach { vertexModel =>

            buffer += new VertexView(vertexModel, Point2D(300, 300), settings.vertexModel, settings.textModel)
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
     */
    private def rebuildOldVertices(newVertexViews: ListBuffer[VertexView]): ListBuffer[VertexView] = {
        var newOldVertexViews = ListBuffer[VertexView]()
        var allVertices = newVertexViews ++ newOldVertexViews

        getAllVertices.foreach{ oldVertexView =>

            val vertexInNews = allVertices.find{ _ isEqual oldVertexView }

            if(vertexInNews.isDefined) {
                vertexInNews.get.selected = oldVertexView.selected
                vertexInNews.get.position = oldVertexView.position
            } else if(vertexInNews.isEmpty && oldVertexView.getCurrentAge + 1 <= vertexHighestAge) { //filter out too old vertices

                oldVertexView.increaseCurrentAge()
                newOldVertexViews += oldVertexView
                allVertices += oldVertexView
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
            createEdgeView(edgeModel, vertexViews).foreach{
                created => newEdgeViews += created
            }
        }

        newEdgeViews
    }

    /**
     * For every edge of this graph a new edge is created if its origin and destination exist in the vertexViews
     * parameter. Container of these renewed "valid" edges is returned.
     * @return
     */
    private def rebuildOldEdges(newEdgeViews: ListBuffer[EdgeView], vertexViews: ListBuffer[VertexView]):
    ListBuffer[EdgeView] = {

        if(vertexViews.isEmpty) {
            ListBuffer[EdgeView]()
        }

        val newOldEdgeViews = ListBuffer[EdgeView]()
        getAllEdges.foreach{ oldEdgeView =>

            val edgeInNews = newEdgeViews.find(_ isEqual oldEdgeView)

            if(edgeInNews.isEmpty) {

                createEdgeView(oldEdgeView.edgeModel, vertexViews).foreach{
                    created => newOldEdgeViews += created
                }
            }
        }
        newOldEdgeViews
    }

    private def createEdgeView(edgeModel: Edge, vertexViews: ListBuffer[VertexView]): Option[EdgeView] = {
        val origin = getVertexForEdgeConstruct(edgeModel.origin, vertexViews)
        val destination = getVertexForEdgeConstruct(edgeModel.destination, vertexViews)
        if(destination.isDefined && origin.isDefined) {
            val createdEdgeView = new EdgeView(
                edgeModel, origin.get, destination.get, settings.edgesModel, settings.textModel)
            destination.get.edges += createdEdgeView
            origin.get.edges += createdEdgeView
            Some(createdEdgeView)
        } else {
            None
        }
    }

    private def getVertexForEdgeConstruct(vertex: Vertex, vertexViews: ListBuffer[VertexView]): Option[VertexView] = {

        val foundVertices = vertexViews.filter{_.vertexModel.toString eq vertex.toString}
        foundVertices.length match {
            case 0 =>
                None
            case 1 =>
                Some(foundVertices(0))
            case _ =>
                foundVertices(0).vertexModel match {
                    case i: LiteralVertex =>
                        foundVertices.find{_.edges.length == 0}
                    case i: IdentifiedVertex =>
                    /*THIS SHOULD NEVER HAPPEN, entering this means, that server sent few identical
                      identifiedVertices that are supposed to be uniquely identified*/
                        None
                    case _ => /*ADD Vertex class children, that may be multiple times present in the graph*/
                        None
                }
        }
    }

    /**
     * finds all edges of every vertex, that have this vertex as origin or destination and set its vertex.edges
     * attribute
     */
    private def fillVertexViewsEdges(vertexViews: ListBuffer[VertexView], edgeViews: ListBuffer[EdgeView]) {

        vertexViews.foreach { vertexView =>
            vertexView.edges = getEdgesOfVertex(vertexView, edgeViews)
            /*if(vertexView.edges.length == 0) {
                window.alert("to vertex were set NO edges: "+vertexView.vertexModel.toString+" ; "+edgeViews.toString())
            }*/
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

    //###################################################################################################################
    //selection and whatever routines####################################################################################
    //###################################################################################################################

    def getTouchedVertex(position: Point2D): Option[VertexView] = {

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
            selectionChanged = components(componentPointer).selectVertex(vertexView)
            componentPointer += 1
        }

        selectionChanged
    }

    /**
      * Adds the input vector to positions of all selected vertices in this graph visualisation.
      * @param difference to move the vertices
      */
    def moveAllSelectedVertices(difference: Vector2D) {
        components.foreach{
            _.moveAllSelectedVertices(difference)
        }
    }

    /**
      * Adds the input vector to positions of all vertices in this graph visualisation.
      * @param difference to move the vertices
      */
    def moveAllVertices(difference: Vector2D) {
        components.foreach{
            _.moveAllVertices(difference)
        }
    }

    /**
      * Marks all verices in this graph graphical representation as NOT selected.
      * @return list of deselected vertices if some were deselected, else None
      */
    def deselectAll() {

        //var deselected = ListBuffer[VertexView]()

        components.foreach{ component =>

            component.deselectAll()
        }

        /*if(deselected.isEmpty) {
            None
        } else {
            Some(deselected)
        }*/
    }

    //###################################################################################################################
    //drawing############################################################################################################
    //###################################################################################################################

    def draw(context: CanvasRenderingContext2D, color: Option[Color], positionCorrection: Vector2D) {

        //fitCanvas()

        var colorVertex: Option[Color] = Some(Color.Black)

        val vertexViews = getAllVertices
        val edgeViews = getAllEdges

        vertexViews.foreach {vertexView =>

            if (color != None) {
                colorVertex = Some(color.get)
            } else if (vertexView.isSelected) {
                colorVertex = Some(settings.vertexModel.colorSelected)
            } else if (edgeViews.exists(edgeView =>
                TODO_RenameThisMethod(edgeView, vertexView))) {
//                colorVertex = Some(settings.vertexModel.colorMed)
            } else if (getAllSelectedVerticesCount == 0) {
                colorVertex = None
            } else {
                colorVertex = Some(settings.vertexModel.color)
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

    def drawQuick(context: CanvasRenderingContext2D, color: Option[Color], positionCorrection: Vector2D) {

        val vertexViews = getAllVertices
        val edgeViews = getAllEdges

        vertexViews.foreach { vertexView =>

            val colorToUseVertex = if (color != None) {
                Some(color.get)
            } else if (vertexView.selected) {
                Some(settings.vertexModel.colorSelected)
            } else {
                Some(settings.vertexModel.color)
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
                draw(null, None, Vector2D.Zero)
                //^because elements are drawn into separate layers, redraw(..) does not know to which context to draw

            case RedrawOperation.Selection =>
                redrawAll()

            case RedrawOperation.Animation =>
                canvasPack.clear()
                drawQuick(null, None, Vector2D.Zero)

            case RedrawOperation.All =>
                canvasPack.clear()
                redrawAll()

            case _ =>
                redrawAll()
        }
    }

    /**
      * Redraws the whole graph from scratch. All layers are cleared and all elements of the graph are drawn again.
      */
    def redrawAll() {
        canvasPack.clear()
        draw(null, None, Vector2D.Zero)
        //^because elements are drawn into separate layers, redraw(..) does not know to which context to draw
    }


    def destroy() {
        while(container.childNodes.length > 0) {
            container.removeChild(container.firstChild)
        }
    }

    //###################################################################################################################
    //component together putters#########################################################################################
    //###################################################################################################################

    def getAllVertices: ListBuffer[VertexView] = {
        var allVertices = ListBuffer[VertexView]()
        components.foreach{ component =>
            allVertices ++= component.vertexViews
        }

        allVertices
    }

    def getAllEdges: ListBuffer[EdgeView] = {
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

    def fitCanvas() {
        //measure size of the graph and set dimensions of the canvasPack accordingly (max(sizeOfTheWindow, sizeOfTheGraph))
        val maxBottomRight = Point2D(0, 0)
        components.foreach{ component =>
            val componentBR = component.getBottomRight()
            if(maxBottomRight.x < componentBR.x) {
                maxBottomRight.x = componentBR.x
            }
            if(maxBottomRight.y < componentBR.y) {
                maxBottomRight.y = componentBR.y
            }
        }
        canvasPack.size = Vector2D(window.innerWidth - canvasPack.offsetLeft, window.innerHeight - canvasPack.offsetTop)
    }

    def getGraphCenter(): Point2D = { //() are intentional
        val top = getGraphTop.y
        val right = getGraphRight.x
        val bottom = getGraphBottom.y
        val left = getGraphLeft.x

        Point2D(left + (right - left)/2, top + (bottom - top)/2)
    }

    def getGraphTop: Point2D = {
        var top = Point2D(0, Double.MaxValue)
        getAllVertices.foreach{ vv =>
            if(vv.position.y < top.y) {
                top = vv.position
            }
        }
        top
    }

    def getGraphLeft: Point2D = {
        var left = Point2D(Double.MaxValue, 0)
        getAllVertices.foreach{ vv =>
            if(vv.position.x < left.x) {
                left = vv.position
            }
        }
        left
    }

    def getGraphBottom: Point2D = {
        var bottom = Point2D(0, Double.MinValue)
        getAllVertices.foreach{ vv =>
            if(vv.position.y > bottom.y) {
                bottom = vv.position
            }
        }
        bottom
    }

    def getGraphRight: Point2D = {
        var right = Point2D(Double.MinValue, 0)
        getAllVertices.foreach{ vv =>
            if(vv.position.x > right.x) {
                right = vv.position
            }
        }
        right
    }
}
