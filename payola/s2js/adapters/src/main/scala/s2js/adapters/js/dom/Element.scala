package s2js.adapters.js.dom

abstract class Element extends Node
{
    val tagName: String

    def getAttribute(name: String): String

    def getAttributeNS(ns: String, name: String): String

    def getAttributeNode(name: String): Attr

    def getAttributeNodeNS(ns: String, name: String): Attr

    def getElementsByTagName(name: String): NodeList[Element]

    def getElementsByTagNameNS(ns: String, name: String): NodeList[Element]

    def hasAttribute(name: String): Boolean

    def hasAttributeNS(ns: String, name: String): Boolean

    def removeAttribute(name: String)

    def removeAttributeNS(ns: String, name: String)

    def removeAttributeNode(attribute: Attr)

    def setAttribute(name: String, value: String)

    def setAttributeNS(ns: String, name: String, value: String)
}
