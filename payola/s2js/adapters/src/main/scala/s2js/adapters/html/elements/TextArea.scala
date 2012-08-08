package s2js.adapters.html.elements

import s2js.adapters.html.Element

trait TextArea extends Element with TextInputLike
{
    var rows: Int

    var cols: Int

    var readOnly: Boolean
}
