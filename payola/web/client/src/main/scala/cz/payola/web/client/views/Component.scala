package cz.payola.web.client.views

import s2js.adapters.js.browser.document
import s2js.adapters.js.dom.Element

trait Component
{
    def render(parent: Element)

    def destroy() {

    }

    def getDomElement: Element
}
