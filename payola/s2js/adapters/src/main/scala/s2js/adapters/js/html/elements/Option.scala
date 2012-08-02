package s2js.adapters.js.html.elements

import s2js.adapters.js.html.Element

abstract class Option extends Element with InputLike
{
    val defaultSelected: Boolean

    var index: Int

    var selected: Boolean

    var text: String
}
