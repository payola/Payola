import browser._

package goog {

    class History(
        opt_invisible:Boolean=false, 
        opt_blankPageUrl:String="", 
        opt_input:HTMLInputElement=null, 
        opt_iframe:HTMLFrameElement=null) extends goog.events.EventTarget {
        
        def setEnabled(enable:Boolean) {}
        def getToken():String = ""
        def setToken(token:String, opt_title:String = "") {}
        def replaceToken(token:String, opt_title:String = "") {}
    }
}

package goog.history {

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

// vim: set ts=4 sw=4 et:
