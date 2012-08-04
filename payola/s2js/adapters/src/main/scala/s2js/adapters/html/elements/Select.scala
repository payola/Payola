package s2js.adapters.html.elements

import s2js.adapters.html.Element

abstract class Select extends Element with InputLike
{
    val length: Int

    var multiple: Boolean

    var selectedIndex: Int

    var size: Int

    /**
     * Used to add an option to a dropdown list.
     * @param option Specifies the option to add.
     * @param before Where to insert the new option (null indicates that the new option will be inserted at the end of
     *               the list)
     */
    def add(option: Option, before: Option)

    def remove(index: Int)
}
