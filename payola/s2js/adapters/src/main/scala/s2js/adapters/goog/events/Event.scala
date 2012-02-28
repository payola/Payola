package s2js.adapters.goog.events

import s2js.adapters.goog.Disposable

class Event(eventType: String, target: AnyRef = null) extends Disposable
{
    def preventDefault() {}

    def stopPropagation() {}
}

object Event
{
    def preventDefault(e: Event) {}

    def stopPropagation(e: Event) {}
}
