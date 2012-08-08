package s2js.adapters

abstract class jQuery
{
    def on(actionName: String, callback: Unit)

    def css(propertyName: String, value: String)

    def addClass(addClass: String)

    def removeClass(removeClass: String)
}

object jQuery
{
    def apply(selector: String): jQuery = null

    def apply(thisPointer: Any): jQuery = null
}
