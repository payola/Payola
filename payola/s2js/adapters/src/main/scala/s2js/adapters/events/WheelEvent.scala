package s2js.adapters.events

trait WheelEvent[+A <: EventTarget] extends MouseEvent[A]
{
    val wheelDelta: Double

    val deltaX: Double

    val deltaY: Double

    val deltaZ: Double

    val deltaMode: Int
}

object WheelEvent
{
    val DOM_DELTA_PIXEL = 0x00

    val DOM_DELTA_LINE = 0x01

    val DOM_DELTA_PAGE = 0x02
}
