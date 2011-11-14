package goog {

import js.dom.{Frame, Input}


class History(
        opt_invisible:Boolean=false, 
        opt_blankPageUrl:String="", 
        opt_input:Input=null,
        opt_iframe:Frame=null) extends goog.events.EventTarget {
        
        def setEnabled(enable:Boolean) {}
        def getToken():String = ""
        def setToken(token:String, opt_title:String = "") {}
        def replaceToken(token:String, opt_title:String = "") {}
    }
}

package goog.history {

import js.browser.Window

object EventType {
        var NAVIGATE = "navigate" 
    }

    class Event(
        var token:String, 
        var isNavigation:Boolean) extends goog.events.Event(EventType.NAVIGATE) {
    }

    object Html5History {
        class TokenTransformer
    }

    class Html5History(
        opt_win:Window = null,
        opt_transformer:Html5History.TokenTransformer = null) extends goog.events.EventTarget {

        def isSupported(opt_win:Window=null):Boolean = false
        def setEnabled(enable:Boolean) {}
        def getToken():String = ""
        def setToken(token:String, opt_title:String = "") {}
        def replaceToken(token:String, opt_title:String = "") {}
    }
}