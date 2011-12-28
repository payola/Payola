package cz.payola.web.client

import graph.{Constants, Drawer, Vertex}
import s2js.adapters.js.browser._
import s2js.adapters.js.dom._
import s2js.adapters.goog.events._
import s2js.adapters.goog.events.{EventType, BrowserEvent}
import Constants._

object Index {
    // TODO will be model. Maybe create a class for graph that will encapsulate the list of vertices and perform the
    // selection/deselection of vertices and counting of selected vertices.
    var graph: List[Vertex] = null

    // TODO will be view.
    var context: CanvasRenderingContext2D = null;
    var drawer: Drawer = null;

    var selectedVertexCount = 0
    var selectionStart: Option[Point] = None
    var moveStart: Option[Point] = None

    def init() {
        // Initialize the canvas and drawer
        val canvas = document.createElement[Canvas]("canvas")
        context = canvas.getContext[CanvasRenderingContext2D]("2d")
        canvas.width = window.innerWidth
        canvas.height = window.innerHeight
        document.getElementById("canvas-holder").appendChild(canvas)
        drawer = new Drawer(context)

        // Attach events to the canvas.
        listen[BrowserEvent](canvas, EventType.MOUSEDOWN, onMouseDown _)
        listen[BrowserEvent](canvas, EventType.MOUSEMOVE, onMouseMove _)
        listen[BrowserEvent](canvas, EventType.MOUSEUP, onMouseUp _)

        // Initialize the graph.
        graph = getGraph

        drawer.redraw(graph)
    }

    private def getGraph: List[Vertex] = {
        val v0 = new Vertex(0, Point(15, 15), "0", null)
        val v1 = new Vertex(1, Point(120, 40), "1", null)
        val v2 = new Vertex(2, Point(50, 120), "2", null)
        val v3 = new Vertex(3, Point(180, 60), "3", null)
        val v4 = new Vertex(4, Point(240, 110), "4", null)
        val v5 = new Vertex(5, Point(160, 160), "5", null)
        val v6 = new Vertex(6, Point(240, 240), "6", null)
        val v7 = new Vertex(7, Point(270, 320), "7", null)
        val v8 = new Vertex(8, Point(160, 240), "8", null)
        val v9 = new Vertex(9, Point(120, 400), "9", null)
        val v10 = new Vertex(10, Point(300, 80), "10", null)
        val v11 = new Vertex(11, Point(320, 30), "11", null)
        val v12 = new Vertex(12, Point(300, 200), "12", null)
        val v13 = new Vertex(13, Point(350, 210), "13", null)
        val v14 = new Vertex(14, Point(300, 400), "14", null)
        val v15 = new Vertex(15, Point(80, 310), "15", null)
        val v16 = new Vertex(16, Point(15, 240), "16", null)
        val v17 = new Vertex(17, Point(15, 300), "17", null)
        val v18 = new Vertex(18, Point(400, 15), "18", null)
        val v19 = new Vertex(19, Point(400, 120), "19", null)

        v0.neighbours = List(v1, v2, v9, v11, v16)
        v1.neighbours = List(v0, v5, v6)
        v2.neighbours = List(v0, v3, v5, v6, v8)
        v3.neighbours = List(v2, v4, v5, v11)
        v4.neighbours = List(v3, v7, v8, v11)
        v5.neighbours = List(v1, v2, v3, v6, v12)
        v6.neighbours = List(v1, v2, v5, v7, v9)
        v7.neighbours = List(v4, v6, v9)
        v8.neighbours = List(v2, v4, v9, v16)
        v9.neighbours = List(v0, v6, v7, v8, v10, v13, v15)
        v10.neighbours = List(v9, v11, v12)
        v11.neighbours = List(v0, v3, v4, v10, v13, v18, v19)
        v12.neighbours = List(v5, v10, v13)
        v13.neighbours = List(v9, v11, v12, v14, v19)
        v14.neighbours = List(v13)
        v15.neighbours = List(v9, v16, v17)
        v16.neighbours = List(v0, v8, v15, v17)
        v17.neighbours = List(v15, v16)
        v18.neighbours = List(v11)
        v19.neighbours = List(v11, v13)

        List(v0, v1, v2, v3, v4, v5, v6, v7, v8, v9, v10, v11, v12, v13, v14, v15, v16, v17, v18, v19)
    }

    def onMouseDown(e: BrowserEvent) {
        val position = Point(e.clientX, e.clientY)
        val vertex = getTouchedVertex(position)
        var needsToRedraw = false;

        // Mouse down near a vertex.
        if (vertex.isDefined) {
            if (e.shiftKey) {
                needsToRedraw = invertVertexSelection(vertex.get) || needsToRedraw
            } else {
                if (!vertex.get.selected) {
                    needsToRedraw = deselectAll(graph)
                }
                moveStart = Some(position)
                needsToRedraw = selectVertex(vertex.get) || needsToRedraw
            }

        // Mouse down somewhere in the inter-vertex space.
        } else {
            if (!e.shiftKey) {
                needsToRedraw = deselectAll(graph)
            }
            selectionStart = Some(position)
        }

        if (needsToRedraw) {
            drawer.redraw(graph)
        }
    }

