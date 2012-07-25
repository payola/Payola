package cz.payola.web.client.views.graph.visual.graph

import collection.mutable.ListBuffer
import cz.payola.web.client.views.graph.visual.settings.components.visualsetup.VisualSetup
import cz.payola.common.rdf._
import cz.payola.web.client.views.graph.visual._
import cz.payola.web.client.views.algebra._
import cz.payola.common.entities.settings.OntologyCustomization
import s2js.adapters.js.browser.window

/**
  * Graphical representation of Graph object.
  * @param container the space where the graph should be visualised
  */
class GraphView(var settings: VisualSetup) extends View[CanvasPack] {
    /**
      * During update vertices with higher age than this value are removed from this graph.
      */
    private val vertexHighestAge = 2

    /**
      * Components containing vertices and their edges. A component is a part of graph, for which does not exist
      * a path via edges and vertices, that would connect it to any other component. Graph may consist of only one
      * component.
      */
    var components = ListBuffer[Component]()

    def setVisualSetup(newCustomization: Option[OntologyCustomization]) {
        settings.setOntologyCustomization(newCustomization)
        getAllEdges.foreach(_.settings.setOntologyCustomization(newCustomization))
        getAllVertices.foreach(_.settings.setOntologyCustomization(newCustomization))
    }

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
        if (oldVertexViews.isEmpty) {
            components.foreach { component =>
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
        while (!remainingVertices.isEmpty) {

            var currentVertex = remainingVertices.head
            remainingVertices -= currentVertex

            var currentNeighbours = ListBuffer[VertexView]()
            var currentComponentsVertices = ListBuffer[VertexView]()
            var currentComponentsEdges = ListBuffer[EdgeView]()

            var plk = true
            while (plk) {

                val neighbours = getNeighbours(currentVertex)

                currentNeighbours ++= neighbours

                currentNeighbours --= currentNeighbours -- remainingVertices
                //^remove vertices from currNeighb that are not present in remVerts

                remainingVertices -= currentVertex

                currentComponentsVertices += currentVertex

                currentComponentsEdges ++= currentVertex.edges -- currentComponentsEdges

                if (currentNeighbours.isEmpty) {
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

        ofVertex.edges.foreach { edgeOfCurrentVertex =>

            if (edgeOfCurrentVertex.originView.vertexModel eq ofVertex.vertexModel) {
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
        val literalVertices = ListBuffer[LiteralVertex]()

        graphModel.vertices.foreach { vertexModel =>

            vertexModel match {
                case i: IdentifiedVertex => {
                    val newVertexView = new VertexView(i, Point2D(300, 300)/*TODO center of drawing space*/,
                        settings.vertexModel, settings.textModel, null)

                    newVertexView.rdfType = getRdfTypeForVertexView(graphModel.edges, i)
                    newVertexView.setInformation(getInformationForVertexView(graphModel, i))


                    buffer += newVertexView
                }
                case i: LiteralVertex => {
                    literalVertices += i
                }
            }
        }

        addLiteralVerticesToVertexViews(graphModel, buffer, literalVertices)
    }

    private def getInformationForVertexView(graphModel: Graph, vertexModel: IdentifiedVertex): Option[Vertex] = {

        val foundEdge = graphModel.edges.find{ edge =>
            vertexModel.uri == edge.origin.uri && Edge.rdfLabelEdges.find( _ == edge.uri).isDefined
        }
        if(foundEdge.isDefined) {
            Some(foundEdge.get.destination)
        } else {
            None
        }
    }

    private def getRdfTypeForVertexView(edges: Seq[Edge], vertexModel: IdentifiedVertex): String = {
        var rdfTypeEdge: Option[Edge]= None
        edges.foreach{ edge =>

            if(edge.origin == vertexModel && edge.uri == Edge.rdfTypeEdge) {
                rdfTypeEdge = Some(edge)
            }
        }

        if(rdfTypeEdge.isDefined) {
            rdfTypeEdge.get.destination.toString //getting rdf type
        } else {
            ""
        }
    }

    private def addLiteralVerticesToVertexViews(graphModel: Graph,
        vertexViews: ListBuffer[VertexView], literalVertices: ListBuffer[LiteralVertex]): ListBuffer[VertexView] = {

        literalVertices.foreach { literalVertex =>
        // find edge by which the vertex is connected to the rest of the graph and add it to the identified vertex
        // on the other side of the edge

            val edgeToIdentVertex =
                graphModel.edges.find{ edge => (edge.origin == literalVertex || edge.destination == literalVertex) }
            if(edgeToIdentVertex.isDefined) {
                //get identified vertex neighbour
                val identNeighborVertex =
                    edgeToIdentVertex.get.origin match {
                        case i: LiteralVertex =>
                            edgeToIdentVertex.get.destination.asInstanceOf[IdentifiedVertex]
                        case i: IdentifiedVertex =>
                            i
                    }
                //find the view vertex of the identified vertex neighbour
                val identNeighbourVertexView =
                    vertexViews.find{ vertexView => vertexView.vertexModel == identNeighborVertex }
                if(identNeighbourVertexView.isDefined) {
                    identNeighbourVertexView.get.addLiteralVertex(literalVertex,
                        graphModel.edges.filter{
                            edge => (edge.origin == literalVertex || edge.destination == literalVertex) })
                } else {
                    //this should never happen
                }
            } else {
                //this should never happen
            }
        }
        vertexViews
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

        getAllVertices.foreach { oldVertexView =>

            val vertexInNews = allVertices.find {
                _ isEqual oldVertexView
            }

            if (vertexInNews.isDefined) {
                vertexInNews.get.selected = oldVertexView.selected
                vertexInNews.get.position = oldVertexView.position
            } else if (vertexInNews.isEmpty && oldVertexView.getCurrentAge + 1 <= vertexHighestAge) {
                //filter out too old vertices

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
      *                    that are supposed to be connected by the created edgeViews
      * @return container with packed
      */
    private def createEdgeViews(newGraphModel: Graph, vertexViews: ListBuffer[VertexView]): ListBuffer[EdgeView] = {
        if (vertexViews.isEmpty) {
            ListBuffer[EdgeView]()
        }
        val newEdgeViews = ListBuffer[EdgeView]()

        //create new edgeViews
        newGraphModel.edges.foreach { edgeModel =>
            createEdgeView(edgeModel, vertexViews).foreach {
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
        if (vertexViews.isEmpty) {
            ListBuffer[EdgeView]()
        }

        val newOldEdgeViews = ListBuffer[EdgeView]()
        getAllEdges.foreach { oldEdgeView =>

            val edgeInNews = newEdgeViews.find(_ isEqual oldEdgeView)

            if (edgeInNews.isEmpty) {

                createEdgeView(oldEdgeView.edgeModel, vertexViews).foreach {
                    created => newOldEdgeViews += created
                }
            }
        }
        newOldEdgeViews
    }

    private def createEdgeView(edgeModel: Edge, vertexViews: ListBuffer[VertexView]): Option[EdgeView] = {
        val origin = getVertexForEdgeConstruct(edgeModel.origin, vertexViews)
        val destination = getVertexForEdgeConstruct(edgeModel.destination, vertexViews)
        if (destination.isDefined && origin.isDefined) {
            val createdEdgeView = new EdgeView(
                edgeModel, origin.get, destination.get, settings)
            destination.get.edges += createdEdgeView
            origin.get.edges += createdEdgeView
            Some(createdEdgeView)
        } else {
            None
        }
    }

    private def getVertexForEdgeConstruct(vertex: Vertex, vertexViews: ListBuffer[VertexView]): Option[VertexView] = {
        val foundVertices = vertexViews.filter {
            _.vertexModel.toString eq vertex.toString
        }
        foundVertices.length match {
            case 0 =>
                None
            case 1 =>
                Some(foundVertices(0))
            case _ =>
                foundVertices(0).vertexModel match {
                    case i: LiteralVertex =>
                        foundVertices.find {
                            _.edges.length == 0
                        }
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

        while (result.isEmpty && componentsPointer < components.length) {
            result = components(componentsPointer).getTouchedVertex(position)
            componentsPointer += 1
        }

        result
    }

    def invertVertexSelection(vertexView: VertexView): Boolean = {
        var componentPointer = 0
        var selectionInverted = false

        while (!selectionInverted && componentPointer < components.length) {
            selectionInverted = components(componentPointer).invertVertexSelection(vertexView)
            componentPointer += 1
        }

        selectionInverted
    }

    def selectVertex(vertexView: VertexView): Boolean = {
        var componentPointer = 0
        var selectionChanged = false

        while (!selectionChanged && componentPointer < components.length) {
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
        components.foreach {
            _.moveAllSelectedVertices(difference)
        }
    }

    /**
      * Adds the input vector to positions of all vertices in this graph visualisation.
      * @param difference to move the vertices
      */
    def moveAllVertices(difference: Vector2D) {
        components.foreach {
            _.moveAllVertices(difference)
        }
    }

    /**
      * Marks all verices in this graph graphical representation as NOT selected.
      * @return list of deselected vertices if some were deselected, else None
      */
    def deselectAll() {
        //var deselected = ListBuffer[VertexView]()

        components.foreach { component =>

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

    def draw(canvasPack: CanvasPack, positionCorrection: Vector2D) {

        val vertexViews = getAllVertices
        val edgeViews = getAllEdges

        var selectedVerticesDrawn = false
        var deselectedVerticesDrawn = false
        vertexViews.foreach { vertexView =>

            if(vertexView.isSelected) {
                if(canvasPack.verticesSelected.isClear) {
                    vertexView.draw(canvasPack.verticesSelected.context, positionCorrection)
                    selectedVerticesDrawn = true
                }
            } else {
                if(canvasPack.verticesDeselected.isClear) {
                    vertexView.draw(canvasPack.verticesDeselected.context, positionCorrection)
                    deselectedVerticesDrawn = true
                }
            }
        }
        if(selectedVerticesDrawn) {
            canvasPack.verticesSelected.dirty()
        }
        if(deselectedVerticesDrawn) {
            canvasPack.verticesDeselected.dirty()
        }


        var selectedEdgesDrawn = false
        var deselectedEdgesDrawn = false
        edgeViews.foreach { edgeView =>

            if(edgeView.isSelected) {
                if(canvasPack.edgesSelected.isClear) {
                    edgeView.draw(canvasPack.edgesSelected.context, positionCorrection)
                    selectedEdgesDrawn = true
                }
            } else {
                if(canvasPack.edgesDeselected.isClear) {
                    edgeView.draw(canvasPack.edgesDeselected.context, positionCorrection)
                    deselectedEdgesDrawn = true
                }
            }
        }
        if(selectedEdgesDrawn) {
            canvasPack.edgesSelected.dirty()
        }
        if(deselectedEdgesDrawn) {
            canvasPack.edgesDeselected.dirty()
        }
    }

    private def TODO_RenameThisMethod(edgeView: EdgeView, vertexView: VertexView): Boolean = {
        (edgeView.originView.eq(vertexView) && edgeView.destinationView.selected) ||
            (edgeView.destinationView.eq(vertexView) && edgeView.originView.selected)
    }

    def drawQuick(canvasPack: CanvasPack, positionCorrection: Vector2D) {
        val vertexViews = getAllVertices
        val edgeViews = getAllEdges

        var selectedVerticesDrawn = false
        var deselectedVerticesDrawn = false
        vertexViews.foreach { vertexView =>

            if(vertexView.isSelected) {
                if(canvasPack.verticesSelected.isClear) {
                    vertexView.drawQuick(canvasPack.verticesSelected.context, positionCorrection)
                    selectedVerticesDrawn = true
                }
            } else {
                if(canvasPack.verticesDeselected.isClear) {
                    vertexView.drawQuick(canvasPack.verticesDeselected.context, positionCorrection)
                    deselectedVerticesDrawn = true
                }
            }
        }
        if(selectedVerticesDrawn) {
            canvasPack.verticesSelected.dirty()
        }
        if(deselectedVerticesDrawn) {
            canvasPack.verticesDeselected.dirty()
        }


        var selectedEdgesDrawn = false
        var deselectedEdgesDrawn = false
        edgeViews.foreach { edgeView =>

            if(edgeView.isSelected) {
                if(canvasPack.edgesSelected.isClear) {
                    edgeView.drawQuick(canvasPack.edgesSelected.context, positionCorrection)
                    selectedEdgesDrawn = true
                }
            } else {
                if(canvasPack.edgesDeselected.isClear) {
                    edgeView.drawQuick(canvasPack.edgesDeselected.context, positionCorrection)
                    deselectedEdgesDrawn = true
                }
            }
        }
        if(selectedEdgesDrawn) {
            canvasPack.edgesSelected.dirty()
        }
        if(deselectedEdgesDrawn) {
            canvasPack.edgesDeselected.dirty()
        }
    }

    /**
      * Prepares the layers for drawing and calls the draw routine of this graph based on the graphOperation parameter.
      * If the Movement operation is used only layers for selected objects are redrawn. If the selection operation
      * is used all layers are redrawn.
      * @param graphOperation specifying which layers should be redrawn
      */
    def redraw(canvasPack: CanvasPack, graphOperation: Int) {
        graphOperation match {
            case RedrawOperation.Movement =>
                canvasPack.clearForMovement()
                draw(canvasPack, Vector2D.Zero)
            //^because elements are drawn into separate layers, redraw(..) does not know to which context to draw

            case RedrawOperation.Selection =>
                redrawAll(canvasPack)

            case RedrawOperation.Animation =>
                canvasPack.clear()
                drawQuick(canvasPack, Vector2D.Zero)

            case RedrawOperation.All =>
                redrawAll(canvasPack)

            case _ =>
                redrawAll(canvasPack)
        }
    }

    /**
      * Redraws the whole graph from scratch. All layers are cleared and all elements of the graph are drawn again.
      */
    def redrawAll(canvasPack: CanvasPack) {
        canvasPack.clear()
        draw(canvasPack, Vector2D.Zero)
        //^because elements are drawn into separate layers, redraw(..) does not know to which context to draw
    }

    //###################################################################################################################
    //component together putters#########################################################################################
    //###################################################################################################################

    def getAllVertices: ListBuffer[VertexView] = {
        var allVertices = ListBuffer[VertexView]()
        components.foreach { component =>
            allVertices ++= component.vertexViews
        }

        allVertices
    }

    def getAllEdges: ListBuffer[EdgeView] = {
        var allEdges = ListBuffer[EdgeView]()
        components.foreach { component =>
            allEdges ++= component.edgeViews
        }

        allEdges
    }

    private def getAllSelectedVerticesCount: Int = {
        var allSelectedCount = 0
        components.foreach { component =>
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
        components.foreach { component =>
            result = component.isEmpty && result
        }

        result
    }

    def getGraphCenter(): Point2D = {
        //() are intentional
        val top = getGraphTop.y
        val right = getGraphRight.x
        val bottom = getGraphBottom.y
        val left = getGraphLeft.x

        Point2D(left + (right - left) / 2, top + (bottom - top) / 2)
    }

    def getGraphTop: Point2D = {
        var top = Point2D(0, Double.MaxValue)
        getAllVertices.foreach { vv =>
            if (vv.position.y < top.y) {
                top = vv.position
            }
        }
        top
    }

    def getGraphLeft: Point2D = {
        var left = Point2D(Double.MaxValue, 0)
        getAllVertices.foreach { vv =>
            if (vv.position.x < left.x) {
                left = vv.position
            }
        }
        left
    }

    def getGraphBottom: Point2D = {
        var bottom = Point2D(0, Double.MinValue)
        getAllVertices.foreach { vv =>
            if (vv.position.y > bottom.y) {
                bottom = vv.position
            }
        }
        bottom
    }

    def getGraphRight: Point2D = {
        var right = Point2D(Double.MinValue, 0)
        getAllVertices.foreach { vv =>
            if (vv.position.x > right.x) {
                right = vv.position
            }
        }
        right
    }
}
