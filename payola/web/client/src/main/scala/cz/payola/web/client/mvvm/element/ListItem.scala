package cz.payola.web.client.mvvm.element

import cz.payola.web.client.mvvm.Component
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom.{Element, Node}

class ListItem(val innerElements: Seq[Component], val addClass: String = "") extends Component
{
    val li = document.createElement[Element]("li")
    li.setAttribute("class", addClass)

    def render(parent: Element = document.body) = {
        parent.appendChild(li)

        innerElements.map(_.render(li))
    }

    def getDomElement : Element = {
        li
    }
}
