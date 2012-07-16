package cz.payola.web.client.events

import s2js.adapters.js.browser
import cz.payola.web.client.events._

class BrowserEvent[A] extends BooleanEvent[A, BrowserEventArgs[A]]
{
    def triggerDirectly(target: A, event: browser.Event): Boolean = {
        trigger(BrowserEventArgs[A](target, event))
    }
}
