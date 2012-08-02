package s2js.adapters.js.html

abstract class Element extends s2js.adapters.js.dom.Element
{
    var accessKey: String

    var className: String

    val clientHeight: Double

    val clientWidth: Double

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

    var onclick: MouseKeyboardEvent => Boolean

    var ondblclick: MouseKeyboardEvent => Boolean

    var onmousedown: MouseKeyboardEvent => Boolean

    var onmousemove: MouseKeyboardEvent => Boolean

    var onmouseover: MouseKeyboardEvent => Boolean

    var onmouseout: MouseKeyboardEvent => Boolean

    var onmouseup: MouseKeyboardEvent => Boolean

    var onmousewheel: MouseKeyboardEvent => Boolean

    var onkeydown: MouseKeyboardEvent => Boolean

    var onkeypress: MouseKeyboardEvent => Boolean

    var onkeyup: MouseKeyboardEvent => Boolean
}
