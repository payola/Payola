package goog.dom

import js.browser.Window

class ViewportSizeMonitor(opt_window:Window=null) extends goog.events.EventTarget {
    def getSize():goog.math.Size = null
}

object ViewportSizeMonitor {
    var WINDOW_SIZE_POLL_RATE = 500;
    def getInstanceForWindow(opt_window:Window=null):ViewportSizeMonitor = null
}
