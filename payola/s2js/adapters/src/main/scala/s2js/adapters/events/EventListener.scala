package s2js.adapters.events

trait EventListener
{
    def handleEvent(event: Event[_ <: EventTarget])
}
