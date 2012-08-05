package cz.payola.web.client.views

import scala.collection._
import s2js.adapters.js.dom
import s2js.adapters.js.browser.document
import cz.payola.web.client.events.BrowserEvent
import cz.payola.web.client.View
import s2js.compiler.javascript
import s2js.adapters.js.browser.window
import cz.payola.web.client.views.elements.Text
import cz.payola.web.client.views.algebra.Vector2D

abstract class ElementView[A <: dom.Element](domElementName: String, val subViews: Seq[View], cssClass: String)
    extends View
{
    val domElement = document.createElement[A](domElementName)

    val keyPressed = new BrowserEvent[this.type]

    val keyReleased = new BrowserEvent[this.type]

    val mouseClicked = new BrowserEvent[this.type]

    val mouseDoubleClicked = new BrowserEvent[this.type]

    val mousePressed = new BrowserEvent[this.type]

    val mouseReleased = new BrowserEvent[this.type]

    val mouseMoved = new BrowserEvent[this.type]

    val mouseOut = new BrowserEvent[this.type]

    val mouseWheelRotated = new BrowserEvent[this.type]

    protected var parentElement: Option[dom.Element] = None

    domElement.onkeydown = { e => keyPressed.triggerDirectly(this, e) }
    domElement.onkeyup = { e => keyReleased.triggerDirectly(this, e) }
    domElement.onclick = { e => mouseClicked.triggerDirectly(this, e) }
    domElement.ondblclick = { e => mouseDoubleClicked.triggerDirectly(this, e) }
    domElement.onmousedown = { e => mousePressed.triggerDirectly(this, e) }
    domElement.onmouseup = { e => mouseReleased.triggerDirectly(this, e) }
    domElement.onmousemove = { e => mouseMoved.triggerDirectly(this, e) }
    domElement.onmousewheel = { e => mouseWheelRotated.triggerDirectly(this, e) }
    domElement.onmouseout = { e => mouseOut.triggerDirectly(this, e) }
    addCssClass(cssClass)

    def blockDomElement = domElement

    def render(parent: dom.Element) {
        parentElement = Some(parent)
        parent.appendChild(domElement)
        subViews.foreach { v =>
            new Text(" ").render(domElement)
            v.render(domElement)
        }
    }

    def destroy() {
        parentElement.foreach(_.removeChild(domElement))
    }

    def getAttribute(name: String): String = {
        domElement.getAttribute(name)
    }

    def setAttribute(name: String, value: String): this.type = {
        domElement.setAttribute(name, value)
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

    def id: String = {
        getAttribute("id")
    }

    def id_=(value: String) {
        setAttribute("id", value)
    }

    def hide(){
        setAttribute("style","display: none")
    }

    def show(displayStyle: String = "block"){
        setAttribute("style","display: "+displayStyle)
    }

    def removeAllChildNodes() {
        while (domElement.hasChildNodes) {
            domElement.removeChild(domElement.firstChild)
        }
    }

    @javascript("""
        var offsetTop = 0;
        var offsetLeft = 0;
        var element = self.domElement;
        while (element != null) {
            offsetTop += element.offsetTop;
            offsetLeft += element.offsetLeft;
            element = element.offsetParent;
        }
        return new cz.payola.web.client.views.algebra.Vector2D(offsetLeft, offsetTop);
                """)
    def topLeftCorner: Vector2D = Vector2D(0, 0)
}
