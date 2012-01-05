package cz.payola.web.client

import graph.{Vertex, Graph, Drawer}
import s2js.adapters.js.browser._
import s2js.adapters.js.dom._
import s2js.adapters.goog.events._
import s2js.adapters.goog.events.{EventType, BrowserEvent, EventHandler}

object Index {
    // TODO will be model
    var graph: Graph = new Graph(null);

    // TODO will be view.
    var layerVertices: Layer = null;
    var layerEdges: Layer = null;
    var layerText: Layer = null;
    var drawer: Drawer = null;

    var selectionStart: Option[Point] = None
    var moveStart: Option[Point] = None

    def init() {
        // Initialize the canvas and drawer

        //TODO position the canvases
        //edges are the lowest layer
        val canvasEdges = document.createElement[Canvas]("canvas")
        val contextEdges = canvasEdges.getContext[CanvasRenderingContext2D]("2d")
        layerEdges = new Layer(canvasEdges, contextEdges)
        layerEdges.setWidth(window.innerWidth)
        layerEdges.setHeight(window.innerHeight)
        document.getElementById("canvas-holder").appendChild(canvasEdges)

        //vertices are over edges
        val canvasVertices = document.createElement[Canvas]("canvas")
        val contextVertices = canvasVertices.getContext[CanvasRenderingContext2D]("2d")
        layerVertices = new Layer(canvasVertices, contextVertices)
        layerVertices.setWidth(window.innerWidth)
        layerVertices.setHeight(window.innerHeight)
        document.getElementById("canvas-holder").appendChild(canvasVertices)

        //text is on top
        val canvasText = document.createElement[Canvas]("canvas")
        val contextText = canvasText.getContext[CanvasRenderingContext2D]("2d")
        layerText = new Layer(canvasText, contextText)
        layerText.setWidth(window.innerWidth)
        layerText.setHeight(window.innerHeight)
        document.getElementById("canvas-holder").appendChild(canvasText)

        drawer = new Drawer(layerEdges, layerVertices, layerText);

        // Attach events to the canvas. //TODO may be required to attach to all canvases
        listen[BrowserEvent](canvasText, EventType.MOUSEDOWN, onMouseDown _)
        listen[BrowserEvent](canvasText, EventType.MOUSEMOVE, onMouseMove _)
        listen[BrowserEvent](canvasText, EventType.MOUSEUP, onMouseUp _)
        
        drawer.redraw(graph)
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
            val difference = end.subtract(moveStart.get)
            
            graph.getGraph.foreach { vertex =>
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
