package s2js.adapters.goog {

import s2js.adapters.js.dom.{Frame, Input}
import s2js.adapters.goog.events.EventTarget


class History(
                 opt_invisible: Boolean = false,
                 opt_blankPageUrl: String = "",
                 opt_input: Input = null,
                 opt_iframe: Frame = null) extends EventTarget
{

    def setEnabled(enable: Boolean) {}

    def getToken(): String = ""

    def setToken(token: String, opt_title: String = "") {}

    def replaceToken(token: String, opt_title: String = "") {}
}

}

package s2js.adapters.goog.history {

import s2js.adapters.js.browser.Window
import s2js.adapters.goog.events.EventTarget

object EventType
{
    var NAVIGATE = "navigate"
}

class Event(
               var token: String,
               var isNavigation: Boolean) extends s2js.adapters.goog.events.Event(EventType.NAVIGATE)
{
}

object Html5History
{

    class TokenTransformer

}

class Html5History(
                      opt_win: Window = null,
                      opt_transformer: Html5History.TokenTransformer = null) extends EventTarget
{

    def isSupported(opt_win: Window = null): Boolean = false

    def setEnabled(enable: Boolean) {}

    def getToken(): String = ""

    def setToken(token: String, opt_title: String = "") {}

    def replaceToken(token: String, opt_title: String = "") {}
}

}