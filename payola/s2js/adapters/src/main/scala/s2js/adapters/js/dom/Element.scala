package s2js.adapters.js.dom

/**
  * A DOM element as it's described on http://www.w3schools.com/dom/dom_element.asp
  */
abstract class Element extends Node
{
    var offsetTop: Double

    var offsetLeft: Double

    val attributes: NamedNodeMap

    val tagName: String

    val id: String

    var innerHTML: String

    var className: String

    def getAttribute(name: String): String

    def getAttributeNS(ns: String, name: String): String

    def getAttributeNode(name: String): Attribute

    def getAttributeNodeNS(ns: String, name: String): Attribute

    def getElementsByTagName(name: String): NodeList

    def getElementsByTagNameNS(ns: String, name: String): NodeList

    def hasAttribute(name: String): Boolean

    def hasAttributeNS(ns: String, name: String): Boolean

    def removeAttribute(name: String)

    def removeAttributeNS(ns: String, name: String)

    def removeAttributeNode(attribute: Attribute)

    def setAttribute(name: String, value: String)

    def setAttributeNS(ns: String, name: String, value: String)

    val scrollLeft: Int

    val scrollTop: Int
}
