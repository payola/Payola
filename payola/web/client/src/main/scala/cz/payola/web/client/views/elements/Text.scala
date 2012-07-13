package cz.payola.web.client.views.elements

import s2js.adapters.js.browser.document
import s2js.adapters.js.dom
import cz.payola.web.client.views.Component

class Text(initialValue: String) extends Component
{
    private var value = initialValue

    private var textNode: Option[dom.Node] = None

    def text: String = value

    def text_=(text: String) {
        value = text
        destroy()
        textNode.foreach(e => render(e.parentNode))
    }

    def render(parent: dom.Node) {
        textNode = Some(document.createTextNode(value))
        parent.appendChild(textNode.get)
    }

    def destroy() {
        textNode.foreach(e => e.parentNode.removeChild(e))
    }
}
