package cz.payola.web.client.events

class BooleanEvent[A, B <: EventArgs[A]] extends Event[A, B, Boolean]
{
    protected def resultsFolderInitializer: Boolean = {
        true
    }

    protected def resultsFolderReducer(stackTop: Boolean, currentHandlerResult: Boolean): Boolean = {
        stackTop && currentHandlerResult
    }
}
