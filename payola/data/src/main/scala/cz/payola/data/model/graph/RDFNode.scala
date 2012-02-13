package cz.payola.data.model.graph

import scala.collection.mutable._
import com.hp.hpl.jena.rdf.model.{StmtIterator, Resource}

import cz.payola.scala2json.annotations._
import cz.payola.scala2json.traits.JSONSerializationCustomFields

object RDFNode {

    /** Creates a new RDFNode from Jena's Resource.
      *
      * @param graph Graph into which the node will be inserted.
      * @param resource Jena's Resource representing the node.
      * @return RDFNode.
      */
    def apply(graph: RDFGraph, resource: Resource): RDFNode = {
        // Create a new node and set the graph
        val node = new RDFNode(graph, resource.getURI)

        // Look for edges and add them
        val iterator: StmtIterator = resource.listProperties
        while (iterator.hasNext) {
            node.addEdge(RDFEdge(graph, iterator.nextStatement()))
        }

        // Return the node
        node
    }
}

@JSONUnnamedClass class RDFNode(val graph: RDFGraph, val URI: String) extends JSONSerializationCustomFields {

    // Edges from the node.
    private val edges: ArrayBuffer[RDFEdge] = new ArrayBuffer[RDFEdge]()

    /** Aggregates the edges by their namespaces.
      * 
      * @return An aggregated hash map.
      */
    private def _aggregatedEdges: HashMap[String, ArrayBuffer[RDFEdge]] = {
        val hashMap = new HashMap[String,  ArrayBuffer[RDFEdge]]()
        
        edges foreach { edge => {
            var edgeList: ArrayBuffer[RDFEdge] = null
            val shortNS = graph.shortenedNamespace(edge.namespace)
            if (hashMap.contains(shortNS)) {
                edgeList = hashMap.get(shortNS).get
            } else {
                edgeList = new ArrayBuffer[RDFEdge]()
                hashMap.put(shortNS, edgeList)
            }
            
            edgeList += edge
        }}

        hashMap
    }

    /** Adds edge to edges.
      *
      * @param edge The edge.
      */
    private def addEdge(edge: RDFEdge) = edges += edge

    /** @see JSONSerializationCustomFields
      *
      * @return Iterable collection for the field names.
      */
    override def fieldNamesForJSONSerialization = List("URI", "edges")

    /** Return the value for the field named @key.
      *
      * @param key Value for the field called @key.
      *
      * @return The value.
      */
    def fieldValueForKey(key: String): Any = {
        key match {
            case "URI" => URI
            case "edges" => _aggregatedEdges
            case _ => null
        }
    }
}
