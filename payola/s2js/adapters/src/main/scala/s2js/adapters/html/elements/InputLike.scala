package s2js.adapters.html.elements

import s2js.adapters.events._

trait InputLike extends EventTarget
{
    var disabled: Boolean

    val form: Form

    var name: String

    var value: String

    var onchange: Event[this.type] => Unit

    def focus()
}

trait TextInputLike extends InputLike
{
    var defaultValue: String

    var readOnly: Boolean

    var value: String

    def select()
}
