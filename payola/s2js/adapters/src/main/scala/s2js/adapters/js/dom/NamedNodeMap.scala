package s2js.adapters.js.dom

abstract class NamedNodeMap[A <: Node]
{
    val length: Int

    def getNamedItem(name: String): A

    def getNamedItemNS(ns: String, name: String): A

    def item(index: Int): A

    def removeNamedItem(name: String): A

    def removeNamedItemNS(ns: String, name: String): A

    def setNamedItem(node: A): A

    def setNamedItemNS(node: A): A
}
