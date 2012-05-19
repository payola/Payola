package cz.payola.web.client.views.plugins.visual.graph

import collection.mutable.ListBuffer
import s2js.adapters.js.dom.{Element, CanvasRenderingContext2D}
import cz.payola.common.rdf.{Vertex, Graph}
import cz.payola.web.client.views.plugins.visual._
import settings.components.visualsetup.VisualSetup
import s2js.adapters.js.browser.window

/**
  * Graphical representation of Graph object.
  * @param container the space where the graph should be visualised
  */
class GraphView(val container: Element, val settings: VisualSetup) extends View {

    /**
     * During update vertices with higher age than this value are removed from this graph.
     */
    private val vertexHighestAge = 2

    /*The order in which are layers created determines their "z coordinate"
(first created layer is on the bottom and last created one covers all the others).*/
    private val edgesDeselectedLayer = createLayer(container)

    private val edgesDeselectedTextLayer = createLayer(container)

    private val edgesSelectedLayer = createLayer(container)

    private val edgesSelectedTextLayer = createLayer(container)

    private val verticesDeselectedLayer = createLayer(container)

    private val verticesDeselectedTextLayer = createLayer(container)

    private val verticesSelectedLayer = createLayer(container)

    private val verticesSelectedTextLayer = createLayer(container)

    /**
      * Topmost layer to contain listeners, controls of the visualisation.
      */
    val controlsLayer = createLayer(container)

    private val layers = List(
        edgesDeselectedLayer, edgesDeselectedTextLayer, edgesSelectedLayer, edgesSelectedTextLayer,
        verticesDeselectedLayer, verticesDeselectedTextLayer, verticesSelectedLayer, verticesSelectedTextLayer,
        controlsLayer)

    val components = ListBuffer[Component]()


    //###################################################################################################################
    //graph construction and update routines#############################################################################
    //###################################################################################################################

    /**
      * Replaces the current graph with new one and marks all new vertices as selected.
      * @param graph to replace the current graph representation with
      */
    def update(graph: Graph) {

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


        //if there were some vertices in the graph before the update
        if(oldVertexViews.isEmpty) {
            components.foreach{ component =>
                component.selectVertex(component.vertexViews.head)
            }
        }
    }

