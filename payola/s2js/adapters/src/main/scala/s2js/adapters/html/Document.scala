package s2js.adapters.html

import s2js.adapters.js.Date
import s2js.adapters.dom
import s2js.adapters.events._

trait Document extends s2js.adapters.dom.Document with DocumentEvent with EventTarget
{
    type ElementType = Element

    val body: Element

    val cookie: String

    val documentMode: String

    val domain: String

    val lastModified: Date

    val readyState: String

    val referrer: String

    var title: String

    val URL: String

    var onkeydown: KeyboardEvent[this.type] => Boolean

    var onkeypress: KeyboardEvent[this.type] => Boolean

    var onkeyup: KeyboardEvent[this.type] => Boolean

    var onload: Event[this.type] => Unit

    var onresize: UIEvent[this.type] => Unit

    var onscroll: UIEvent[this.type] => Unit

    var onselect: Event[this.type] => Unit

    var onunload: Event[this.type] => Unit

    var onmousewheel: WheelEvent[this.type] => Boolean

    def getElementsByClassName(cssClass: String): dom.NodeList[ElementType]

    def createRange(): Range
}
