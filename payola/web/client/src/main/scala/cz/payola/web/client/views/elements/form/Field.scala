package cz.payola.web.client.views.elements.form

import s2js.adapters.html
import cz.payola.web.client.View
import cz.payola.web.client.events.SimpleUnitEvent

trait Field[A] extends View
{
    val changed = new SimpleUnitEvent[this.type]

    def formHtmlElement: html.elements.InputLike

    def value: A

    def value_=(newValue: A) {
        if (newValue != value) {
            updateValue(newValue)
            changed.triggerDirectly(this)
        }
    }

    def isActive: Boolean

    def isActive_=(value: Boolean)

    protected def updateValue(newValue: A)
}
