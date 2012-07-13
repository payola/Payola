package cz.payola.web.client.views.todo

import s2js.adapters.js.dom.Element
import s2js.adapters.js.browser
import cz.payola.web.client.views._
import cz.payola.web.client.views.plugins.visual.Color
import cz.payola.web.client.views.plugins.visual.graph._
import s2js.compiler.javascript
import s2js.adapters.js.browser._
import cz.payola.web.client.views.plugins.visual.graph.positioning.LocationDescriptor
import cz.payola.web.client.views.events._
import cz.payola.web.client.views.elements.Canvas

class CanvasPack(size: Vector2D) extends Canvas(size)
{
    val mouseDragged = new BrowserEvent[CanvasPack]

    val windowResized = new BrowserEvent[CanvasPack]

    protected var mouseIsPressed = false

    private val edgesDeselectedLayer = new Canvas(size)

    private val edgesDeselectedTextLayer = new Canvas(size)

    private val edgesSelectedLayer = new Canvas(size)

    private val edgesSelectedTextLayer = new Canvas(size)

    private val verticesDeselectedLayer = new Canvas(size)

    private val verticesDeselectedTextLayer = new Canvas(size)

    private val verticesSelectedLayer = new Canvas(size)

    private val verticesSelectedTextLayer = new Canvas(size)

    mousePressed += { e =>
        mouseIsPressed = true
        false
    }

    mouseReleased += { e =>
        mouseIsPressed = false
        false
    }

    mouseMoved += { e =>
        if (mouseIsPressed) {
            mouseDragged.trigger(e)
        }
        false
    }

    window.onresize = { e => windowResized.triggerDirectly(this, e)}

    //on mouse wheel event work-around###################################################################################

    private def onMouseWheel(e: browser.Event): Boolean = {
        mouseWheelRotated.triggerDirectly(this, e)
    }

    @javascript(
        """
           /* DOMMouseScroll is for mozilla. */
           self.canvasElement.addEventListener('DOMMouseScroll', function(event) {
               return self.mouseWheelRotated.triggerDirectly(self, event);
           });
        """)
    private def setMouseWheelListener() {}

    //^TODO this calls the onMouseWheel function in window context; that results in error,
    // because window..mouseWheel does not exist

    //###################################################################################################################

    override def setSize(size: Vector2D) {
        edgesDeselectedLayer.size = size
        edgesDeselectedTextLayer.size = size
        edgesSelectedLayer.size = size
        edgesSelectedTextLayer.size = size
        verticesDeselectedLayer.size = size
        verticesDeselectedTextLayer.size = size
        verticesSelectedLayer.size = size
        verticesSelectedTextLayer.size = size
        super.size = size
    }

    def offsetLeft: Double = {
        domElement.offsetLeft
    }

    def offsetTop: Double = {
        domElement.offsetTop
    }

    def render(parent: Element) {
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
}
