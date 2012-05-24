package cz.payola.web.client.mvvm_api.element

import cz.payola.web.client.mvvm_api.Component
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom
import cz.payola.web.client.events.{ClickedEvent, ClickedEventArgs}
import dom.Element

class Div(val innerElements: Seq[Component] = List(), val addClass: String = "") extends Component
{
    val div = document.createElement[dom.Element]("div")
    div.setAttribute("class",addClass)

    def render(parent: Element = document.body) = {
        parent.appendChild(div)

        innerElements.map(_.render(div))
    }

    def setAttribute(attributeName: String, value: String) = {
        div.setAttribute(attributeName, value)
    }

    def removeClass(remClass: String) = {
        div.setAttribute("class",div.getAttribute("class").replaceAllLiterally(remClass, ""))
    }
}
