package s2js.adapters.js.html.elements

import s2js.adapters.js.html._

abstract class Form extends Element
{
    var acceptCharset: String

    var action: String

    var enctype: String

    var length: Int

    var method: String

    var name: String

    var onreset: Event => Boolean

    var onsumbmit: Event => Boolean

    def reset()

    def submit()
}
