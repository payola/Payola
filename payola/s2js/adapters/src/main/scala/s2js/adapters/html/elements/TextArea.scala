package s2js.adapters.html.elements

import s2js.adapters.html.Element

abstract class TextArea extends Element with TextInputLike
{
    var rows: Int

    var cols: Int

    var readOnly: Boolean
}
