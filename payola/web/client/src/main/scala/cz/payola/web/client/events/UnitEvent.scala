package cz.payola.web.client.events

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
