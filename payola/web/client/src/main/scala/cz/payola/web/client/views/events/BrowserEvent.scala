package cz.payola.web.client.views.events

import cz.payola.web.client.events.Event

class BrowserEvent[A] extends Event[A, BrowserEvent[A], Boolean]
{
    protected def resultsFolderInitializer = true

    protected def resultsFolderReducer(stackTop: Boolean, currentHandlerResult: Boolean) = {
        stackTop && currentHandlerResult
    }
}
