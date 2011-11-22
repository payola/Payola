package s2js.adapters.goog.dom

import s2js.adapters.js.browser.Window
import s2js.adapters.goog.events.EventTarget
import s2js.adapters.goog.math.Size

class ViewportSizeMonitor(opt_window: Window = null) extends EventTarget
{
    def getSize(): Size = null
}

object ViewportSizeMonitor
{
    var WINDOW_SIZE_POLL_RATE = 500;

    def getInstanceForWindow(opt_window: Window = null): ViewportSizeMonitor = null
}
