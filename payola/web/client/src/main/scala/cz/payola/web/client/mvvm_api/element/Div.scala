package cz.payola.web.client.mvvm_api.element

import cz.payola.web.client.mvvm_api.Component
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom
import cz.payola.web.client.events.{ClickedEvent, ClickedEventArgs}
import dom.Element

class Div(val innerElements: Seq[Component], val addClass: String = "") extends Component
{
    val div = document.createElement[dom.Anchor]("div")

    def render(parent: Element = document.body) = {
        parent.appendChild(div)

        innerElements.map(_.render(div))
    }
}
