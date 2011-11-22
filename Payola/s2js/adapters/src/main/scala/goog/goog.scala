package s2js.adapters.goog

import s2js.adapters.js.dom.Element


object `package`
{
    def getCssName(className: String, modifier: String = ""): String = ""

    def dispose(obj: Any) {}

    def base(me: Any, methodName: String, args: Map[String, Any]) {}
}

object css
{
    def getCssName(n: String) = "test"
}

object style
{
    def setOpacity(el: Element, opacity: Double) {}

    def showElement(el: Element, show: Boolean) {}

    def isRightToLeft(el: Element) = false
}

object string
{
    def createUniqueString(): String = ""
}

class Disposable
{
    def isDisposed(): Boolean = false

    def dispose() {}
}

