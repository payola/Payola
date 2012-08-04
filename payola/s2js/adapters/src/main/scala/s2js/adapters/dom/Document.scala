package s2js.adapters.dom

trait Document extends Node
{
    type NodeType <: Node

    type ElementType <: Element

    val doctype: DocumentType

    val implementation: DOMImplementation

    val documentElement: ElementType

    val inputEncoding: String

    val xmlEncoding: String

    var xmlStandalone: Boolean

    var xmlVersion: String

    var strictErrorChecking: Boolean

    var documentURI: String

    val domConfig: DOMConfiguration

    def createElement[A <: ElementType](name: String): A

    def createDocumentFragment(): DocumentFragment

    def createTextNode(data: String): Text

    def createComment(text: String): Comment

    def createCDATASection(text: String): CDATASection

    def createProcessingInstruction(target: String, data: String): ProcessingInstruction

    def createAttribute(name: String): Attr

    def createEntityReference(name: String): EntityReference

    def getElementsByTagName(name: String): NodeList[ElementType]

    def importNode(importedNode: Node, deep: Boolean): Node

    def createElementNS[A <: ElementType](ns: String, name: String): A

    def createAttributeNS(ns: String, name: String): Attr

    def getElementsByTagNameNS(ns: String, name: String): NodeList[ElementType]

    def getElementById(id: String): ElementType

    def adoptNode(node: Node): Node

    def normalizeDocument()

    def renameNode(node: Node, namespaceURI: String, qualifiedName: String): Node
}
