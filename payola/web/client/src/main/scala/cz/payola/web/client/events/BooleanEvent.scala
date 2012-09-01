package cz.payola.web.client.events

/**
 * Boolean event includes basic implementation of the resultsFolderReducer of for the Boolean type.
 * It inits the fold stack with true. The folderReducer returns true if and only if all the handlers returns true.
 * @tparam A Type of the event target (an object that triggers the event).
 * @tparam B Type of the event arguments.
 */
class BooleanEvent[A, B <: EventArgs[A]] extends Event[A, B, Boolean]
{
    protected def resultsFolderInitializer: Boolean = {
        true
    }

    protected def resultsFolderReducer(stackTop: Boolean, currentHandlerResult: Boolean): Boolean = {
        stackTop && currentHandlerResult
    }
}

class SimpleBooleanEvent[A] extends BooleanEvent[A, EventArgs[A]]
