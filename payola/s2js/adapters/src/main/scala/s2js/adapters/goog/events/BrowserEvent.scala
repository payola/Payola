package s2js.adapters.goog.events

import s2js.adapters.js.dom.Node

class BrowserEvent(e: s2js.adapters.js.browser.Event = null, currentTarget: Node = null) extends Event("")
{
    def isMouseActionButton: Boolean = false

    val altKey: Boolean = false

    val button: Int = 0

    val clientX: Double = 0

    val clientY: Double = 0

    val ctrlKey: Boolean = false

    val metaKey: Boolean = false

    val offsetX: Int = 0

    val offsetY: Int = 0

    val platformModifierKey: Boolean = false

    val relatedTarget: Node = null

    val screenX: Int = 0

    val screenY: Int = 0

    val shiftKey: Boolean = false

    val target: Node = null

    val pageX: Int = 0

    val pageY: Int = 0
}

object BrowserEvent
{

    object MouseButton
    {
        val LEFT = 0

        val MIDDLE = 1

        val RIGHT = 2
    }

}
