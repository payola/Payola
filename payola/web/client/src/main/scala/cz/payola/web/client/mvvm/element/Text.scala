package cz.payola.web.client.mvvm.element

import cz.payola.web.client.mvvm.Component
import s2js.adapters.js.browser.document
import s2js.adapters.js.dom.{Element, Node}

class Text(var value: String) extends Component
{
    private var parentHtml = ""
    private var parent: Element = null

    def setText(text: String) = {
        value = text
        parent.innerHTML = parentHtml+text
    }

    def render(parent: Element) = {
        this.parent = parent
        parentHtml = this.parent.innerHTML
        this.parent.innerHTML = parentHtml+value
    }
}
