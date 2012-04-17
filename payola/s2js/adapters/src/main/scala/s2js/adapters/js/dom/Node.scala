package s2js.adapters.js.dom

/**
  * A DOM node as it's described on http://www.w3schools.com/dom/dom_node.asp
  */
abstract class Node
{
    val baseURI: String

    val childNodes: NodeList

    val firstChild: Node

    val lastChild: Node

    val localName: String

    var namespaceURI: String

    val nextSibling: Node

    val nodeName: String

    val nodeType: Int

    var nodeValue: Any

    val ownerDocument: Document

    val parentNode: Node

    var prefix: String

    val previousSibling: Node

    var textContent: String

    var text: String

    var xml: String

    def appendChild(newChild: Node)

    def cloneNode(includeAll: Boolean): Node

    def compareDocumentPosition(node: Node): Int

    def hasAttributes: Boolean

    def hasChildNodes: Boolean

    def insertBefore(newChild: Node,refChild: Node): Node

    def isEqualNode(node: Node): Boolean

    def isSameNode(node: Node): Boolean

    def lookupNamespaceURI(prefix: String): String

    def lookupPrefix(uri: String): String

    def removeChild(child: Node): Node

    def replaceChild(newChild: Node, oldChild: Node): Node
}
