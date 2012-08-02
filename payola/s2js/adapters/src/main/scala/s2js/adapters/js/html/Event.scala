package s2js.adapters.js.html

abstract class Event
{
    val bubbles: Boolean

    val cancelable: Boolean

    val currentTarget: Element

    val target: Element

    val timeStamp: Long

    val `type`: String

    def preventDefault()

    def stopPropagation()
}
