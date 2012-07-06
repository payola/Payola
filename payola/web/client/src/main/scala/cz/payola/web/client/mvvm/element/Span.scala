package cz.payola.web.client.mvvm.element

import cz.payola.web.client.mvvm.Component
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom
import dom.Element
import cz.payola.web.client.events._

class Span(val innerElements: Seq[Component], val addClass: String = "") extends Component
{
    var clicked = new ClickedEvent[Span]

    val span = document.createElement[dom.Element]("span")
    span.setAttribute("class", addClass)

    span.onclick = { event =>
        val args = new ClickedEventArgs(this)
        args.set(event)
        clicked.trigger(args)
    }

    def render(parent: Element = document.body) = {
        parent.appendChild(span)

        innerElements.map(_.render(span))
    }

    def getDomElement : Element = {
        span
    }
}