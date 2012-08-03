package s2js.adapters.js.dom

trait DOMImplementationList
{
    val length: Int

    def item(index: Int): DOMImplementation
}
