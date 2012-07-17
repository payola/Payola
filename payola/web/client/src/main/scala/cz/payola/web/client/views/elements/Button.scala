package cz.payola.web.client.views.elements

import s2js.adapters.js.dom
import cz.payola.web.client.views._
import cz.payola.web.client.View

class Button(subView: View, cssClass: String = "")
    extends ElementView[dom.Button]("button", List(subView), cssClass + " btn")
{
    setAttribute("type", "button")

    def setIsEnabled(isEnabled: Boolean) {
        if (isEnabled) {
            removeCssClass("disabled")
            domElement.disabled = false
        } else {
            addCssClass("disabled")
            domElement.disabled = true
        }
    }
}
