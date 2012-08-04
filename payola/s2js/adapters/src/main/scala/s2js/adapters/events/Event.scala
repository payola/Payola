package s2js.adapters.events

trait Event[+A <: EventTarget]
{
    val `type`: String

    val target: A

    val currentTarget: A

    val eventPhase: Int

    val bubbles: Boolean

    val cancelable: Boolean

    val timeStamp: Long

    val defaultPrevented: Boolean

    val isTrusted: Boolean

    def stopPropagation()

    def preventDefault()

    def initEvent(eventType: String, canBubble: Boolean, cancelable: Boolean)

    def stopImmediatePropagation()
}

object Event
{
    val NONE = 0

    val CAPTURING_PHASE = 1

    val AT_TARGET = 2

    val BUBBLING_PHASE = 3
}
