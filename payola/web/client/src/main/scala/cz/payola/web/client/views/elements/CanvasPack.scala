package cz.payola.web.client.views.elements

import cz.payola.web.client.events._
import s2js.adapters.js.dom.Element
import cz.payola.web.client.views.plugins.visual.{Color, Vector, Point}
import cz.payola.web.client.views.plugins.visual.graph._
import s2js.adapters.js.dom
import s2js.compiler.javascript
import s2js.adapters.js.browser._
import cz.payola.web.client.presenters.components.ZoomControls
import cz.payola.web.client.views.plugins.visual.graph.positioning.LocationDescriptor
import cz.payola.web.client.views.events._

class CanvasPack(width: Double, height: Double) extends Canvas(width, height)
{
    val mouseClicked = new BrowserEvent[CanvasPack]

    val mouseDragged = new BrowserEvent[CanvasPack]

    val mouseDblClicked = new BrowserEvent[CanvasPack]

    val mouseDown = new BrowserEvent[CanvasPack]

    val mouseUp = new BrowserEvent[CanvasPack]

    val keyUp = new BrowserEvent[CanvasPack]

    val keyDown = new BrowserEvent[CanvasPack]

    val mouseMove = new BrowserEvent[CanvasPack]

    val mouseWheel = new BrowserEvent[CanvasPack]

    val windowResize = new BrowserEvent[CanvasPack]

    private val edgesDeselectedLayer = new Canvas(width, height)

    private val edgesDeselectedTextLayer = new Canvas(width, height)

    private val edgesSelectedLayer = new Canvas(width, height)

    private val edgesSelectedTextLayer = new Canvas(width, height)

    private val verticesDeselectedLayer = new Canvas(width, height)

    private val verticesDeselectedTextLayer = new Canvas(width, height)

    private val verticesSelectedLayer = new Canvas(width, height)

    private val verticesSelectedTextLayer = new Canvas(width, height)

    canvasElement.onclick = { e => mouseClicked.trigger(this, e) }

    canvasElement.ondblclick = { e => mouseDblClicked.trigger(this, e) }

    canvasElement.onmousedown = { e =>
        mousePressed = true
        mouseDown.trigger(this, e)
    }

    canvasElement.onmouseup = { e =>
        mousePressed = false
        mouseUp.trigger(this, e)
    }

    canvasElement.onmousemove = { e =>
        val returnValue = mouseMove.trigger(this, e)
        if (mousePressed) {
            mouseDragged.trigger(this, e)
        } else {
            returnValue
        }
    }

    window.onresize = { e => windowResize.trigger(this, e) }

    //on mouse wheel event work-around###################################################################################
    /**
      * definition of onMouseWheel trigger; required since Mozilla has different way of setting this up
      * @param event
      * @return
      */
    canvasElement.onmousewheel = onMouseWheel

    private def onMouseWheel(e: s2js.adapters.js.browser.Event): Boolean = {
        mouseWheel.trigger(this, e)
    }

    @javascript(
        """
           /* DOMMouseScroll is for mozilla. */
           self.canvasElement.addEventListener('DOMMouseScroll', function(event) {
               var args = new cz.payola.web.client.views.events.MouseWheelEventArgs(self);
               args.set(event);
               self.mouseWheel.trigger(args);
               return false;
           });
        """)
    private def setMouseWheelListener() {}

    //^TODO this calls the onMouseWheel function in window context; that results in error,
    // because window..mouseWheel does not exist

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
                if (view.isSelected) {
                    if (verticesSelectedLayer.isCleared) {
                        i.draw(verticesSelectedLayer.context, color, positionCorrection)
                    }
                    if (verticesSelectedTextLayer.isCleared) {
                        i.drawInformation(verticesSelectedTextLayer.context, None,
                            (LocationDescriptor.getVertexInformationPosition(i.position) + positionCorrection).toVector)
                    }
                } else {
                    if (verticesDeselectedLayer.isCleared) {
                        i.draw(verticesDeselectedLayer.context, color, positionCorrection)
                    }
                }
            case i: EdgeView =>
                if (i.isSelected) {
                    if (edgesSelectedLayer.isCleared) {
                        i.draw(edgesSelectedLayer.context, color, positionCorrection)
                    }
                    if (i.areBothVerticesSelected && edgesSelectedTextLayer.isCleared) {
                        i.information.draw(edgesSelectedTextLayer.context, None,
                            (LocationDescriptor.getEdgeInformationPosition(i.originView.position,
                                i.destinationView.position) + positionCorrection).toVector)
                    }
                } else {
                    if (edgesDeselectedLayer.isCleared) {
                        i.draw(edgesDeselectedLayer.context, color, positionCorrection)
                    }
                }
        }
    }

    def drawQuick(view: View, color: Option[Color], positionCorrection: Vector) {
        view match {
            case i: VertexView =>
                if (view.isSelected) {
                    if (verticesSelectedLayer.isCleared) {
                        i.drawQuick(verticesSelectedLayer.context, color, positionCorrection)
                    }
                } else {
                    if (verticesDeselectedLayer.isCleared) {
                        i.drawQuick(verticesDeselectedLayer.context, color, positionCorrection)
                    }
                }
            case i: EdgeView =>
                if (i.areBothVerticesSelected) {
                    if (edgesSelectedLayer.isCleared) {
                        i.drawQuick(edgesSelectedLayer.context, color, positionCorrection)
                    }
                } else {
                    if (edgesDeselectedLayer.isCleared) {
                        i.drawQuick(edgesDeselectedLayer.context, color, positionCorrection)
                    }
                }
        }
    }

    override def getDomElement: Element = {
        //TODO
        edgesDeselectedLayer.getDomElement
    }
}
