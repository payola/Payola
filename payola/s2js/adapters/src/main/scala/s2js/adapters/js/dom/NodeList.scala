package s2js.adapters.js.dom

trait NodeList[A <: Node]
{
    val length: Int

    def item(index: Int): A
}