    /**
     * splits vertices to components according to accessibility between vertices (two vertices are in the same component
     * only if a series of edges and vertices connecting them exists)
     * @param vertexViews
     * @param edgeViews
     */
    private def splitToComponents(vertexViews: ListBuffer[VertexView], edgeViews: ListBuffer[EdgeView]) {


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

            val vertexInNews = newVertexViews.find(_.vertexModel eq oldVertexView.vertexModel) //TODO may be dangerous

            if(vertexInNews.isDefined) {
                vertexInNews.get.selected = oldVertexView.selected
                vertexInNews.get.position = oldVertexView.position

            } else if(oldVertexView.getCurrentAge + 1 <= vertexHighestAge) { //filter out too old vertices

                oldVertexView.increaseCurrentAge()
                oldVertexView.edges = ListBuffer[EdgeView]()

                newOldVertexViews += oldVertexView
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

            val edgeInNews = newEdgeViews.find(_.edgeModel eq oldEdgeView.edgeModel) //TODO may be dangerous

            if(edgeInNews.isEmpty) {

                val origin = findVertexView(oldEdgeView.edgeModel.origin, vertexViews)
                val destination = findVertexView(oldEdgeView.edgeModel.destination, vertexViews)
                if(destination.isDefined && origin.isDefined) { //this may not happen always
                    newOldEdgeViews +=
                        new EdgeView(oldEdgeView.edgeModel, origin.get, destination.get, settings.edgesModel,
                        settings.textModel)
                }
            }
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

        edgeViews.filter { edgeView: EdgeView =>
            ((edgeView.originView.vertexModel eq vertexView.vertexModel) ||
                (edgeView.destinationView.vertexModel eq vertexView.vertexModel))
        }
    }

    /**
     * Finds a vertexView that is a representation of the vertexModel object.
     * @param vertexModel to be searched
     * @return found graphica; representation of the input vertex from the model
     */
    private def findVertexView(vertexModel: Vertex, vertexViews: ListBuffer[VertexView]): Option[VertexView] = {
        vertexViews.find(_.vertexModel eq vertexModel)
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

    def draw(context: CanvasRenderingContext2D, color: Option[Color], position: Option[Point]) {
        val positionCorrection = position.getOrElse(Point.Zero)

        var colorVertex: Option[Color] = Some(Color.Black)
        var colorText: Option[Color] = Some(Color.Black)

        val vertexViews = getAllVertices
        val edgeViews = getAllEdges

        vertexViews.foreach {vertexView =>

            if (color != None) {
                colorVertex = Some(color.get)
                colorText = Some(color.get)
            } else if (vertexView.selected) {
                colorVertex = Some(settings.vertexModel.colorHigh)
                colorText = Some(settings.textModel.color)
            } else if (edgeViews.exists(edgeView =>
                TODO_RenameThisMethod(edgeView, vertexView))) {
                colorVertex = Some(settings.vertexModel.colorMed)
                colorText = Some(settings.textModel.color)
            } else if (getAllSelectedVerticesCount == 0) {
                colorVertex = None
                colorText = Some(settings.textModel.color)
            } else {
                colorVertex = Some(settings.vertexModel.colorLow)
                colorText = Some(Color.Transparent)
            }

            if (vertexView.selected) {
                if (verticesSelectedLayer.cleared) {
                    vertexView.draw(verticesSelectedLayer.context, colorVertex, Some(positionCorrection))
                }
                if (verticesSelectedTextLayer.cleared) {
                    vertexView.drawInformation(verticesSelectedTextLayer.context, colorText,
                        Some(LocationDescriptor.getVertexInformationPosition(vertexView.position) +
                            positionCorrection.toVector))
                }
            } else {
                if (verticesDeselectedLayer.cleared) {
                    vertexView.draw(verticesDeselectedLayer.context, colorVertex, Some(positionCorrection))
                }
            }
        }

        edgeViews.foreach {edgeView =>

            val positionToUse = LocationDescriptor.getEdgeInformationPosition(
                edgeView.originView.position, edgeView.destinationView.position) + positionCorrection.toVector
            val colorToUseEdge = color
            val colorToUseText = if (color != None) {
                color
            } else if (edgeView.isSelected) {
                Some(settings.textModel.color)
            } else {
                None
            }


            if(edgeView.areBothVerticesSelected) {
                edgeView.information.setSelectedForDrawing()
            }

            if (edgeView.isSelected) {
                if (edgesSelectedLayer.cleared) {
                    edgeView.draw(edgesSelectedLayer.context, colorToUseEdge, Some(positionCorrection))
                }
                if (edgesSelectedTextLayer.cleared && edgeView.areBothVerticesSelected) {
                    edgeView.information.draw(edgesSelectedTextLayer.context, colorToUseText, Some(positionToUse))
                }
            } else {
                if (edgesDeselectedLayer.cleared) {
                    edgeView.draw(edgesDeselectedLayer.context, colorToUseEdge, Some(positionCorrection))
                }
                if (edgesDeselectedTextLayer.cleared && edgeView.areBothVerticesSelected) {
                    edgeView.information.draw(edgesDeselectedTextLayer.context, colorToUseText, Some(positionToUse))
                }
            }
        }

        layers.foreach(layer => layer.cleared = false)
    }

    private def TODO_RenameThisMethod(edgeView: EdgeView, vertexView: VertexView): Boolean = {
        (edgeView.originView.eq(vertexView) && edgeView.destinationView.selected) ||
            (edgeView.destinationView.eq(vertexView) && edgeView.originView.selected)
    }

    def drawQuick(context: CanvasRenderingContext2D, color: Option[Color], position: Option[Point]) {
        val positionCorrection = position.getOrElse(Point.Zero)

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

            if(vertexView.selected) {
                if(verticesSelectedLayer.cleared) {
                    vertexView.drawQuick(verticesSelectedLayer.context, colorToUseVertex, Some(positionCorrection))
                }
            } else {
                if(verticesDeselectedLayer.cleared) {
                    vertexView.drawQuick(verticesDeselectedLayer.context, colorToUseVertex, Some(positionCorrection))
                }
            }
        }

        edgeViews.foreach {edgeView =>

            val colorToUseEdge = color

            if (edgeView.isSelected) {
                if (edgesSelectedLayer.cleared) {
                    edgeView.draw(edgesSelectedLayer.context, colorToUseEdge, Some(positionCorrection))
                }
            } else {
                if (edgesDeselectedLayer.cleared) {
                    edgeView.draw(edgesDeselectedLayer.context, colorToUseEdge, Some(positionCorrection))
                }
            }
        }

        layers.foreach(layer => layer.cleared = false)
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
                clear(edgesSelectedLayer.context, Point.Zero, edgesSelectedLayer.getSize)
                edgesSelectedLayer.cleared = true
                clear(edgesSelectedTextLayer.context, Point.Zero, edgesSelectedTextLayer.getSize)
                edgesSelectedTextLayer.cleared = true
                clear(verticesSelectedLayer.context, Point.Zero, verticesSelectedLayer.getSize)
                verticesSelectedLayer.cleared = true
                clear(verticesSelectedTextLayer.context, Point.Zero, verticesSelectedTextLayer.getSize)
                verticesSelectedTextLayer.cleared = true

                draw(null, None, None)
                //^because elements are drawn into separate layers, redraw(..) does not know to which context to draw

            case RedrawOperation.Selection =>
                redrawAll()

            case RedrawOperation.Animation =>
                layers.foreach {layer =>
                    clear(layer.context, Point.Zero, layer.getSize)
                    layer.cleared = true
                }

                drawQuick(null, None, None)

            case _ =>
                redrawAll()
        }
    }

    /**
      * Redraws the whole graph from scratch. All layers are cleared and all elements of the graph are drawn again.
      */
    def redrawAll() {
        layers.foreach {layer =>
            clear(layer.context, Point.Zero, layer.getSize)
            layer.cleared = true
        }
        draw(null, None, None)
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

    /*private def diffVertex(from: ListBuffer[VertexView], of: ListBuffer[VertexView]): ListBuffer[VertexView] = {
        var result = ListBuffer[VertexView]()

        var pointerOuter = 0
        var pointerInner = 0
        var found = false

        while(pointerOuter < from.length) {
            pointerInner = 0
            found = false

            while(pointerInner < of.length && !found) {

                if(from(pointerOuter).vertexModel eq of(pointerInner).vertexModel) {
                    found = true
                }
                pointerInner += 1
            }

            if(!found) {
                result += from(pointerInner)
            }

            pointerOuter += 1
        }

        result
        TODO remove
    }*/

    /*private def diffEdge(from: ListBuffer[EdgeView], of: ListBuffer[EdgeView]): ListBuffer[EdgeView] = {
        var result = ListBuffer[EdgeView]()

        var pointerOuter = 0
        var pointerInner = 0
        var found = false

        while(pointerOuter < from.length) {
            pointerInner = 0
            found = false

            while(pointerInner < of.length && !found) {

                if(from(pointerOuter).edgeModel eq of(pointerInner).edgeModel) {
                    found = true
                }
                pointerInner += 1
            }

            if(!found) {
                result += from(pointerInner)
            }

            pointerOuter += 1
        }

        result

        TODO remove
    }*/
}
