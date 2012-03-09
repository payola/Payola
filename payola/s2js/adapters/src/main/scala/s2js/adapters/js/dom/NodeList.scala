package s2js.adapters.js.dom

abstract class NodeList
{
    val length: Int

    def item(index: Int): Node
}
