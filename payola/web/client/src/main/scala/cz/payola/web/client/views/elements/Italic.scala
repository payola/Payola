package cz.payola.web.client.views.elements

import cz.payola.web.client.views.Component
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom
import dom.Element

class Italic(val innerElements: Seq[Component] = List(), val addClass: String = "") extends Component
{
    val i = document.createElement[dom.Element]("i")

    i.setAttribute("class", addClass)

    def render(parent: Element = document.body) = {
        parent.appendChild(i)

        innerElements.map(_.render(i))
    }

    def getDomElement: Element = {
        i
    }
}
