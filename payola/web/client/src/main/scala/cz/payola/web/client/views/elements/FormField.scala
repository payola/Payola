package cz.payola.web.client.views.elements

import cz.payola.web.client.views._
import s2js.adapters.js.html
import bootstrap.EditableInput
import s2js.adapters.html.elements.InputLike
import s2js.adapters.html.Element

abstract class FormField[A <: html.Element with html.elements.InputLike](
    elementName: String,
    name: String,
    initialValue: String,
    title: Option[String],
    cssClass: String = "")
    extends ElementView[A](elementName, Nil, cssClass) with EditableInput
{
    keyReleased += { e =>
        changed.triggerDirectly(this)
        true
    }

    value = initialValue
    setAttribute("name", name)
    setAttribute("id", name)
    title.foreach {
        t =>
            setAttribute("placeholder", t)
            setAttribute("title", t)
    }

    def maxLength_=(maxLength: Int) {
        setAttribute("maxlength", maxLength.toString)
    }

    def maxLength: Int = {
        getAttribute("maxlength").toInt
    }

    def value: String = {
        htmlElement.value
    }

    def value_=(value: String) {
        htmlElement.value = value
    }

    def setIsActive(isActive: Boolean = true) {
        if (isActive) {
            addCssClass("active")
        } else {
            removeCssClass("active")
        }
    }

    def setIsEnabled(isEnabled: Boolean) {
        if (isEnabled) {
            removeCssClass("disabled")
            htmlElement.disabled = false
        } else {
            addCssClass("disabled")
            htmlElement.disabled = true
        }
    }
}
