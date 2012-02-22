package cz.payola.data.model.graph

import scala.collection.mutable.{HashMap, ArrayBuffer}
import java.io.StringReader
import java.security.MessageDigest
import com.hp.hpl.jena.rdf.model.{Resource, ResIterator, ModelFactory}

import cz.payola.scala2json.annotations._

object RDFGraph {

    // Minimal length of the hash used for space-saving during serialization
    private val kRDFGraphMinimalNamespaceHashLength = 1

    /** Takes a XML or TTL string representing an RDF graph and returns an instance
      * of RDFGraph representing that particular graph.
      *
      * @param rdfString XML or TTL representation of the graph.
      * @return The graph.
      */
    def apply(rdfString: String): RDFGraph = {
        val reader = new StringReader(rdfString)

        // Guess the input language. By default, Jena assumes it's
        // RDF/XML - would fail if the input was Turtle. So, we use
        // (at the moment) very primitive heuristic that XML files
        // begin (or they should) with a '<' char.
        var inputType = "RDF/XML"
        if (!rdfString.startsWith("<")){
            inputType = "TURTLE"
        }

        // Create a model and read it from the input string
        val model = ModelFactory.createDefaultModel
        model.read(reader, null, inputType)

        // List the graph nodes and build the graph
        val graph = new RDFGraph()
        val resIterator: ResIterator = model.listSubjects
        while (resIterator.hasNext) {
            val res: Resource = resIterator.nextResource
            graph.addNode(RDFNode(graph, res))
        }

        // Returning the graph
        graph
    }
}

import RDFGraph._

class RDFGraph {

    // Contains an array of RDFNode objects representing nodes of the graph.
    private val nodes: ArrayBuffer[RDFNode] = new ArrayBuffer[RDFNode]()

    /** _namespaces is a hash map that contains pairs [namespace, MD5-hash].
      * This field is transient because we need inverted hash map. For the ease of adding
      * namespaces, two hash maps are kept - _namespaces and _invertedNamespaces.
      */
    @JSONTransient private val _namespaces: HashMap[String, String] = new HashMap[String, String]()


    /** _invertedNamespaces is a hash map that contains pairs [MD5-hash, namespace].
      * This field is serialized as 'namespaces'.
      */
    @JSONFieldName(name = "namespaces") private val _invertedNamespaces: HashMap[String, String] = new HashMap[String, String]()

    /** Computes a MD5 hash of a string.
      * 
      * @param str The string to be hashed.
      * @return The MD5 hash.
      */
    private def _md5String(str: String) : String = {
        val bytes = str.getBytes
        val md5 = MessageDigest.getInstance("MD5")
        md5.reset()
        md5.update(bytes)
        md5.digest().map(0xFF & _).map { "%02x".format(_) }.foldLeft(""){_ + _}
    }

    /** Adds a node to the nodes ArrayBuffer.
      *
      * @param node The node to be added.
      */
    def addNode(node: RDFNode) = nodes += node


    /** Returns the hashed namespace. This method is used in RDFEdge classes
      * during serialization.
      *
      * This method automatically registers the namespace if it wasn't encountered
      * before. If the method processes a particular namespace for the first time,
      * an MD5 hash is computed and the shortest unique prefix is used.
      *
      * @param ns The namespace.
      * @return MD5-hash (or sub-hash) of the namespace.
      */
    def shortenedNamespace(ns: String) = {
        val short: Option[String] = _namespaces.get(ns)
        if (short.isEmpty){
            // Never seen this namespace before
            // Compute MD5 and use the shortest unique prefix
            val md5 = _md5String(ns)
            var actualHash = md5.substring(0, kRDFGraphMinimalNamespaceHashLength)
            var len = kRDFGraphMinimalNamespaceHashLength

            while (_invertedNamespaces.contains(actualHash) && len < md5.length) {
                len += 1
                actualHash = md5.substring(0, len)
            }
            
            _namespaces.put(ns, actualHash)
            _invertedNamespaces.put(actualHash, ns)
            actualHash
        }else{
            short.get
        }
    }

}
