package s2js.adapters.html.elements

import s2js.adapters.html._

abstract class Image extends Element
{
    var alt: String

    var src: String

    val complete: Boolean

    var height: Double

    var width: Double
}
