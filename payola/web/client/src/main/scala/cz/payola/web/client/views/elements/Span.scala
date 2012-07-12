package cz.payola.web.client.views.elements

import cz.payola.web.client.views.Component
import cz.payola.web.client.views.Component
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom
import dom.Element
import cz.payola.web.client.events._
import cz.payola.web.client.views.events._

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

    def getDomElement: Element = {
        span
    }
}
