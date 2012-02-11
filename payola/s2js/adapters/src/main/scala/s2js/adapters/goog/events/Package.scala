package s2js.adapters.goog.events

object `package` {
    def listen(src: AnyRef, eventType: String, fn: () => Any) {}

    def listen(src: AnyRef, eventTypes: List[String], fn: () => Any) {}

    def listen[T <: Event](src: AnyRef, events: List[String], fn: (T) => Any) {}

    def listen[T <: Event](src: AnyRef, event: String, fn: (T) => Any) {}
}
