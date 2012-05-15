package cz.payola.web.client.mvvm_api.element

import cz.payola.web.client.mvvm_api.Component
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom
import cz.payola.web.client.events.{ClickedEvent, ClickedEventArgs}
import dom.Element

class I(val innerElements: Seq[Component], val addClass: String = "") extends Component
{
    val i = document.createElement[dom.Element]("i")

    def render(parent: Element = document.body) = {
        parent.appendChild(i)

        innerElements.map(_.render(i))
    }
}
