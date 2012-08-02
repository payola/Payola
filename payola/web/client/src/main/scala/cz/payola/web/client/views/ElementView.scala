package cz.payola.web.client.views

import scala.collection._
import s2js.adapters.js.html
import s2js.adapters.js.browser.document
import cz.payola.web.client.events.BrowserEvent
import cz.payola.web.client.View
import cz.payola.web.client.views.elements.Text
import cz.payola.web.client.views.algebra.Vector2D

abstract class ElementView[A <: html.Element](htmlElementName: String, val subViews: Seq[View], cssClass: String)
    extends View
{
    val htmlElement = document.createElement[A](htmlElementName)

    val keyPressed = new BrowserEvent[this.type]

    val keyReleased = new BrowserEvent[this.type]

    val mouseClicked = new BrowserEvent[this.type]

    val mouseDoubleClicked = new BrowserEvent[this.type]

    val mousePressed = new BrowserEvent[this.type]

    val mouseReleased = new BrowserEvent[this.type]

    val mouseMoved = new BrowserEvent[this.type]

    val mouseOut = new BrowserEvent[this.type]

    val mouseWheelRotated = new BrowserEvent[this.type]

    protected var parentElement: Option[html.Element] = None

    htmlElement.onkeydown = { e => keyPressed.triggerDirectly(this, e) }
    htmlElement.onkeyup = { e => keyReleased.triggerDirectly(this, e) }
    htmlElement.onclick = { e => mouseClicked.triggerDirectly(this, e) }
    htmlElement.ondblclick = { e => mouseDoubleClicked.triggerDirectly(this, e) }
    htmlElement.onmousedown = { e => mousePressed.triggerDirectly(this, e) }
    htmlElement.onmouseup = { e => mouseReleased.triggerDirectly(this, e) }
    htmlElement.onmousemove = { e => mouseMoved.triggerDirectly(this, e) }
    htmlElement.onmousewheel = { e => mouseWheelRotated.triggerDirectly(this, e) }
    htmlElement.onmouseout = { e => mouseOut.triggerDirectly(this, e) }
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

    def addCssClass(cssClass: String): this.type =  {
        removeCssClass(cssClass)
        setAttribute("class", getAttribute("class") + " " + cssClass)
        this
    }

    def removeCssClass(cssClass: String): this.type =  {
        setAttribute("class", getAttribute("class").replaceAllLiterally(cssClass, ""))
        this
    }

    def hide() {
        setAttribute("style","display: none")
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

    def topLeftCorner: Vector2D = {
        var offsetTop = 0.0
        var offsetLeft = 0.0
        var element: html.Element = htmlElement
        while (element != null) {
            offsetTop += element.offsetTop
            offsetLeft += element.offsetLeft
            element = element.offsetParent
        }
        Vector2D(offsetLeft, offsetTop)
    }
}
