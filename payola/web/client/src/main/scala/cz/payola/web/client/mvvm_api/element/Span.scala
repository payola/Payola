package cz.payola.web.client.mvvm_api.element

import cz.payola.web.client.mvvm_api.Component
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom
import cz.payola.web.client.events.{ClickedEvent, ClickedEventArgs}
import dom.Element

class Span(val innerElements: Seq[Component], val addClass: String = "") extends Component
{
    val span = document.createElement[dom.Element]("span")
    span.setAttribute("class",addClass)

    def render(parent: Element = document.body) = {
        parent.appendChild(span)

        innerElements.map(_.render(span))
    }
}
