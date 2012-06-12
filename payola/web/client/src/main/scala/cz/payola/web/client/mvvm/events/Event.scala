package cz.payola.web.client.events

import collection.mutable.ArrayBuffer

abstract class Event[A, B <: EventArgs[A], C]
{

    private type EventHandler = B => C

    private val handlers = new ArrayBuffer[EventHandler]()

    protected def handlerResultsFolder(stackTop: C, currentHandlerResult: C) : C

    protected def resultsFolderInitializer : C

    def trigger(eventArgs: B) : C = {
        handlers.map(_(eventArgs)).fold(resultsFolderInitializer)(handlerResultsFolder _)
    }

    def +=(handler: EventHandler) = {
        handlers += handler
    }

    def -=(handler: EventHandler) = {
        handlers -= handler
    }
}
