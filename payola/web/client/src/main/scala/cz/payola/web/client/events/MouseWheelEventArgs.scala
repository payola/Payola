package cz.payola.web.client.events

import s2js.adapters.js._
import s2js.adapters.events._

class MouseWheelEventArgs[+A](
    target: A,
    screenX: Double,
    screenY: Double,
    clientX: Double,
    clientY: Double,
    ctrlKey: Boolean,
    shiftKey: Boolean,
    altKey: Boolean,
    metaKey: Boolean,
    button: Int,
    buttons: Int,
    val wheelDelta: Double,
    val deltaX: Double,
    val deltaY: Double,
    val deltaZ: Double,
    val deltaMode: Int)
    extends MouseEventArgs[A](
        target,
        screenX,
        screenY,
        clientX,
        clientY,
        ctrlKey,
        shiftKey,
        altKey,
        metaKey,
        button,
        buttons)

object MouseWheelEventArgs
{
    def apply[A](target: A, event: WheelEvent[_]): MouseWheelEventArgs[A] = {
        new MouseWheelEventArgs[A](
            target,
            event.screenX,
            event.screenY,
            event.clientX,
            event.clientY,
            event.ctrlKey,
            event.shiftKey,
            event.altKey,
            event.metaKey,
            event.button,
            event.buttons,
            if (isNaN(event.wheelDelta)) event.detail / 120 else (- event.wheelDelta / 3),
            event.deltaX,
            event.deltaY,
            event.deltaZ,
            event.deltaMode
        )
    }
}
