package cz.payola.web.client.mvvm.element

import cz.payola.web.client.events._
import s2js.adapters.js.dom.Element
import cz.payola.web.client.views.plugins.visual.{Color, Vector, Point}
import cz.payola.web.client.views.plugins.visual.graph._
import s2js.adapters.js.dom
import s2js.compiler.javascript
import s2js.adapters.js.browser._

class CanvasPack(width: Double, height: Double) extends Canvas(width, height) {


    val mouseClicked = new ClickedEvent[CanvasPack]
    val mouseDragged = new DraggedEvent[CanvasPack]
    val mouseDblClicked = new DoubleClickedEvent[CanvasPack]
    val mouseDown = new MouseDownEvent[CanvasPack]
    val mouseUp = new MouseUpEvent[CanvasPack]
    val keyUp = new KeyUpEvent[CanvasPack]
    val keyDown = new KeyDownEvent[CanvasPack]
    val mouseMove = new MouseMoveEvent[CanvasPack]
    val mouseWheel = new MouseWheelEvent[CanvasPack]
    val windowResize = new WindowResizeEvent[CanvasPack]

    private val edgesDeselectedLayer = new Canvas(width, height)
    private val edgesDeselectedTextLayer = new Canvas(width, height)
    private val edgesSelectedLayer = new Canvas(width, height)
    private val edgesSelectedTextLayer = new Canvas(width, height)
    private val verticesDeselectedLayer = new Canvas(width, height)
    private val verticesDeselectedTextLayer = new Canvas(width, height)
    private val verticesSelectedLayer = new Canvas(width, height)
    private val verticesSelectedTextLayer = new Canvas(width, height)

    canvasElement.onclick = { event =>
        val args = new ClickedEventArgs(this)
        args.set(event)
        mouseClicked.trigger(args)
    }

    canvasElement.ondblclick = { event =>
        val args = new DoubleClickedEventArgs(this)
        args.set(event)
        mouseDblClicked.trigger(args)
    }

    canvasElement.onmousedown = { event =>
        mousePressed = true
        val args = new MouseDownEventArgs(this)
        args.set(event)
        mouseDown.trigger(args)
    }

    canvasElement.onmouseup = { event =>
        mousePressed = false
        val args = new MouseUpEventArgs(this)
        args.set(event)
        mouseUp.trigger(args)
    }

    canvasElement.onmousemove = { event =>
        val args = new MouseMoveEventArgs(this)
        args.set(event)
        mouseMove.trigger(args)
        if(mousePressed) {
            val argsDrag = new DraggedEventArgs(this)
            argsDrag.set(event)
            mouseDragged.trigger(argsDrag)
        } else {
            false
        }
    }

    window.onresize = { event =>
        val args = new WindowResizeEventArgs(this)
        args.set(event)
        windowResize.trigger(args)
        true
    }

    //on mouse wheel event work-around###################################################################################
    /**
      * definition of onMouseWheel trigger; required since Mozilla has different way of setting this up
      * @param event
      * @return
      */
    canvasElement.onmousewheel = onMouseWheel

    private def onMouseWheel(event: s2js.adapters.js.browser.Event): Boolean = {
        val args = new MouseWheelEventArgs(this)
        args.set(event)
        mouseWheel.trigger(args)
        false
    }

    @javascript(
        """
           /* DOMMouseScroll is for mozilla. */
           self.canvasElement.addEventListener('DOMMouseScroll', function(event) {
               var args = new cz.payola.web.client.mvvm.events.MouseWheelEventArgs(self);
               args.set(event);
               self.mouseWheel.trigger(args);
               return false;
           });
        """)
    private def setMouseWheelListener() {}
    //^TODO this calls the onMouseWheel function in window context; that results in error, because window..mouseWheel does not exist

    //###################################################################################################################


    override def setSize(size: Vector) {
        edgesDeselectedLayer.setSize(size)
        edgesDeselectedTextLayer.setSize(size)
        edgesSelectedLayer.setSize(size)
        edgesSelectedTextLayer.setSize(size)
        verticesDeselectedLayer.setSize(size)
        verticesDeselectedTextLayer.setSize(size)
        verticesSelectedLayer.setSize(size)
        verticesSelectedTextLayer.setSize(size)
        super.setSize(size)
    }

