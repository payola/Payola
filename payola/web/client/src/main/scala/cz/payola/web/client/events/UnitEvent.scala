package cz.payola.web.client.events

/**
 * This event contains the most simple implementation of the resultsFolderReducer for the type Unit. It does nothing.
 * @tparam A Type of the event target (an object that triggers the event).
 * @tparam B Type of the event arguments.
 */
class UnitEvent[A, B <: EventArgs[A]] extends Event[A, B, Unit]
{
    protected def resultsFolderReducer(stackTop: Unit, currentHandlerResult: Unit) { }

    protected def resultsFolderInitializer { }
}

class SimpleUnitEvent[A] extends UnitEvent[A, EventArgs[A]]
{
    def triggerDirectly(target: A) {
        trigger(new EventArgs[A](target))
    }
}
