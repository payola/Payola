package cz.payola.web.client.views.elements

import cz.payola.web.client.views._
import s2js.adapters.js.dom
import cz.payola.web.client.events._

abstract class FormField[A <: dom.Input](
    elementName: String,
    name: String,
    initialValue: String,
    title: Option[String],
    cssClass: String = "")
    extends ElementView[A](elementName, Nil, cssClass)
{
    val changed = new SimpleUnitEvent[this.type]

    keyReleased += { e =>
        changed.triggerDirectly(this)
        true
    }

    value = initialValue
    setAttribute("name", name)
    setAttribute("id", name)
    title.foreach { t =>
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
        domElement.value
    }

    def value_=(value: String) {
        domElement.value = value
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
            domElement.disabled = false
        } else {
            addCssClass("disabled")
            domElement.disabled = true
        }
    }
}
