package s2js.adapters.dom

trait NodeList[A <: Node]
{
    val length: Int

    def item(index: Int): A
}
