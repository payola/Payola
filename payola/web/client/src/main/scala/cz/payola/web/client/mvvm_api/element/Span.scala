package cz.payola.web.client.mvvm_api.element

import cz.payola.web.client.mvvm_api.Component
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom
import dom.Element
import cz.payola.web.client.events._

class Span(val innerElements: Seq[Component], val addClass: String = "") extends Component
{
    val clicked = new ClickedEvent[Span]

    val span = document.createElement[dom.Element]("span")
    span.setAttribute("class", addClass)

    def render(parent: Element = document.body) = {
        parent.appendChild(span)

        innerElements.map(_.render(span))
    }
}