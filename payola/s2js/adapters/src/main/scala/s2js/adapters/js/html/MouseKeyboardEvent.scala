package s2js.adapters.js.html

abstract class MouseKeyboardEvent extends Event
{
    /** Returns whether or not the "ALT" key was pressed when an event was triggered. */
    val altKey: Boolean

    /** Returns which mouse button was clicked when an event was triggered. */
    val button: Int

    /**
     * Returns the horizontal coordinate of the mouse pointer, relative to the current window, when an event was
     * triggered.
     */
    val clientX: Double

    /**
     * Returns the vertical coordinate of the mouse pointer, relative to the current window, when an event was
     * triggered.
     */
    val clientY: Double

    /** Returns whether or not the "CTRL" key was pressed when an event was triggered. */
    val ctrlKey: Boolean

    /** Returns the code of a key. */
    val keyCode: Int

    /** Returns the location of the key on the advice. */
    val keyLocation: Int

    /** Returns whether or not the "meta" key was pressed when an event was triggered. */
    val metaKey: Boolean

    /** Returns the horizontal coordinate of the mouse pointer, relative to the screen, when an event was triggered. */
    val screenX: Double

    /** Returns the vertical coordinate of the mouse pointer, relative to the screen, when an event was triggered. */
    val screenY: Double

    /** Returns whether or not the "SHIFT" key was pressed when an event was triggered. */
    val shiftKey: Boolean

    /** Returns scrolling force of the mouse wheel. */
    val wheelDelta: Int

    /** Returns scrolling force of the mouse wheel in Mozilla Firefox browser. */
    val detail: Int
}
