package cz.payola.web.client.events

import s2js.adapters.js.browser
import s2js.adapters.js.html.MouseKeyboardEvent

object BrowserEventArgs
{
    def apply[A](target: A, event: MouseKeyboardEvent): BrowserEventArgs[A] = {
        new BrowserEventArgs[A](
            target,
            event.altKey,
            event.button,
            event.clientX,
            event.clientY,
            event.ctrlKey,
            event.keyCode,
            event.keyLocation,
            event.metaKey,
            event.screenX,
            event.screenY,
            event.shiftKey,
            if (s2js.adapters.js.isNaN(event.wheelDelta)) event.detail / 120 else (- event.wheelDelta / 3)
        )
    }
}

/**
  * MouseKeyboardEvent arguments of a browser event.
  * @param altKey Whether the "ALT" key is pressed.
  * @param button Mouse button that is pressed.
  * @param clientX Horizontal coordinate of the mouse pointer, relative to the current window.
  * @param clientY Vertical coordinate of the mouse pointer, relative to the current window.
  * @param ctrlKey Whether the "CTRL" key is pressed.
  * @param keyCode Code of the key.
  * @param keyLocation Location of the key on the advice.
  * @param metaKey Whether the "meta" key is pressed.
  * @param screenX Horizontal coordinate of the mouse pointer, relative to the screen.
  * @param screenY Vertical coordinate of the mouse pointer, relative to the screen.
  * @param shiftKey Whether the "SHIFT" key is pressed.
  * @param wheelDelta Scrolling force of the mouse wheel.
  * @tparam A Type of the event target.
  */
class BrowserEventArgs[+A](
    target: A,
    val altKey: Boolean,
    val button: Int,
    val clientX: Double,
    val clientY: Double,
    val ctrlKey: Boolean,
    val keyCode: Int,
    val keyLocation: Int,
    val metaKey: Boolean,
    val screenX: Double,
    val screenY: Double,
    val shiftKey: Boolean,
    val wheelDelta: Int)
    extends EventArgs[A](target)
