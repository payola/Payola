package cz.payola.web.client.views

import s2js.adapters.js.dom

abstract class Component
{
    val domElement: dom.Element

    def render(parent: dom.Node) {
        parent.appendChild(domElement)
    }

    def destroy() {
        domElement.parentNode.removeChild(domElement)
    }
}
