package s2js.adapters.js.html.elements

import s2js.adapters.js.html.Event

trait InputLike
{
    var disabled: Boolean

    val form: Form

    var name: String

    var value: String

    var onchange: (Event => Boolean)

    def focus()
}

trait TextInputLike extends InputLike
{
    var defaultValue: String

    var readOnly: Boolean

    var value: String

    def select()
}
