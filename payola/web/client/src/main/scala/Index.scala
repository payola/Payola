package cz.payola.web.client

import cz.payola.web.client.graph.Drawer
import graph.model.{Graph, Edge, Vertex}
import graph.views.{EdgeView, VertexView, GraphView}
import s2js.adapters.js.browser._
import s2js.adapters.js.dom._
import s2js.adapters.goog.events._
import s2js.adapters.goog.events.{EventType, BrowserEvent}

object Index {
    // TODO will be model
    var graph: GraphView = null;

    // TODO will be view.
    var drawer: Drawer = null;

    var selectionStart: Option[Point] = None

    var moveStart: Option[Point] = None

    def init() {
        // Initialize the drawer
        val edgesLayer = createLayer()
        val verticesLayer = createLayer()
        val textLayer = createLayer()
        drawer = new Drawer(edgesLayer, verticesLayer, textLayer);

        // Attach events to the canvas.
        val mouseLayer = createLayer()
        listen[BrowserEvent](mouseLayer.canvas, EventType.MOUSEDOWN, onMouseDown _)
        //listen[BrowserEvent](mouseLayer.canvas, EventType.MOUSEMOVE, onMouseMove _)
        listen[BrowserEvent](mouseLayer.canvas, EventType.MOUSEUP, onMouseUp _)

        // Initialize the graph and draw it.
        initGraph()
        //drawer.redraw(graph)
    }

    def createLayer(): Layer = {
        val canvas = document.createElement[Canvas]("canvas")
        val context = canvas.getContext[CanvasRenderingContext2D]("2d")
        val layer = new Layer(canvas, context)

        document.getElementById("canvas-holder").appendChild(canvas)
        layer.setSize(Vector(window.innerWidth, window.innerHeight))
        layer
    }

    def initGraph() {
        val v0 = new Vertex("0")
        val v1 = new Vertex("1")
        val v2 = new Vertex("2")
        val v3 = new Vertex("3")
        val v4 = new Vertex("4")
        val v5 = new Vertex("5")
        val v6 = new Vertex("6")
        val v7 = new Vertex("7")
        val v8 = new Vertex("8")
        val v9 = new Vertex("9")
        val v10 = new Vertex("10")
        val v11 = new Vertex("11")
        val v12 = new Vertex("12")
        val v13 = new Vertex("13")
        val v14 = new Vertex("14")
        val v15 = new Vertex("15")
        val v16 = new Vertex("16")
        val v17 = new Vertex("17")
        val v18 = new Vertex("18")
        val v19 = new Vertex("19")
        
        val vertices = List[Vertex](
            v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19
        )
        val edges = List[Edge](
            new Edge("0", v0, v1), new Edge("1", v0, v2), new Edge("2", v0, v9), new Edge("3", v0, v11), new Edge("4", v0, v16),
            new Edge("5", v1, v5), new Edge("6", v1, v6),
            new Edge("7", v2, v3), new Edge("8", v2, v5), new Edge("9", v2, v6), new Edge("10", v2, v8),
            new Edge("11", v3, v4), new Edge("12", v3, v5), new Edge("13", v3, v11),
            new Edge("14", v4, v7), new Edge("15", v4, v8), new Edge("16", v4, v11),
            new Edge("17", v5, v6), new Edge("18", v5, v12),
            new Edge("19", v6, v7), new Edge("20", v6, v9),
            new Edge("21", v7, v9),
            new Edge("22", v8, v9), new Edge("23", v8, v16),
            new Edge("24", v9, v10), new Edge("25", v9, v13), new Edge("26", v9, v15),
            new Edge("27", v10, v11), new Edge("28", v10, v12),
            new Edge("29", v11, v13), new Edge("30", v11, v18), new Edge("31", v11, v19),
            new Edge("32", v12, v13),
            new Edge("33", v13, v14), new Edge("34", v13, v19),
            new Edge("35", v15, v16), new Edge("36", v15, v17),
            new Edge("37", v16, v17)
        )
        
        val graphModel = new Graph(vertices, edges)


        val vertexPlk1 = new VertexView(0, new Point(1, 1), "plk1")
        val vertexPlk2 = new VertexView(1, new Point(10, 10), "plk1")
        val edgePlk = new EdgeView(vertexPlk1, vertexPlk2, "plk")
        val verticesPlk = List[VertexView](vertexPlk1, vertexPlk2)
        val edgesPlk = List[EdgeView](edgePlk)
            
        graph = new GraphView(graphModel, verticesPlk, edgesPlk)
        //graph.init()
    }

    def onMouseDown(event: BrowserEvent) {
        val position = Point(event.clientX, event.clientY)
        val vertex = graph.getTouchedVertex(position)
        var needsToRedraw = false;

        // Mouse down near a vertex.
        if (vertex.isDefined) {
            if (event.shiftKey) {
                needsToRedraw = graph.invertVertexSelection(vertex.get) || needsToRedraw
            } else {
                if (!vertex.get.selected) {
                    needsToRedraw = graph.deselectAll(graph)
                }
                moveStart = Some(position)
                needsToRedraw = graph.selectVertex(vertex.get) || needsToRedraw
            }

        // Mouse down somewhere in the inter-vertex space.
        } else {
            if (!event.shiftKey) {
                needsToRedraw = graph.deselectAll(graph)
            }
            selectionStart = Some(position)
        }

        if (needsToRedraw) {
            drawer.redraw(graph)
        }
    }

    /*def onMouseMove(event: BrowserEvent) {
        if (selectionStart.isDefined) {
            //TODO place to write "rectangle selection" code

        } else if (moveStart.isDefined) {
            val end = Point(event.clientX, event.clientY)
            val difference = end - moveStart.get
            
            graph.vertices.foreach { vertex =>
                if(vertex.selected) {
                    vertex.position += difference
                }
            }
            
            moveStart = Some(end)
            drawer.redraw(graph)
        }
    }*/
    
    def onMouseUp(event: BrowserEvent) {
        selectionStart = None
        moveStart = None
        drawer.redraw(graph)
    }
}
