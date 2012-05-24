package s2js.adapters.js.dom

import s2js.adapters.js.browser.Event

abstract class Canvas extends Element
{
    var height: Double = 0

    var width: Double = 0

    def getContext[A <: CanvasContext](contextId: String): A

    //def focus()

    var onkeyup: (Event => Boolean)
    var onkeydown: (Event => Boolean)
    var onclick: (Event => Boolean)
    var ondblclick: (Event => Boolean)
    var onmousedown: (Event => Boolean)
    var onmouseup: (Event => Boolean)
    var onmousemove: (Event => Boolean)
}