    def onMouseMove(e: BrowserEvent) {
        if (selectionStart.isDefined) {
            /*
            if(scala.math.abs(select_rectOriginX - e.clientX) > mouse_moveToleration ||
                scala.math.abs(select_rectOriginY - e.clientY) > mouse_moveToleration) {
                mouse_useToleration = false
            }
            selectionByRect(e, graph)
            */

            /*
            val start = selectionStart.get
            drawer.redraw(graph)
            drawer.drawSelectionByRect(start.x, start.y, e.clientX, e.clientY, COLOR_SELECTION_RECT)*/
        } else if (moveStart.isDefined) {
            val end = Point(e.clientX, e.clientY)
            val difference = end.subtract(moveStart.get)
            
            graph.foreach { vertex =>
                if(vertex.selected) {
                    vertex.position = vertex.position.add(difference)

                    /*
                    resultX = diffX + vertex.X
                    vertex.X = if(resultX < 0) { 0 } else { if(resultX > canvas_width) { canvas_width } else {
                        resultX }}

                    resultY = diffY + vertex.Y
                    vertex.Y = if(resultY < 0) { 0 } else { if(resultY > canvas_height) { canvas_height } else {
                        resultY }}*/
                }
            }
            
            moveStart = Some(end)
            drawer.redraw(graph)
        }
    }
    
    def onMouseUp(e: BrowserEvent) {
        selectionStart = None
        moveStart = None
        drawer.redraw(graph)
    }

    def getTouchedVertex(p: Point): Option[Vertex] = {
        val bottomRight = Vector(VERTEX_WIDTH / 2, VERTEX_HEIGHT / 2)
        val topLeft = bottomRight.multiply(-1)
        graph.find {vertex =>
            isPointInRect(p, vertex.position.add(topLeft), vertex.position.add(bottomRight))
        }
    }

    def isPointInRect(p: Point, topLeft: Point, bottomRight: Point): Boolean = {
        p.x >= topLeft.x && p.x <= bottomRight.x && p.y >= topLeft.y && p.y <= bottomRight.y
    }
    
    def setVertexSelection(vertex: Vertex, selected: Boolean): Boolean = {
        if (vertex.selected != selected) {
            selectedVertexCount += (if (selected) 1 else -1)
            vertex.selected = selected
            true
        } else {
            false
        }
    }

    def selectVertex(vertex: Vertex): Boolean = {
        setVertexSelection(vertex, true)
    }
    
    def deselectVertex(vertex: Vertex): Boolean = {
        setVertexSelection(vertex, false)
    }

    def invertVertexSelection(vertex: Vertex): Boolean = {
        setVertexSelection(vertex, !vertex.selected)
    }

    def deselectAll(graph: List[Vertex]): Boolean = {
        var somethingChanged = false
        if (selectedVertexCount > 0) {
            graph.foreach(vertex => somethingChanged = deselectVertex(vertex) || somethingChanged)
        }
        somethingChanged
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

    var canvas_width = 700 //TODO is variable
    var canvas_height = 700 //TODO is variable


    var ctx: Context = null
    var document: Document = null

    var graph: Array[Vertex] = Array[Vertex]()

    /////////////////////////////////////////////////////////////////////////////
    //vertex processing
    /////////////////////////////////////////////////////////////////////////////

    //changes coordinates of all selected vertices according to difference between canvas.mouseX/Y and e.clientX/Y
    values
    def moveSelectedVertices(e: Event, graph: Array[Vertex]) {

        //prepare
        val mouseX = e.clientX
        val mouseY = e.clientY
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

    def selectionByRect(e: Event, graph: Array[Vertex]) {

        //top values of mouse positoins on screen
        val mouseLeft = if(e.clientX <= select_rectOriginX) { e.clientX } else { select_rectOriginX }
        val mouseRight = if(e.clientX > select_rectOriginX) { e.clientX } else { select_rectOriginX }
        val mouseTop = if(e.clientY <= select_rectOriginY) { e.clientY } else { select_rectOriginY }
        val mouseBottom = if(e.clientY > select_rectOriginY) { e.clientY } else { select_rectOriginY }

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
    def MOUSE_CLICKED(e: Event) {

        mouse_useToleration = true
        mouse_buttonDownMove = false
        mouse_buttonDown = true
        mouse_x = e.clientX
        mouse_y = e.clientY
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
    def MOUSE_RELEASED(e: Event) {

        mouse_x = e.clientX
        mouse_y = e.clientY
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
    def MOUSE_MOVED(e: Event) {

        if(mouse_buttonDown) {
            if(select_rect) {

                //if mouse is further than move toleration allows forbid tolerating
                //slight movement
                if(scala.math.abs(select_rectOriginX - e.clientX) > mouse_moveToleration ||
                    scala.math.abs(select_rectOriginY - e.clientY) > mouse_moveToleration) {
                    mouse_useToleration = false
                }
                selectionByRect(e, graph)
            } else {
                moveSelectedVertices(e, graph)
            }

            mouse_buttonDownMove = true
        }

        mouse_x = e.clientX
        mouse_y = e.clientY
    }

    def mouseWheel(e: Event) {

        /*var evt=window.event || e //equalize event object
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
    /*def KEY_DOWN(e: Event) {

        if(e.keyCode == 16) {
            keyboard_shiftDown = true
        }
    }

    //reaction to a released keyboard button
    def KEY_UP(e: Event) {

        if(e.keyCode == 16) {
            keyboard_shiftDown = false
        }
    }*/
}
