package cz.payola.web.client.views.elements

import cz.payola.web.client.views.Component
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom
import dom.Element

class Div(val innerElements: Seq[Component] = List(), additionalClass: String = "") extends Component
{
    val div = document.createElement[dom.Element]("div")

    div.setAttribute("class", additionalClass)

    def render(parent: Element = document.body) = {
        parent.appendChild(div)

        innerElements.map(_.render(div))
    }

    def setAttribute(attributeName: String, value: String) = {
        div.setAttribute(attributeName, value)
    }

    def addClass(addClass: String) = {
        div.setAttribute("class", div.getAttribute("class").replaceAllLiterally(addClass, "") + " " + addClass)
    }

    def removeClass(remClass: String) = {
        div.setAttribute("class", div.getAttribute("class").replaceAllLiterally(remClass, ""))
    }

    def getDomElement: Element = {
        div
    }

    def setId(id: String) = {
        div.setAttribute("id", id)
    }
}
