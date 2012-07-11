package cz.payola.web.client.mvvm.element

import cz.payola.web.client.mvvm.Component
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom
import dom.Element

class Strong(val innerElements: Seq[Component] = List(), val addClass: String = "") extends Component
{
    val strong = document.createElement[dom.Element]("strong")
    strong.setAttribute("class", addClass)

    def render(parent: Element = document.body) = {
        parent.appendChild(strong)
        innerElements.map(_.render(strong))
    }

    def getDomElement : Element = {
        strong
    }
}
