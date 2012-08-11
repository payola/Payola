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
    val htmlElement = document.createElement[A](htmlElementName)

    val keyPressed = new BooleanEvent[this.type, KeyboardEventArgs[this.type]]

    val keyReleased = new BooleanEvent[this.type, KeyboardEventArgs[this.type]]

    val mouseClicked = new BooleanEvent[this.type, MouseEventArgs[this.type]]

    val mouseDoubleClicked = new BooleanEvent[this.type, MouseEventArgs[this.type]]

    val mousePressed = new BooleanEvent[this.type, MouseEventArgs[this.type]]

    val mouseReleased = new BooleanEvent[this.type, MouseEventArgs[this.type]]

    val mouseMoved = new BooleanEvent[this.type, MouseEventArgs[this.type]]

    val mouseOut = new BooleanEvent[this.type, MouseEventArgs[this.type]]

    val mouseWheelRotated = new BooleanEvent[this.type, MouseWheelEventArgs[this.type]]

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

    def getAttribute(name: String): String = {
        htmlElement.getAttribute(name)
    }

    def setAttribute(name: String, value: String): this.type = {
        htmlElement.setAttribute(name, value)
        this
    }

    def addCssClass(cssClass: String): this.type = {
        removeCssClass(cssClass)
        setAttribute("class", getAttribute("class") + " " + cssClass)
        this
    }

    def removeCssClass(cssClass: String): this.type = {
        setAttribute("class", getAttribute("class").replaceAllLiterally(cssClass, ""))
        this
    }

    def hide() {
        setAttribute("style", "display: none")
    }

    def show(displayStyle: String = "block") {
        setAttribute("style", "display: " + displayStyle)
    }

    def id: String = htmlElement.id

    def id_=(value: String) {
        htmlElement.id = value
    }

    def removeAllChildNodes() {
        while (htmlElement.hasChildNodes) {
            htmlElement.removeChild(htmlElement.firstChild)
        }
    }

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
