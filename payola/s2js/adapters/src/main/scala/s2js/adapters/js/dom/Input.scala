package s2js.adapters.js.dom

import s2js.adapters.js.browser.Event

abstract class Input extends Element
{
    def focus()

    var value: String

    var onchange: (Event => Unit)
    var onkeyup: (Event => Unit)
    var onclick: (Event => Unit)
}
