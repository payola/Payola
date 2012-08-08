package s2js.adapters.events

trait KeyboardEvent[+A <: EventTarget] extends UIEvent[A]
{
    val char: String

    val key: String

    val keyCode: Int

    val location: Int

    val ctrlKey: Boolean

    val shiftKey: Boolean

    val altKey: Boolean

    val metaKey: Boolean

    val repeat: Boolean

    val locale: String

    def getModifierState(key: String): Boolean
}

object KeyboardEvent
{
    val DOM_KEY_LOCATION_STANDARD = 0x00

    val DOM_KEY_LOCATION_LEFT = 0x01

    val DOM_KEY_LOCATION_RIGHT = 0x02

    val DOM_KEY_LOCATION_NUMPAD = 0x03

    val DOM_KEY_LOCATION_MOBILE = 0x04

    val DOM_KEY_LOCATION_JOYSTICK = 0x05
}
