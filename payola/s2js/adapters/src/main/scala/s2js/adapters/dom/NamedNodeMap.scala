package s2js.adapters.dom

trait NamedNodeMap[A <: Node]
{
    val length: Int

    def getNamedItem(name: String): A

    def setNamedItem(node: A): A

    def removeNamedItem(name: String): A

    def item(index: Int): A

    def getNamedItemNS(ns: String, name: String): A

    def setNamedItemNS(node: A): A

    def removeNamedItemNS(ns: String, name: String): A
}
