package cz.payola.web.client.events

import s2js.adapters.events._

class KeyboardEventArgs[+A](
    target: A,
    val char: String,
    val key: String,
    val keyCode: Int,
    val location: Int,
    val ctrlKey: Boolean,
    val shiftKey: Boolean,
    val altKey: Boolean,
    val metaKey: Boolean,
    val repeat: Boolean,
    val locale: String)
    extends EventArgs[A](target)

object KeyboardEventArgs
{
    def apply[A](target: A, event: KeyboardEvent[_]): KeyboardEventArgs[A] = {
        new KeyboardEventArgs[A](
            target,
            event.char,
            event.key,
            event.keyCode,
            event.location,
            event.ctrlKey,
            event.shiftKey,
            event.altKey,
            event.metaKey,
            event.repeat,
            event.locale
        )
    }
}

