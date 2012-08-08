package s2js.adapters.events

trait UIEvent[+A <: EventTarget] extends Event[A]
{
    val detail: Int
}
