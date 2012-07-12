package cz.payola.web.client.views.events

import s2js.adapters.js.browser
import cz.payola.web.client.events.Event

class BrowserEvent[A] extends Event[A, BrowserEventArgs[A], Boolean]
{
    protected def resultsFolderInitializer = true

    protected def resultsFolderReducer(stackTop: Boolean, currentHandlerResult: Boolean) = {
        stackTop && currentHandlerResult
    }

    def trigger(target: A, event: browser.Event): Boolean = {
        trigger(BrowserEventArgs[A](target, event))
    }
}
