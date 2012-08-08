package cz.payola.web.client.events

import s2js.adapters.events._

class MouseEventArgs[+A](
    target: A,
    val screenX: Double,
    val screenY: Double,
    val clientX: Double,
    val clientY: Double,
    val ctrlKey: Boolean,
    val shiftKey: Boolean,
    val altKey: Boolean,
    val metaKey: Boolean,
    val button: Int,
    val buttons: Int)
    extends EventArgs[A](target)

object MouseEventArgs
{
    def apply[A](target: A, event: MouseEvent[_]): MouseEventArgs[A] = {
        new MouseEventArgs[A](
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
            event.buttons
        )
    }
}
