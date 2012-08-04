package s2js.adapters.events

trait CustomEvent[+A <: EventTarget] extends Event[A]
{
    val detail: Any
}
