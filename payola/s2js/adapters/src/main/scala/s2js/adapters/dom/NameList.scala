package s2js.adapters.dom

trait NameList
{
    val length: Int

    def getName(index: Int): String

    def getNamespaceURI(index: Int): String

    def contains(str: String): Boolean

    def containsNS(namespaceURI: String, name: String): Boolean
}
