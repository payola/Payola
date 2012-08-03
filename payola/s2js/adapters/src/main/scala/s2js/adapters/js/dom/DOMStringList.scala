package s2js.adapters.js.dom

trait DOMStringList
{
    val length: Int

    def item(index: Int): String

    def contains(item: String): Boolean
}
