package cz.payola.web.client.views.elements

import s2js.adapters.html
import cz.payola.web.client.views._
import cz.payola.web.client.View
import cz.payola.web.client.views.bootstrap.Icon

object Button {
    def apply(caption: String, cssClass: String = "btn btn-default") : Button = {
        new Button(new Text(caption), cssClass)
    }
}

class Button(subView: View, cssClass: String = "", icon: Icon = null)
    extends ElementView[html.elements.Button]("button", if (icon == null) {
        List(subView)
    } else {
        List(icon, subView)
    }, cssClass + " btn btn-default")
{
    setAttribute("type", "button")

    def setIsEnabled(isEnabled: Boolean): this.type = {
        if (isEnabled) {
            removeCssClass("disabled")
            htmlElement.disabled = false
        } else {
            addCssClass("disabled")
            htmlElement.disabled = true
        }

        this
    }
}
