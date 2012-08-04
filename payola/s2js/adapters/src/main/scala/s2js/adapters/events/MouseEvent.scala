package s2js.adapters.events

trait MouseEvent[+A <: EventTarget] extends UIEvent[A]
{
    val screenX: Double

    val screenY: Double

    val clientX: Double

    val clientY: Double

    val ctrlKey: Boolean

    val shiftKey: Boolean

    val altKey: Boolean

    val metaKey: Boolean

    val button: Int

    val buttons: Int

    val relatedTarget: EventTarget

    def getModifierState(key: String): Boolean
}
