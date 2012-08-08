package s2js.adapters.dom

trait Element extends Node
{
    val tagName: String

    val schemaTypeInfo: TypeInfo

    def getAttribute(name: String): String

    def setAttribute(name: String, value: String)

    def removeAttribute(name: String)

    def getAttributeNode(name: String): Attr

    def setAttributeNode(newAttr: Attr): Attr

    def removeAttributeNode(attribute: Attr): Attr

    def getElementsByTagName(name: String): NodeList[Element]

    def getAttributeNS(ns: String, name: String): String

    def setAttributeNS(ns: String, name: String, value: String)

    def removeAttributeNS(ns: String, name: String)

    def getAttributeNodeNS(ns: String, name: String): Attr

    def setAttributeNodeNS(newAttr: Attr): Attr

    def getElementsByTagNameNS(ns: String, name: String): NodeList[Element]

    def hasAttribute(name: String): Boolean

    def hasAttributeNS(ns: String, name: String): Boolean

    def setIdAttribute(name: String, isId: Boolean)

    def setIdAttributeNS(ns: String, name: String, isId: Boolean)

    def setIdAttributeNode(idAttr: Attr, isId: Boolean): Attr
}
