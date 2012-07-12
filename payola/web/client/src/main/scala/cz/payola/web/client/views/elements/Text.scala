package cz.payola.web.client.views.elements

import cz.payola.web.client.views.Component
import cz.payola.web.client.views.Component
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom.{Element, Node}

class Text(var value: String) extends Component
{
    private var parent: Element = null

    private var element: Element = null

    def setText(text: String) = {
        if (parent != null) {
            parent.removeChild(element)
            value = text
            render(parent)
        }
    }

    def render(parent: Element = document.body) = {
        this.parent = parent
        element = document.createTextNode(value)
        parent.appendChild(element)
    }

    def getDomElement: Element = {
        element
    }
}
