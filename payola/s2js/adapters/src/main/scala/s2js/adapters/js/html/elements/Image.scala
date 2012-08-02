package s2js.adapters.js.html.elements

import s2js.adapters.js.html._

abstract class Image extends Element
{
    var alt: String

    var src: String

    val complete: Boolean

    var height: Double

    var width: Double

    var onabort: Event => Boolean

    var onerror: Event => Boolean

    var onload: Event => Boolean
}
