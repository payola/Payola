package cz.payola.web.client.views.elements.form.fields

import s2js.adapters.html
import cz.payola.web.client.views.ElementView
import cz.payola.web.client.View
import cz.payola.web.client.views.elements.form.Field

abstract class InputLikeView[A <: html.Element with html.elements.InputLike, B](
    htmlElementName: String,
    subViews: Seq[View],
    val name: String,
    initialValue: B,
    title: String,
    cssClass: String)
    extends ElementView[A](htmlElementName, subViews, cssClass) with Field[B]
{
    private var _isActive: Boolean = false

    htmlElement.name = name
    htmlElement.id = name
    setAttribute("placeholder", title)
    setAttribute("title", title)
    value = initialValue

    htmlElement.onchange = { _ =>
        changed.triggerDirectly(this)
    }

    def formHtmlElement = htmlElement

    def enable() {
        htmlElement.disabled = false
        removeCssClass("disabled")
    }

    def disable() {
        htmlElement.disabled = true
        addCssClass("disabled")
    }

    def setIsEnabled(isEnabled: Boolean) {
        if (isEnabled) {
            enable()
        } else {
            disable()
        }
    }

    def isActive = _isActive

    def isActive_=(newValue: Boolean) {
        _isActive = newValue
        if (isActive) {
            addCssClass("active")
        } else {
            removeCssClass("active")
        }
    }

    def triggerChangedOnKeyReleased() {
        keyReleased += { e =>
            if (e.keyCode > 45) {
                changed.triggerDirectly(this)
            }
            true
        }
    }
}
