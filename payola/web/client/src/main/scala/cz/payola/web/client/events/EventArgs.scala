package cz.payola.web.client.events

import s2js.adapters.js.browser

/**
 *
 * @author jirihelmich
 * @created 4/17/12 2:08 PM
 * @package cz.payola.web.client.events
 */

class EventArgs[A](val target: A)
{
    private var altKeyVal = false

    /**
     * Returns whether or not the "ALT" key was pressed when an event was triggered.
     */
    def altKey: Boolean = {
        altKeyVal
    }

    private var buttonVal = 0

    /**
     * Returns which mouse button was clicked when an event was triggered.
     */
    def button: Int = {
        buttonVal
    }

    private var clientXVal = 0.0

    /**
     * Returns the horizontal coordinate of the mouse pointer, relative to the current window, when an event was triggered
     */
    def clientX: Double = {
        clientXVal
    }

    private var clientYVal = 0.0

    /**
     * Returns the vertical coordinate of the mouse pointer, relative to the current window, when an event was triggered
     */
    def clientY: Double = {
        clientYVal
    }

    private var ctrlKeyVal = false

    /**
     * Returns whether or not the "CTRL" key was pressed when an event was triggered
     */
    def ctrlKey: Boolean = {
        ctrlKeyVal
    }

    private var keyIdentifierVal = 0

    /**
     * Returns the identifier of a key
     */
    def keyIdentifier: Int = {
        keyIdentifierVal
    }

    private var keyLocationVal = 0

    /**
     * Returns the location of the key on the advice.
     */
    def keyLocation: Int = {
        keyLocationVal
    }

    private var metaKeyVal = false

    /**
     * Returns whether or not the "meta" key was pressed when an event was triggered
     */
    def metaKey: Boolean = {
        metaKeyVal
    }

    private var screenXVal = 0.0

    /**
     * Returns the horizontal coordinate of the mouse pointer, relative to the screen, when an event was triggered
     */
    def screenX: Double = {
        screenXVal
    }

    private var screenYVal = 0.0

    /**
     * Returns the vertical coordinate of the mouse pointer, relative to the screen, when an event was triggered
     */
    def screenY: Double = {
        screenYVal
    }

    private var shiftKeyVal = false

    /**
     * Returns whether or not the "SHIFT" key was pressed when an event was triggered
     */
    def shiftKey: Boolean = {
        shiftKeyVal
    }

    def set(event: browser.Event) {
        altKeyVal = event.altKey
        buttonVal = event.button
        clientXVal = event.clientX
        clientYVal = event.clientY
        ctrlKeyVal = event.ctrlKey
        keyIdentifierVal = event.keyIdentifier
        keyLocationVal = event.keyLocation
        metaKeyVal = event.metaKey
        screenXVal = event.screenX
        screenYVal = event.screenY
        shiftKeyVal = event.shiftKey
    }
}
