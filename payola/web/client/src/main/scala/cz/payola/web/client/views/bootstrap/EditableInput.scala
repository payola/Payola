package cz.payola.web.client.views.bootstrap

import cz.payola.web.client.events.SimpleUnitEvent

trait EditableInput
{
    val changed = new SimpleUnitEvent[this.type]

    def value: String

    def value_=(value: String)

    def setIsActive(isActive: Boolean = true)
}
