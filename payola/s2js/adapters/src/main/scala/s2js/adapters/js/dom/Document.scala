package s2js.adapters.js.dom

abstract class Document
{
    type NodeType <: Node

    type ElementType <: Element

    val doctype: String

    val documentElement: ElementType

    var documentURI: String

    val inputEncoding: String

    val xmlEncoding: String

    val xmlStandalone: Boolean

    val xmlVersion: String

    def createAttribute(name: String): Attr

    def createAttributeNS(ns: String, name: String): Attr

    def createCDATASection(text: String): CDATASection

    def createComment(text: String): Comment

    def createDocumentFragment(): DocumentFragment

    def createElement[A <: ElementType](name: String): A

    def createElementNS[A <: ElementType](ns: String, name: String): A

    def createEntityReference(name: String): EntityReference

    def createProcessingInstruction(target: String, data: String): ProcessingInstruction

    def createTextNode(data: String): Text

    def getElementById(id: String): ElementType

    def getElementsByTagName(name: String): NodeList[ElementType]

    def getElementsByTagNameNS(ns: String, name: String): NodeList[ElementType]

    def getElementsByClassName(cssClass: String): NodeList[ElementType]

    def execCommand(command: String, showDefaultUI: Boolean, value: String) {}
}
