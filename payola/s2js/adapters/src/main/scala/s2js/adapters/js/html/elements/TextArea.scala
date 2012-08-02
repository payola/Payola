package s2js.adapters.js.html.elements

import s2js.adapters.js.html.Element

abstract class TextArea extends Element with TextInputLike
{
    var rows: Int

    var cols: Int

    var readOnly: Boolean
}
