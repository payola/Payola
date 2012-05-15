package cz.payola.web.client.events

import cz.payola.web.client.mvvm_api.Component

class ComponentEvent[A, B <: EventArgs[A]] extends Event[A,B,Boolean]
{
    protected def handlerResultsFolder(stackTop: Boolean, currentHandlerResult: Boolean) = {
        stackTop && currentHandlerResult
    }

    protected def resultsFolderInitializer = true
}
