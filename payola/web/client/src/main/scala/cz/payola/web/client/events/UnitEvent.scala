package cz.payola.web.client.events

abstract class UnitEvent[A, B <: EventArgs[A]] extends Event[A, B, Unit]
{
    protected def resultsFolderReducer(stackTop: Unit, currentHandlerResult: Unit) {

    }

    protected def resultsFolderInitializer {

    }
}
