package s2js.adapters.html.elements

import s2js.adapters.html._
import s2js.adapters.events.Event

trait Form extends Element
{
    var acceptCharset: String

    var action: String

    var enctype: String

    var length: Int

    var method: String

    var name: String

    var onreset: Event[this.type] => Boolean

    var onsubmit: Event[this.type] => Boolean

    def reset()

    def submit()
}
