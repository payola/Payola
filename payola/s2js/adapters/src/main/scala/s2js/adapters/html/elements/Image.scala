package s2js.adapters.html.elements

import s2js.adapters.html._

trait Image extends Element
{
    var alt: String

    var src: String

    val complete: Boolean

    var height: Double

    var width: Double
}
