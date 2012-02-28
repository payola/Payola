package s2js.adapters.goog.events

import s2js.adapters.goog.Disposable

class EventHandler(handler: AnyRef = null) extends Disposable
{
    def getListenerCount: Int = 0

    def handleEvent(e: Event) {}

    def listen(src: AnyRef, eventType: String, fn: () => Any): EventHandler = null

    def listen(src: AnyRef, eventTypes: List[String], fn: () => Any): EventHandler = null

    def listen[T <: Event](src: AnyRef, eventType: String, fn: (T) => Any): EventHandler = null

    def listen[T <: Event](src: AnyRef, eventTypes: List[String], fn: (T) => Any): EventHandler = null

    def listenOnce(src: AnyRef, eventType: String, fn: () => Any): EventHandler = null

    def listenOnce(src: AnyRef, eventTypes: List[String], fn: () => Any): EventHandler = null

    def listenOnce[T <: Event](src: AnyRef, eventType: String, fn: (T) => Any): EventHandler = null

    def listenOnce[T <: Event](src: AnyRef, eventTypes: List[String], fn: (T) => Any): EventHandler = null

    def removeAll() {}
}


