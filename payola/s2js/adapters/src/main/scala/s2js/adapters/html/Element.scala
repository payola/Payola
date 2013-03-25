package s2js.adapters.html

import s2js.adapters.dom
import s2js.adapters.events._

trait Element extends dom.Element with EventTarget
{
    var accessKey: String

    var className: String

    var clientHeight: Double

    var clientWidth: Double

    var dir: String

    var id: String

    var innerHTML: String

    var lang: String

    val offsetHeight: Double

    val offsetLeft: Double

    val offsetParent: Element

    val offsetTop: Double

    val offsetWidth: Double

    val scrollHeight: Double

    val scrollLeft: Double

    val scrollTop: Double

    val scrollWidth: Double

    var style: String

    var tabIndex: Int

    var title: String

    var onabort: Event[this.type] => Unit

    var onblur: FocusEvent[this.type] => Unit

    var onclick: MouseEvent[this.type] => Boolean

    var oncompositionstart: CompositionEvent[this.type] => Boolean

    var oncompositionupdate: CompositionEvent[this.type] => Unit

    var oncompositionend: CompositionEvent[this.type] => Unit

    var ondblclick: MouseEvent[this.type] => Unit

    var onerror: Event[this.type] => Unit

    var onfocus: FocusEvent[this.type] => Unit

    var onfocusin: FocusEvent[this.type] => Unit

    var onfocusout: FocusEvent[this.type] => Unit

    var onkeydown: KeyboardEvent[this.type] => Boolean

    var onkeypress: KeyboardEvent[this.type] => Boolean

    var onkeyup: KeyboardEvent[this.type] => Boolean

    var onload: Event[this.type] => Unit

    var onmousedown: MouseEvent[this.type] => Boolean

    var onmouseenter: MouseEvent[this.type] => Unit

    var onmouseleave: MouseEvent[this.type] => Unit

    var onmousemove: MouseEvent[this.type] => Boolean

    var onmouseout: MouseEvent[this.type] => Boolean

    var onmouseover: MouseEvent[this.type] => Boolean

    var onmouseup: MouseEvent[this.type] => Boolean

    var onscroll: UIEvent[this.type] => Unit

    var onselect: Event[this.type] => Unit

    var onunload: Event[this.type] => Unit

    var onmousewheel: WheelEvent[this.type] => Boolean

    def getBoundingClientRect: TextRectangle
}
