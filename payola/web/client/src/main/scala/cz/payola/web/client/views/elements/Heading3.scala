package cz.payola.web.client.views.elements

import cz.payola.web.client.views.Component
import cz.payola.web.client.views.Component
import s2js.adapters.js.dom.Element
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom

class Heading3(val innerElements: Seq[Component], val addClass: String = "") extends Component
{
    val heading = document.createElement[dom.Element]("h3")

    heading.setAttribute("class", addClass)

    def render(parent: Element = document.body) = {
        parent.appendChild(heading)

        innerElements.map(_.render(heading))
    }

    def getDomElement: Element = {
        heading
    }
}
