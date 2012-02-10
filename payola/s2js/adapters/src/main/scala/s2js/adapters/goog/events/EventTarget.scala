package s2js.adapters.goog.events

import s2js.adapters.goog.Disposable

class EventTarget extends Disposable {
    def dispatchEvent(e: String): Boolean = false

    def dispatchEvent(e: Event): Boolean = false

    def dispatchEvent(e: Any): Boolean = false

    def getParentEventTarget(): EventTarget = null

    def setParentEventTarget(parent: EventTarget) {}
}






