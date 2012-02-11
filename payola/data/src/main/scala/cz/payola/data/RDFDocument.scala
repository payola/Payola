package cz.payola.rdf2scala

import cz.payola.scala2json._
import cz.payola.scala2json.traits.JSONSerializationFullyCustomized

import collection.mutable.ArrayBuffer
import java.io.StringReader
import com.hp.hpl.jena.rdf.model.{ModelFactory, ResIterator, Resource}

object RDFDocument {
    def apply(rdfString: String): RDFDocument = {
        val reader = new StringReader(rdfString)

        // Create a model and read it from the input string
        val model = ModelFactory.createDefaultModel
        model.read(reader, null)

        val doc = new RDFDocument()
        val resIterator: ResIterator = model.listSubjects
        while (resIterator.hasNext){
            val res: Resource = resIterator.nextResource
            doc.addNode(RDFNode(res))
        }

        doc
    }
}

class RDFDocument extends JSONSerializationFullyCustomized {
    private val _nodes: ArrayBuffer[RDFNode] = new ArrayBuffer[RDFNode]()

    /** Adds a new RDFNode to _nodes.
     *
     * @param node The node.
     */
     private def addNode(node: RDFNode) = _nodes += node

    /** Returns an immutable copy of nodes.
     *
     * @return Nodes.
     */
    def getNodes: Array[RDFNode] = _nodes.toArray

    /** @see JSONSerializationFullyCustomized
     *
     * @return JSON value.
     */
    def JSONValue(options: Int): String = {
        val serializer = new JSONSerializer(_nodes, options)
        serializer.stringValue
    }

}
