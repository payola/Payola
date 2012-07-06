package cz.payola.web.client.mvvm.element

import cz.payola.web.client.mvvm.Component
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom
import dom.{Element, Node}
import cz.payola.web.client.events._

class Anchor(val innerElements: Seq[Component], val href: String = "#", var additionalClass: String = "") extends Component
{
    val a = document.createElement[dom.Anchor]("a")
    a.setAttribute("href",href)
    a.setAttribute("class", additionalClass)

    val clicked = new ClickedEvent[Anchor]()

    def render(parent: Element = document.body) = {
        parent.appendChild(a)

        innerElements.map(_.render(a))
    }

    a.onclick = {
        event => clicked.trigger(new ClickedEventArgs(this))
    }

    def addClass(addedClass: String){
        additionalClass = additionalClass + " " + addedClass;
        a.setAttribute("class", additionalClass)
    }

    def getDomElement : Element = {
        a
    }
}
