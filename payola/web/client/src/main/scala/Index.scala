package cz.payola.web.client

import graph.{Edge, Vertex, Graph, Drawer}
import s2js.adapters.js.browser._
import s2js.adapters.js.dom._
import s2js.adapters.goog.events._
import s2js.adapters.goog.events.{EventType, BrowserEvent}

object Index {
    // TODO will be model
    var graph: Graph = null;

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
        listen[BrowserEvent](mouseLayer.canvas, EventType.MOUSEMOVE, onMouseMove _)
        listen[BrowserEvent](mouseLayer.canvas, EventType.MOUSEUP, onMouseUp _)

        // Initialize the graph and draw it.
        initGraph()
        drawer.redraw(graph)
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
        val v0 = new Vertex(0, Point(15, 15), "0")
        val v1 = new Vertex(1, Point(120, 40), "1")
        val v2 = new Vertex(2, Point(50, 120), "2")
        val v3 = new Vertex(3, Point(180, 60), "3")
        val v4 = new Vertex(4, Point(240, 110), "4")
        val v5 = new Vertex(5, Point(160, 160), "5")
        val v6 = new Vertex(6, Point(240, 240), "6")
        val v7 = new Vertex(7, Point(270, 320), "7")
        val v8 = new Vertex(8, Point(160, 240), "8")
        val v9 = new Vertex(9, Point(120, 400), "9")
        val v10 = new Vertex(10, Point(300, 80), "10")
        val v11 = new Vertex(11, Point(320, 30), "11")
        val v12 = new Vertex(12, Point(300, 200), "12")
        val v13 = new Vertex(13, Point(350, 210), "13")
        val v14 = new Vertex(14, Point(300, 400), "14")
        val v15 = new Vertex(15, Point(80, 310), "15")
        val v16 = new Vertex(16, Point(15, 240), "16")
        val v17 = new Vertex(17, Point(15, 300), "17")
        val v18 = new Vertex(18, Point(400, 15), "18")
        val v19 = new Vertex(19, Point(400, 120), "19")
        
        val vertices = List[Vertex](
            v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19
        )
        val edges = List[Edge](
            new Edge(v0, v1), new Edge(v0, v2), new Edge(v0, v9), new Edge(v0, v11), new Edge(v0, v16),
            new Edge(v1, v5), new Edge(v1, v6),
            new Edge(v2, v3), new Edge(v2, v5), new Edge(v2, v6), new Edge(v2, v8),
            new Edge(v3, v4), new Edge(v3, v5), new Edge(v3, v11),
            new Edge(v4, v7), new Edge(v4, v8), new Edge(v4, v11),
            new Edge(v5, v6), new Edge(v5, v12),
            new Edge(v6, v7), new Edge(v6, v9),
            new Edge(v7, v9),
            new Edge(v8, v9), new Edge(v8, v16),
            new Edge(v9, v10), new Edge(v9, v13), new Edge(v9, v15),
            new Edge(v10, v11), new Edge(v10, v12),
            new Edge(v11, v13), new Edge(v11, v18), new Edge(v11, v19),
            new Edge(v12, v13),
            new Edge(v13, v14), new Edge(v13, v19),
            new Edge(v15, v16), new Edge(v15, v17),
            new Edge(v16, v17)
        )
        
        graph = new Graph(vertices, edges)
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

