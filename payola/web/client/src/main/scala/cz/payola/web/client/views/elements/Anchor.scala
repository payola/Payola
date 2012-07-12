package cz.payola.web.client.views.elements

import cz.payola.web.client.views.Component
import cz.payola.web.client.views.Component
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom
import dom.{Element, Node}
import cz.payola.web.client.events._
import cz.payola.web.client.views.events._

class Anchor(val innerElements: Seq[Component], val href: String = "#", var additionalClass: String = "")
    extends Component
{
    val a = document.createElement[dom.Anchor]("a")

    a.setAttribute("href", href)
    a.setAttribute("class", additionalClass)

    val clicked = new BrowserEvent[Anchor]()

    def render(parent: Element = document.body) = {
        parent.appendChild(a)

        innerElements.map(_.render(a))
    }

    a.onclick = { e => clicked.trigger(this, e) }

    def addClass(addedClass: String) {
        additionalClass = additionalClass + " " + addedClass;
        a.setAttribute("class", additionalClass)
    }

    def getDomElement: Element = {
        a
    }
}
