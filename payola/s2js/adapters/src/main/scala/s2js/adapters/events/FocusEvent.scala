package s2js.adapters.events

trait FocusEvent[+A <: EventTarget] extends UIEvent[A]
{
    val relatedTarget: EventTarget
}
