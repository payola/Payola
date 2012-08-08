package s2js.adapters.dom

trait DOMImplementationList
{
    val length: Int

    def item(index: Int): DOMImplementation
}
