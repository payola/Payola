package s2js.adapters.html.elements

import s2js.adapters.html.Element

abstract class Option extends Element with InputLike
{
    val defaultSelected: Boolean

    var index: Int

    var selected: Boolean

    var text: String
}
