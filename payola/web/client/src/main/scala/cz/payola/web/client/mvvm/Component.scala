package cz.payola.web.client.mvvm

import s2js.adapters.js.browser.document
import s2js.adapters.js.dom.Element

trait Component
{
    def render(parent: Element = document.body)
    def destroy() = { }
    def getDomElement() : Element
}
