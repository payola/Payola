package s2js.adapters.js.dom

abstract class NamedNodeMap extends NodeList
{
    def getNamedItem(nodeName: String): Node

    def removeNamedItem(nodeName: String): Node
}
