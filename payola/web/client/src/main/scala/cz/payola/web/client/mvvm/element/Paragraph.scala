package cz.payola.web.client.mvvm.element

import cz.payola.web.client.mvvm.Component
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom
import dom.Element
import cz.payola.web.client.events._

class Paragraph(val innerElements: Seq[Component], val addClass: String = "") extends Component
{
    val p = document.createElement[dom.Element]("p")
    p.setAttribute("class", addClass)

    def render(parent: Element = document.body) = {
        parent.appendChild(p)
        innerElements.map(_.render(p))
    }

    def getDomElement : Element = {
        p
    }
}