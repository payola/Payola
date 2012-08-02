package s2js.adapters.js.dom

abstract class Attr extends Node
{
    val isId: Boolean

    val name: String

    val ownerElement: Element

    val specified: Boolean

    var value: Any
}
