package cz.payola.web.client.events

import s2js.adapters.events.MouseKeyboardEvent
import s2js.adapters.html.MouseKeyboardEvent
import s2js.adapters.js.html.MouseKeyboardEvent

class BrowserEvent[A] extends BooleanEvent[A, BrowserEventArgs[A]]
{
    def triggerDirectly(target: A, event: MouseKeyboardEvent): Boolean = {
        trigger(BrowserEventArgs[A](target, event))
    }
}
