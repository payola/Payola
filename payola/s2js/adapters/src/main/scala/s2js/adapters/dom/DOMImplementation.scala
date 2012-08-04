package s2js.adapters.dom

trait DOMImplementation
{
    def hasFeature(feature: String, version: String): Boolean

    def createDocumentType(qualifiedName: String, publicId: String, systemId: String): DocumentType

    def createDocument(namespaceURI: String, qualifiedName: String, doctype: String): Document

    def getFeature(feature: String, version: String): DOMObject
}
