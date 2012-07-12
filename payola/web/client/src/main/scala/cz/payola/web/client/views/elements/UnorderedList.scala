package cz.payola.web.client.views.elements

import cz.payola.web.client.views.Component
import cz.payola.web.client.views.Component
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom
import dom.Element
import cz.payola.web.client.events._

class UnorderedList(val innerElements: Seq[Component], val addClass: String = "") extends Component
{
    val ul = document.createElement[dom.Element]("ul")

    ul.setAttribute("class", addClass)

    def render(parent: Element = document.body) = {
        parent.appendChild(ul)
        innerElements.map(_.render(ul))
    }

    def getDomElement: Element = {
        ul
    }
}
