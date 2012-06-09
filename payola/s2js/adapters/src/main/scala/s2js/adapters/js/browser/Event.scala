package s2js.adapters.js.browser

abstract class Event {
    def preventDefault

    /**
     * Returns whether or not the "ALT" key was pressed when an event was triggered.
     */
    val altKey = false

    /**
     * Returns which mouse button was clicked when an event was triggered.
     */
    val button = 0

    /**
     * Returns the horizontal coordinate of the mouse pointer, relative to the current window, when an event was triggered
     */
    val clientX = 0.0

    /**
     * Returns the vertical coordinate of the mouse pointer, relative to the current window, when an event was triggered
     */
    val clientY = 0.0

    /**
     * Returns whether or not the "CTRL" key was pressed when an event was triggered
     */
    val ctrlKey = false

    /**
     * Returns the identifier of a key
     */
    val keyIdentifier = 0

    /**
     * Returns the location of the key on the advice.
     */
    val keyLocation = 0

    /**
     * Returns whether or not the "meta" key was pressed when an event was triggered
     */
    val metaKey = false

    /**
     * Returns the horizontal coordinate of the mouse pointer, relative to the screen, when an event was triggered
     */
    val screenX = 0.0

    /**
     * Returns the vertical coordinate of the mouse pointer, relative to the screen, when an event was triggered
     */
    val screenY = 0.0

    /**
     * Returns whether or not the "SHIFT" key was pressed when an event was triggered
     */
    val shiftKey = false

    /**
      * Returns scrolling force of the mouse wheel
      */
    val wheelDelta = 0

    /**
      * Returns scrolling force of the mouse wheel in Mozilla Firefox browser
      */
    val detail = 0
}