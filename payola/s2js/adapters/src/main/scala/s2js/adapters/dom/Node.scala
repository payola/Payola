package s2js.adapters.dom

trait Node
{
    val nodeName: String

    var nodeValue: Any

    val nodeType: Int

    val parentNode: Node

    val childNodes: NodeList[Node]

    val firstChild: Node

    val lastChild: Node

    val previousSibling: Node

    val nextSibling: Node

    val attributes: NamedNodeMap[Attr]

    val ownerDocument: Document

    var namespaceURI: String

    var prefix: String

    val localName: String

    val baseURI: String

    var textContent: String

    def insertBefore(newChild: Node, refChild: Node): Node

    def replaceChild(newChild: Node, oldChild: Node): Node

    def removeChild(child: Node): Node

    def appendChild(newChild: Node)

    def hasChildNodes: Boolean

    def cloneNode(deep: Boolean): Node

    def normalize()

    def isSupported(feature: String, version: String): Boolean

    def hasAttributes: Boolean

    def compareDocumentPosition(node: Node): Int

    def isSameNode(node: Node): Boolean

    def lookupPrefix(uri: String): String

    def isDefaultNamespace(namespaceURI: String): Boolean

    def lookupNamespaceURI(prefix: String): String

    def isEqualNode(node: Node): Boolean

    def getFeature(feature: String, version: String): DOMObject

    def setUserData(key: String, data: DOMUserData, handler: UserDataHandler): DOMUserData

    def getUserData(key: String): DOMUserData
}

object Node
{
    val ELEMENT_NODE = 1

    val ATTRIBUTE_NODE = 2

    val TEXT_NODE = 3

    val CDATA_SECTION_NODE = 4

    val ENTITY_REFERENCE_NODE = 5

    val ENTITY_NODE = 6

    val PROCESSING_INSTRUCTION_NODE = 7

    val COMMENT_NODE = 8

    val DOCUMENT_NODE = 9

    val DOCUMENT_TYPE_NODE = 10

    val DOCUMENT_FRAGMENT_NODE = 11

    val NOTATION_NODE = 12

    val DOCUMENT_POSITION_DISCONNECTED = 0x01

    val DOCUMENT_POSITION_PRECEDING = 0x02

    val DOCUMENT_POSITION_FOLLOWING = 0x04

    val DOCUMENT_POSITION_CONTAINS = 0x08

    val DOCUMENT_POSITION_CONTAINED_BY = 0x10

    val DOCUMENT_POSITION_IMPLEMENTATION_SPECIFIC = 0x20
}
