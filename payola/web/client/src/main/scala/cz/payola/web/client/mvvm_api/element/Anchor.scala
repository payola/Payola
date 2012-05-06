package cz.payola.web.client.mvvm_api.element

import cz.payola.web.client.mvvm_api.Component
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom
import cz.payola.web.client.events.{ClickedEvent, ClickedEventArgs}
import dom.{Element, Node}

class Anchor(val innerElements: Seq[Component], val href: String, val addClass: String = "") extends Component
{
    val a = document.createElement[dom.Anchor]("a")
    a.setAttribute("href",href)

    val clicked = new ClickedEvent[Anchor]()

    def render(parent: Element = document.body) = {
        parent.appendChild(a)

        innerElements.map(_.render(a))
    }

    a.onclick = {
        event => clicked.trigger(new ClickedEventArgs(this))
    }
}
