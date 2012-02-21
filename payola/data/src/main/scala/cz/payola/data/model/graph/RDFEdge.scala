package cz.payola.data.model.graph

import com.hp.hpl.jena.rdf.model.{Property, Statement}

import cz.payola.scala2json.annotations.JSONTransient
import cz.payola.scala2json.traits.JSONSerializationCustomFields

object RDFEdge {

    /** Creates an edge from Jena's Statement object.
      *
      * @param graph The graph into which the edge will be added.
      * @param statement Jena's Statement.
      * @return RDFEdge
      */
    def apply(graph: RDFGraph, statement: Statement): RDFEdge = {
        val predicate: Property = statement.getPredicate
        val rdfNode: com.hp.hpl.jena.rdf.model.RDFNode = statement.getObject

        // We need to distinguish two cases - the node is a literal, or a reference
        // to another node (resource)
        var edge: RDFEdge = null
        if (rdfNode.isLiteral) {
            edge = new RDFLiteralEdge(graph, predicate.getNameSpace, predicate.getLocalName, rdfNode.asLiteral.getValue)
        } else {
            edge = new RDFReferenceEdge(graph, predicate.getNameSpace, predicate.getLocalName,
                rdfNode.asResource.getURI)
        }
        edge
    }
}


abstract trait RDFEdge extends  JSONSerializationCustomFields {

    // The graph that the edge is part of
    val graph: RDFGraph

    // Each edge needs to have a namespace
    val namespace: String

    // Each edge has a local name
    val name: String

    /** @see JSONSerializationCustomFields
      *
      * @return Iterable collection for the field names.
      */
    override def fieldNamesForJSONSerialization: Iterable[String] = {
        // Note that we do not serialize the namespace in the edge.
        // It is already serialized during the process in RDFNode, which
        // aggregates all the edges by namespaces.
        List("name")
    }

    /** Return the value for the field named @key.
      *
      * @param key Value for the field called @key.
      *
      * @return The value.
      */
    override def fieldValueForKey(key: String): Any = {
        key match {
            case "name" => name
            case _ => null
        }
    }
}