    def offsetLeft: Double = {
        canvasElement.offsetLeft
    }

    def offsetTop: Double = {
        canvasElement.offsetTop
    }

    override def render(parent: Element) {

        setMouseWheelListener()

        /*The order in which are layers created determines their "z coordinate"
        (first created layer is on the bottom and last created one covers all the others).*/

        edgesDeselectedLayer.render(parent)
        edgesDeselectedTextLayer.render(parent)
        edgesSelectedLayer.render(parent)
        edgesSelectedTextLayer.render(parent)
        verticesDeselectedLayer.render(parent)
        verticesDeselectedTextLayer.render(parent)
        verticesSelectedLayer.render(parent)
        verticesSelectedTextLayer.render(parent)
        super.render(parent)
    }

    override def clear() {
        edgesDeselectedLayer.clear()
        edgesDeselectedTextLayer.clear()
        edgesSelectedLayer.clear()
        edgesSelectedTextLayer.clear()
        verticesDeselectedLayer.clear()
        verticesDeselectedTextLayer.clear()
        verticesSelectedLayer.clear()
        verticesSelectedTextLayer.clear()
    }

    def clearForMovement() {
        edgesSelectedLayer.clear()
        edgesSelectedTextLayer.clear()
        verticesSelectedLayer.clear()
        verticesSelectedTextLayer.clear()
    }

    override def dirty() {
        edgesDeselectedLayer.dirty()
        edgesDeselectedTextLayer.dirty()
        edgesSelectedLayer.dirty()
        edgesSelectedTextLayer.dirty()
        verticesDeselectedLayer.dirty()
        verticesDeselectedTextLayer.dirty()
        verticesSelectedLayer.dirty()
        verticesSelectedTextLayer.dirty()
    }

    def draw(view: View, color: Option[Color], positionCorrection: Vector) {
        view match {
            case i: VertexView =>
                if(view.isSelected) {
                    if(verticesSelectedLayer.isCleared) {
                        i.draw(verticesSelectedLayer.context, color, positionCorrection)
                    }
                    if(verticesSelectedTextLayer.isCleared) {
                        i.drawInformation(verticesSelectedTextLayer.context, None,
                            (LocationDescriptor.getVertexInformationPosition(i.position) + positionCorrection).toVector)
                    }
                } else {
                    if(verticesDeselectedLayer.isCleared) {
                        i.draw(verticesDeselectedLayer.context, color, positionCorrection)
                    }
                }
            case i: EdgeView =>
                if(i.isSelected) {
                    if(edgesSelectedLayer.isCleared) {
                        i.draw(edgesSelectedLayer.context, color, positionCorrection)
                    }
                    if(i.areBothVerticesSelected && edgesSelectedTextLayer.isCleared) {
                        i.information.draw(edgesSelectedTextLayer.context, None,
                            (LocationDescriptor.getEdgeInformationPosition(i.originView.position,
                                i.destinationView.position) + positionCorrection).toVector)
                    }
                } else {
                    if(edgesDeselectedLayer.isCleared) {
                        i.draw(edgesDeselectedLayer.context, color, positionCorrection)
                    }
                }
        }
    }

    def drawQuick(view: View, color: Option[Color], positionCorrection: Vector) {
        view match {
            case i: VertexView =>
                if(view.isSelected) {
                    if(verticesSelectedLayer.isCleared) {
                        i.drawQuick(verticesSelectedLayer.context, color, positionCorrection)
                    }
                } else {
                    if(verticesDeselectedLayer.isCleared) {
                        i.drawQuick(verticesDeselectedLayer.context, color, positionCorrection)
                    }
                }
            case i: EdgeView =>
                if(i.areBothVerticesSelected) {
                    if(edgesSelectedLayer.isCleared) {
                        i.drawQuick(edgesSelectedLayer.context, color, positionCorrection)
                    }
                } else {
                    if(edgesDeselectedLayer.isCleared) {
                        i.drawQuick(edgesDeselectedLayer.context, color, positionCorrection)
                    }
                }
        }
    }
}
