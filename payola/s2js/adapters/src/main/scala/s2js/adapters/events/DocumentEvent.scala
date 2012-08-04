package s2js.adapters.events

trait DocumentEvent
{
    def createEvent[A <: Event[_ <: EventTarget]](eventInterface: String): A
}
