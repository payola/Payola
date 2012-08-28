package cz.payola.web.client.views

import scala.collection._
import s2js.adapters.browser._
import s2js.adapters.html
import cz.payola.web.client.View
import cz.payola.web.client.views.elements.Text
import cz.payola.web.client.views.algebra.Vector2D
import cz.payola.web.client.events._
import s2js.compiler.javascript
import s2js.adapters.events

abstract class ElementView[A <: html.Element](htmlElementName: String, val subViews: Seq[View], cssClass: String)
    extends View
{
    /**
     * Value used to indicate mouse dragging.
     */
    private var mouseIsPressed = false

    /**
     * HTML element representing this element in the final web page
     */
    val htmlElement = document.createElement[A](htmlElementName)

    /**
     * Key pressed events triggered when onkeydown event of the htmlElement is fired.
     */
    val keyPressed = new BooleanEvent[this.type, KeyboardEventArgs[this.type]]

    /**
     * Key released events triggered when onkeyup event of the htmlElement is fired.
     */
    val keyReleased = new BooleanEvent[this.type, KeyboardEventArgs[this.type]]

    /**
     * Mouse clicked events triggered when onclick event of the htmlElement is fired.
     */
    val mouseClicked = new BooleanEvent[this.type, MouseEventArgs[this.type]]

    /**
     * Mouse double clicked events triggered when ondblclick event of the htmlElement is fired.
     */
    val mouseDoubleClicked = new BooleanEvent[this.type, MouseEventArgs[this.type]]

    /**
     * Mouse pressed events triggered when onmousedown event of the htmlElement is fired.
     */
    val mousePressed = new BooleanEvent[this.type, MouseEventArgs[this.type]]

    /**
     * Mouse released events triggered when onmouseup event of the htmlElement is fired.
     */
    val mouseReleased = new BooleanEvent[this.type, MouseEventArgs[this.type]]

    /**
     * Mouse moved events triggered when onmousemove event of the htmlElement is fired.
     */
    val mouseMoved = new BooleanEvent[this.type, MouseEventArgs[this.type]]

    /**
     * Mouse out events triggered when onmouseout event of the htmlElement is fired.
     */
    val mouseOut = new BooleanEvent[this.type, MouseEventArgs[this.type]]

    /**
     * Mouse wheel rotated events triggered when onmousewheel event of the htmlElement is fired.
     */
    val mouseWheelRotated = new BooleanEvent[this.type, MouseWheelEventArgs[this.type]]

    /**
     * Mouse dragging events triggered when mouse button is pressed and event mouseMoved is triggered.
     */
    val mouseDragged = new BooleanEvent[this.type, MouseEventArgs[this.type]]

    /**
     * Superior html element of this element corresponding to the resulted structured HTML of the web page
     */
    protected var parentElement: Option[html.Element] = None

    htmlElement.onkeydown = { e => keyPressed.trigger(KeyboardEventArgs[this.type](this, e))}
    htmlElement.onkeyup = { e => keyReleased.trigger(KeyboardEventArgs[this.type](this, e))}
    htmlElement.onclick = { e => mouseClicked.trigger(MouseEventArgs[this.type](this, e))}
    htmlElement.ondblclick = { e => mouseDoubleClicked.trigger(MouseEventArgs[this.type](this, e))}
    htmlElement.onmousedown = { e => mousePressed.trigger(MouseEventArgs[this.type](this, e))}
    htmlElement.onmouseup = { e => mouseReleased.trigger(MouseEventArgs[this.type](this, e))}
    htmlElement.onmousemove = { e => mouseMoved.trigger(MouseEventArgs[this.type](this, e))}
    htmlElement.onmouseout = { e => mouseOut.trigger(MouseEventArgs[this.type](this, e))}
    htmlElement.onmousewheel = triggerMouseWheelRotated _
    bindDOMMouseWheel()
    addCssClass(cssClass)

    //mouse dragged simulation
    mousePressed += { e => mouseIsPressed = true; true }
    mouseReleased += { e => mouseIsPressed = false; true }
    mouseMoved += { e => if (mouseIsPressed) { mouseDragged.trigger(e) }; true }

    def blockHtmlElement = htmlElement

    def render(parent: html.Element) {
        parentElement = Some(parent)
        parent.appendChild(htmlElement)
        subViews.foreach { v =>
            new Text(" ").render(htmlElement)
            v.render(htmlElement)
        }
    }

    def destroy() {
        parentElement.foreach(_.removeChild(htmlElement))
    }

    /**
     * Getter of attributes from the html element by name
     * @param name of the attribute
     * @return value of the attribute
     */
    def getAttribute(name: String): String = {
        htmlElement.getAttribute(name)
    }

    /**
     * Setter of attributes to the html element by name
     * @param name of the attribute
     * @param value to set to the attribute
     * @return
     */
    def setAttribute(name: String, value: String): this.type = {
        htmlElement.setAttribute(name, value)
        this
    }

    /**
     * Appends a CSS class to this element.
     * @param cssClass name of the class to append
     * @return
     */
    def addCssClass(cssClass: String): this.type = {
        removeCssClass(cssClass)
        setAttribute("class", getAttribute("class") + " " + cssClass)
        this
    }

    /**
     * Removes a CSS class from this element.
     * @param cssClass name of the class to remove
     * @return
     */
    def removeCssClass(cssClass: String): this.type = {
        setAttribute("class", getAttribute("class").replaceAllLiterally(cssClass, ""))
        this
    }

    /**
     * Hides this element (equivalent of show("none") call).
     */
    def hide() {
        setAttribute("style", "display: none")
    }

    /**
     * Sets value display of attribute style to the element.
     * @param displayStyle display mode (default is "block")
     */
    def show(displayStyle: String = "block") {
        setAttribute("style", "display: " + displayStyle)
    }

    /**
     * Getter of attribute id.
     * @return id of the element
     */
    def id: String = htmlElement.id

    /**
     * Setter of the attribute id.
     * @param value id to set
     */
    def id_=(value: String) {
        htmlElement.id = value
    }

    /**
     * Removes all sub-elements of this element.
     */
    def removeAllChildNodes() {
        while (htmlElement.hasChildNodes) {
            htmlElement.removeChild(htmlElement.firstChild)
        }
    }

    /**
     * Getter of this element's offset according to the topmost element (body).
     * @return
     */
    def offset: Vector2D = {
        val rectangle = htmlElement.getBoundingClientRect
        Vector2D(rectangle.left + document.body.scrollLeft, rectangle.top + document.body.scrollTop)
    }

    private def triggerMouseWheelRotated(e: events.WheelEvent[html.Element]): Boolean = {
        mouseWheelRotated.trigger(MouseWheelEventArgs[this.type](this, e))
    }

    @javascript(
        """
            self.htmlElement.addEventListener('DOMMouseScroll', function(e) {
                self.triggerMouseWheelRotated(e);
            });
        """)
    private def bindDOMMouseWheel() {}
}
