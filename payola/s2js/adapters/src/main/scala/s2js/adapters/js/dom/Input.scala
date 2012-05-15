package s2js.adapters.js.dom

import s2js.adapters.js.browser.Event

abstract class Input extends Element
{
    def focus()

    var value: String

    var onchange: (Event => Boolean)
    var onkeyup: (Event => Boolean)
    var onclick: (Event => Boolean)
}
