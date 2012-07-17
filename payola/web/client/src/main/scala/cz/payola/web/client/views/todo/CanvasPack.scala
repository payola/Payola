package cz.payola.web.client.views.todo

import s2js.adapters.js.dom
import s2js.adapters.js.browser
import cz.payola.web.client.views.graph.visual.Color
import s2js.compiler.javascript
import s2js.adapters.js.browser._
import cz.payola.web.client.views.graph.visual.graph.positioning.LocationDescriptor
import cz.payola.web.client.views.elements.Canvas
import cz.payola.web._
import cz.payola.web.client.views.graph.visual.graph._
import cz.payola.web.client.views.algebra.Vector2D
import cz.payola.web.client.events.BrowserEvent

class CanvasPack(initialSize: Vector2D) extends client.View
{
    val mouseDoubleClicked = new BrowserEvent[Canvas]

    val mousePressed = new BrowserEvent[Canvas]

    val mouseReleased = new BrowserEvent[Canvas]

    val mouseDragged = new BrowserEvent[Canvas]

    val mouseWheelRotated = new BrowserEvent[Canvas]

    val windowResized = new BrowserEvent[CanvasPack]

    protected var mouseIsPressed = false

    private val topLayer = new Canvas(initialSize)

    private val edgesDeselectedLayer = new Canvas(initialSize)

    private val edgesDeselectedTextLayer = new Canvas(initialSize)

    private val edgesSelectedLayer = new Canvas(initialSize)

    private val edgesSelectedTextLayer = new Canvas(initialSize)

    private val verticesDeselectedLayer = new Canvas(initialSize)

    private val verticesDeselectedTextLayer = new Canvas(initialSize)

    private val verticesSelectedLayer = new Canvas(initialSize)

    private val verticesSelectedTextLayer = new Canvas(initialSize)

    topLayer.mouseDoubleClicked += { e =>
        mouseDoubleClicked.trigger(e)
        false
    }

    topLayer.mousePressed += { e =>
        mouseIsPressed = true
        mousePressed.trigger(e)
        false
    }

    topLayer.mouseReleased += { e =>
        mouseIsPressed = false
        mouseReleased.trigger(e)
        false
    }

    topLayer.mouseMoved += { e =>
        if (mouseIsPressed) {
            mouseDragged.trigger(e)
        }
        false
    }

    topLayer.mouseWheelRotated += { e =>
        mouseWheelRotated.trigger(e)
        false
    }

    window.onresize = { e => windowResized.triggerDirectly(this, e)}

    //on mouse wheel event work-around###################################################################################

    private def onMouseWheel(e: browser.Event): Boolean = {
        topLayer.mouseWheelRotated.triggerDirectly(topLayer, e)
    }

    @javascript(
        """
           /* DOMMouseScroll is for mozilla. */
           self.topLayer.domElement.addEventListener('DOMMouseScroll', function(event) {
               return self.mouseWheelRotated.triggerDirectly(self.topLayer, event);
           });
        """)
    private def setMouseWheelListener() {}

    //^TODO this calls the onMouseWheel function in window context; that results in error,
    // because window..mouseWheel does not exist

    //###################################################################################################################

    def size: Vector2D = topLayer.size

    def size_=(size: Vector2D) {
        topLayer.size = size
        edgesDeselectedLayer.size = size
        edgesDeselectedTextLayer.size = size
        edgesSelectedLayer.size = size
        edgesSelectedTextLayer.size = size
        verticesDeselectedLayer.size = size
        verticesDeselectedTextLayer.size = size
        verticesSelectedLayer.size = size
        verticesSelectedTextLayer.size = size
    }

    def offsetLeft: Double = {
        topLayer.domElement.offsetLeft
    }

    def offsetTop: Double = {
        topLayer.domElement.offsetTop
    }

    def render(parent: dom.Element) {
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
        topLayer.render(parent)
    }

    def clear() {
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

    def dirty() {
        edgesDeselectedLayer.dirty()
        edgesDeselectedTextLayer.dirty()
        edgesSelectedLayer.dirty()
        edgesSelectedTextLayer.dirty()
        verticesDeselectedLayer.dirty()
        verticesDeselectedTextLayer.dirty()
        verticesSelectedLayer.dirty()
        verticesSelectedTextLayer.dirty()
    }

    def draw(view: View, color: Option[Color], positionCorrection: Vector2D) {
        view match {
            case i: VertexView =>
                if (view.isSelected) {
                    if (verticesSelectedLayer.isClear) {
                        i.draw(verticesSelectedLayer.context, color, positionCorrection)
                    }
                    if (verticesSelectedTextLayer.isClear) {
                        i.drawInformation(verticesSelectedTextLayer.context, None,
                            (LocationDescriptor.getVertexInformationPosition(i.position) + positionCorrection).toVector)
                    }
                } else {
                    if (verticesDeselectedLayer.isClear) {
                        i.draw(verticesDeselectedLayer.context, color, positionCorrection)
                    }
                }
            case i: EdgeView =>
                if (i.isSelected) {
                    if (edgesSelectedLayer.isClear) {
                        i.draw(edgesSelectedLayer.context, color, positionCorrection)
                    }
                    if (i.areBothVerticesSelected && edgesSelectedTextLayer.isClear) {
                        i.information.draw(edgesSelectedTextLayer.context, None,
                            (LocationDescriptor.getEdgeInformationPosition(i.originView.position,
                                i.destinationView.position) + positionCorrection).toVector)
                    }
                } else {
                    if (edgesDeselectedLayer.isClear) {
                        i.draw(edgesDeselectedLayer.context, color, positionCorrection)
                    }
                }
        }
    }

    def drawQuick(view: View, color: Option[Color], positionCorrection: Vector2D) {
        view match {
            case i: VertexView =>
                if (view.isSelected) {
                    if (verticesSelectedLayer.isClear) {
                        i.drawQuick(verticesSelectedLayer.context, color, positionCorrection)
                    }
                } else {
                    if (verticesDeselectedLayer.isClear) {
                        i.drawQuick(verticesDeselectedLayer.context, color, positionCorrection)
                    }
                }
            case i: EdgeView =>
                if (i.areBothVerticesSelected) {
                    if (edgesSelectedLayer.isClear) {
                        i.drawQuick(edgesSelectedLayer.context, color, positionCorrection)
                    }
                } else {
                    if (edgesDeselectedLayer.isClear) {
                        i.drawQuick(edgesDeselectedLayer.context, color, positionCorrection)
                    }
                }
        }
    }

    def destroy() {
        // TODO
    }

    def block() {
        // TODO
    }

    def unblock() {
        // TODO
    }
}