    def onMouseMove(event: BrowserEvent) {
        if (selectionStart.isDefined) {
            /*
            if(scala.math.abs(select_rectOriginX - event.clientX) > mouse_moveToleration ||
                scala.math.abs(select_rectOriginY - event.clientY) > mouse_moveToleration) {
                mouse_useToleration = false
            }
            selectionByRect(event, graph)
            */

            /*
            val start = selectionStart.get
            drawer.redraw(graph)
            drawer.drawSelectionByRect(start.x, start.y, event.clientX, event.clientY, COLOR_SELECTION_RECT)*/
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
    }
    
    def onMouseUp(event: BrowserEvent) {
        selectionStart = None
        moveStart = None
        drawer.redraw(graph)
    }

    /*


    var select_rect = false
    var select_rectOriginX = 0
    var select_rectOriginY = 0
    var select_VsLastTime = Array[Vertex]()
    var select_touchedSelected = false
    var select_Vs = 0

    var mouse_useToleration = true
    var mouse_moveToleration = 3 //count of pixels that are tolerated as "no mouse move",
    used for deselecting all vertices
    var mouse_buttonDownMove = false
    var mouse_buttonDown = false
    var mouse_x = 0
    var mouse_y = 0

    var keyboard_shiftDown = false

    var canvas_width = 700
    var canvas_height = 700


    var ctx: Context = null
    var document: Document = null

    var graph: Array[Vertex] = Array[Vertex]()

    /////////////////////////////////////////////////////////////////////////////
    //vertex processing
    /////////////////////////////////////////////////////////////////////////////

    //changes coordinates of all selected vertices according to difference between canvas.mouseX/Y and event.clientX/Y
    values
    def moveSelectedVertices(event: Event, graph: Array[Vertex]) {

        //prepare
        val mouseX = event.clientX
        val mouseY = event.clientY
        val diffX = - mouse_x + mouseX
        val diffY = - mouse_y + mouseY
        var resultX = 0
        var resultY = 0

        //change position of all selected vertices
        graph.foreach {
            (vertex: Vertex) =>
                if(vertex.selected) {

                    resultX = diffX + vertex.X
                    vertex.X = if(resultX < 0) { 0 } else { if(resultX > canvas_width) { canvas_width } else {
                    resultX }}

                    resultY = diffY + vertex.Y
                    vertex.Y = if(resultY < 0) { 0 } else { if(resultY > canvas_height) { canvas_height } else {
                    resultY }}
                }
        }

        redraw(graph, ctx)
    }

    def selectionByRect(event: Event, graph: Array[Vertex]) {

        //top values of mouse positoins on screen
        val mouseLeft = if(event.clientX <= select_rectOriginX) { event.clientX } else { select_rectOriginX }
        val mouseRight = if(event.clientX > select_rectOriginX) { event.clientX } else { select_rectOriginX }
        val mouseTop = if(event.clientY <= select_rectOriginY) { event.clientY } else { select_rectOriginY }
        val mouseBottom = if(event.clientY > select_rectOriginY) { event.clientY } else { select_rectOriginY }

        //VsLastTime takes care of not deselectin vertices, that were already selected at the beginning
        //of this action only vertices in VsLastTime can be deselected
        graph.foreach {
            (vertex: Vertex) =>

                if(isPointInRect(vertex.X, vertex.Y,
                    mouseLeft, mouseTop, mouseRight, mouseBottom)) {

                    if(!vertex.selected) { //ingnore if vertex is already selected
                        select_VsLastTime = addToArray(select_VsLastTime, vertex)
                        selectVertex(vertex)
                    }
                } else {

                    val select_VSLastTime_temp = removeFromArray(select_VsLastTime, vertex)
                    if(select_VSLastTime_temp != null) {
                        select_VsLastTime = select_VSLastTime_temp
                        deselectVertex(vertex)
                    }
                }
        }

        redraw(graph, ctx)
        Drawer.drawSelectionByRect(mouseLeft, mouseTop, mouseRight, mouseBottom, COLOR_SELECTION_RECT, ctx)
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////
    //util funcions
    /////////////////////////////////////////////////////////////////////////////////////////////////
    def addToArray(array: Array[Vertex], newElement: Vertex): Array[Vertex] = {
        array.:::(Array[Vertex](newElement))
    }

    def removeFromArray(array: Array[Vertex], oldElement: Vertex): Array[Vertex] = {

        var num = 0
        var arrHead = Array[Vertex]()

        array.foreach {
            (element: Vertex) =>

                if(element.id == oldElement.id) {

                    return array.drop(num) ::: arrHead
                }

                arrHead = arrHead ::: Array[Vertex](element)
                num += 1
        }

        null
    }



    /////////////////////////////////////////////////////////////////////////////////////////////////
    //Arrayener funcions
    /////////////////////////////////////////////////////////////////////////////////////////////////



    //reaction to mouse button pressed action
    def MOUSE_CLICKED(event: Event) {

        mouse_useToleration = true
        mouse_buttonDownMove = false
        mouse_buttonDown = true
        mouse_x = event.clientX
        mouse_y = event.clientY
        select_touchedSelected = false
        select_VsLastTime = Array[Vertex]()


        var needsToRedraw = false

        val touchedVertex = getTouchedVertex(mouse_x, mouse_y, graph)
        if(touchedVertex != null) {

            if(touchedVertex.selected) {
                select_touchedSelected = true
            } else {
                if(!keyboard_shiftDown) {
                    deselectAll(graph)
                }
                selectVertex(touchedVertex)
                needsToRedraw = true
            }
        } else {

            if(!keyboard_shiftDown) {
                needsToRedraw = deselectAll(graph)
            }
            select_rect = true
            select_rectOriginX = mouse_x
            select_rectOriginY = mouse_y
        }

        if(needsToRedraw) {
            redraw(graph, ctx)
        }
    }

    //reaction to mouse button released action
    def MOUSE_RELEASED(event: Event) {

        mouse_x = event.clientX
        mouse_y = event.clientY
        mouse_buttonDown = false
        var needToRedraw = select_rect

        if(select_touchedSelected && !mouse_buttonDownMove) {
            val touchedVertex = getTouchedVertex(mouse_x, mouse_y, graph)
            deselectVertex(touchedVertex)
            needToRedraw = true
        } else if(mouse_useToleration) {
            needToRedraw = true
        }

        if(needToRedraw) {
            redraw(graph, ctx)
        }
        select_rect = false
        mouse_buttonDownMove = false
    }

    //reaction to mouse move action
    def MOUSE_MOVED(event: Event) {

        if(mouse_buttonDown) {
            if(select_rect) {

                //if mouse is further than move toleration allows forbid tolerating
                //slight movement
                if(scala.math.abs(select_rectOriginX - event.clientX) > mouse_moveToleration ||
                    scala.math.abs(select_rectOriginY - event.clientY) > mouse_moveToleration) {
                    mouse_useToleration = false
                }
                selectionByRect(event, graph)
            } else {
                moveSelectedVertices(event, graph)
            }

            mouse_buttonDownMove = true
        }

        mouse_x = event.clientX
        mouse_y = event.clientY
    }

    def mouseWheel(event: Event) {

        /*var evt=window.event || event //equalize event object
        var delta=evt.detail? evt.detail*(-120) : evt.wheelDelta
        //delta returns +120 when wheel is scrolled up, -120 when scrolled down
        global.scale = (delta <= -120) ? global.scale - 0.1 : global.scale + 0.1

        global.scale = global.scale > 15 ? 15 : (global.scale < 8) ? 8 : global.scale
        //drawDefault()*/
    }*/

    /*var mousewheelevt=(/Firefox/i.test(navigator.userAgent))? "DOMMouseScroll" : "mousewheel"
//FF doesn't recognize mousewheel as of FF3.x

if (this.attachEvent) //if IE (and Opera depending on user setting)
  this.attachEvent("on"+mousewheelevt, mouseWheel)
else if (this.addEventArrayener) //WC3 browsers
  this.addEventArrayener(mousewheelevt, mouseWheel, false)*/

    //reaction to a pressed keyboard button
    /*def KEY_DOWN(event: Event) {

        if(event.keyCode == 16) {
            keyboard_shiftDown = true
        }
    }

    //reaction to a released keyboard button
    def KEY_UP(event: Event) {

        if(event.keyCode == 16) {
            keyboard_shiftDown = false
        }
    }*/
}
