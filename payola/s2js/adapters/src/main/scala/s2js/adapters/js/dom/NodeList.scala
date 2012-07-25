package s2js.adapters.js.dom

abstract class NodeList[A <: Node]
{
    val length: Int

    def item(index: Int): A
}
