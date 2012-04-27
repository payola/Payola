package cz.payola.domain.rdf

import com.hp.hpl.jena.rdf.model._
import java.io.StringReader
import java.security.MessageDigest
import collection.mutable.{ArrayBuffer, HashMap, ListBuffer}

object Graph
{
    // Minimal length of the hash used for space-saving during serialization
    private val kRDFGraphMinimalNamespaceHashLength = 1

    /** Takes a XML or TTL string representing an RDF graph and returns an instance
      * of Graph representing that particular graph.
      *
      * @param rdfString XML or TTL representation of the graph.
      * @return The graph.
      */
    def apply(rdfString: String): Graph = {
        val reader = new StringReader(rdfString)

        // Guess the input language. By default, Jena assumes it's
        // RDF/XML - would fail if the input was Turtle. So, we use
        // (at the moment) very primitive heuristic that XML files
        // begin (or they should) with a '<' char.
        var inputType = "RDF/XML"
        if (!rdfString.startsWith("<")) {
            inputType = "TURTLE"
        }

        // Create a model and read it from the input string
        val model = ModelFactory.createDefaultModel
        model.read(reader, null, inputType)

        // List the graph nodes and build the graph
        val identifiedNodes: HashMap[String, IdentifiedNode] = new HashMap[String, IdentifiedNode]()
        val allNodes: ArrayBuffer[Node] = new ArrayBuffer[Node]()
        val edges: ArrayBuffer[Edge] = new ArrayBuffer[Edge]()
        var objectIDCounter: Int = 0

        val resIterator: ResIterator = model.listSubjects
        while (resIterator.hasNext) {
            val res: Resource = resIterator.nextResource

            val URI = res.getURI
            var node: IdentifiedNode = null
            if (identifiedNodes.get(URI).isDefined) {
                node = identifiedNodes.get(URI).get
            } else {
                node = new IdentifiedNode(URI)
                node.objectID = objectIDCounter
                objectIDCounter += 1
                identifiedNodes.put(URI, node)
                allNodes += node
            }

            // Look for edges and add them
            val iterator: StmtIterator = res.listProperties
            while (iterator.hasNext) {
                val statement = iterator.nextStatement

                val predicate: Property = statement.getPredicate
                val rdfNode: com.hp.hpl.jena.rdf.model.RDFNode = statement.getObject
                val namespace = predicate.getNameSpace
                val localName = predicate.getLocalName

                // We need to distinguish two cases - the node is a literal, or a reference
                // to another node (resource)
                var edge: Edge = null
                if (rdfNode.isLiteral) {
                    var language = statement.getLanguage
                    if (language == "") {
                        language = null
                    }
                    val literalNode = new LiteralNode(rdfNode.asLiteral.getValue, Option(language))
                    literalNode.objectID = objectIDCounter
                    objectIDCounter += 1
                    allNodes += literalNode
                    edge = new Edge(node, literalNode, predicate.getURI)
                } else {
                    val asResource = rdfNode.asResource
                    val destinationURI = asResource.getURI
                    var destination: IdentifiedNode = null
                    if (identifiedNodes.get(destinationURI).isEmpty) {
                        destination = new IdentifiedNode(destinationURI)
                        destination.objectID = objectIDCounter
                        objectIDCounter += 1
                        identifiedNodes.put(destinationURI, destination)
                        allNodes += destination
                    } else {
                        destination = identifiedNodes.get(destinationURI).get
                    }
                    edge = new Edge(node, destination, predicate.getURI)
                }

                edges += edge
            }
        }

        val graph = new Graph(allNodes, edges)

        // Returning the graph
        graph
    }
}

import Graph._

class Graph(protected val _vertices: ArrayBuffer[Node], protected val _edges: ArrayBuffer[Edge])
    extends cz.payola.common.rdf.Graph
{

    def this(verts: List[Node], es: List[Edge]) = this(new ArrayBuffer[Node]() ++ verts, new ArrayBuffer[Edge]() ++ es)

    type EdgeType = Edge

    //  Contains an array of Node objects representing nodes of the graph.
    // private val nodes: ArrayBuffer[Node] = new ArrayBuffer[Node]()

    /** _namespaces is a hash map that contains pairs [namespace, MD5-hash].
      * This field is transient because we need inverted hash map. For the ease of adding
      * namespaces, two hash maps are kept - _namespaces and _invertedNamespaces.
      */
    private val _namespaces: HashMap[String, String] = new HashMap[String, String]()

    /** _invertedNamespaces is a hash map that contains pairs [MD5-hash, namespace].
      * This field is serialized as 'namespaces'.
      */
    private val _invertedNamespaces: HashMap[String, String] = new HashMap[String, String]()

    /** Computes a MD5 hash of a string.
      *
      * @param str The string to be hashed.
      * @return The MD5 hash.
      */
    private def _md5String(str: String): String = {
        val bytes = str.getBytes
        val md5 = MessageDigest.getInstance("MD5")
        md5.reset()
        md5.update(bytes)
        md5.digest().map(0xFF & _).map {
            "%02x".format(_)
        }.foldLeft("") {
            _ + _
        }
    }

    private def _populateNamespaces = {
        _vertices.foreach {vertex: Node =>
            if (vertex.isInstanceOf[IdentifiedNode]) {
                val uri = vertex.asInstanceOf[IdentifiedNode].uri
                if (uri != null) {
                    shortenedNamespace(uri)
                }
            }
        }

        _edges.foreach {edge: Edge =>
            if (edge.uri != null) {
                shortenedNamespace(edge.uri)
            }
        }
    }

    /** Creates a new graph with contents of this graph and otherGraph.
      *
      * @param otherGraph The graph to be merged.
      * @return A new Graph instance.
      */
    def +(otherGraph: Graph): Graph = {
        // Create an empty graph and merge this and otherGraph into it
        val g: Graph = new Graph(new ArrayBuffer[Node](), new ArrayBuffer[Edge]())
        g += this
        g += otherGraph
        g
    }


    /** This method merges the otherGraph into this one.
      *
      * @param otherGraph Graph to be merged into this one.
      */
    def +=(otherGraph: Graph) {
        // First, merge in the vertices, then edges:

        otherGraph.vertices foreach { n: Node =>
            mergeInNode(n)
        }

        otherGraph.edges foreach { e: Edge =>
            mergeInEdge(e)
        }

    }

    /** Returns whether this graph contains an edge that goes between the two
      * nodes.
      *
      * @param origin Origin of the edge.
      * @param destination Destination of the edge.
      * @param edgeURI URI of the edge.
      * @return True if there is such an edge.
      */
    def containsEdgeBetweenNodes(origin: IdentifiedNode, destination: Node, edgeURI: String): Boolean = {
        _edges.find({ e: Edge =>
            e.origin == origin && e.destination == destination && e.uri == edgeURI
        }).isDefined
    }

    /** Returns whether this graph contains a vertex with these properties.
      *
      * @param value Value of the literal vertex.
      * @param language Language of the literal vertex.
      * @return True if there is such a vertex.
      */
    def containsLiteralVertexWithValue(value: Any, language: Option[String] = None): Boolean = {
        getLiteralVertexWithValue(value, language).isDefined
    }

    /** Returns whether this graph contains a vertex with these properties.
      *
      * @param vertexURI URI of the vertex.
      * @return True if there is such a vertex.
      */
    def containsVertexWithURI(vertexURI: String): Boolean = {
        getVertexWithURI(vertexURI).isDefined
    }

    /** Returns a vertex with these properties or None if there is no such node.
      *
      * @param value Value of the literal vertex.
      * @param language Language of the literal vertex.
      * @return Vertex or None if there isn't one with such properties.
      */
    def getLiteralVertexWithValue(value: Any, language: Option[String] = None): Option[LiteralNode] = {
        _vertices.find({ n: Node =>
            if (n.isInstanceOf[LiteralNode]){
                val litNode = n.asInstanceOf[LiteralNode]
                if (litNode.value == value &&
                    ((language == None && litNode.language == None) || (language.get == litNode.language.get))){
                    true
                }else{
                    false
                }
            }else{
                false
            }
        }).asInstanceOf[Option[LiteralNode]]
    }

    /** Returns a vertex with these properties or None if there is no such node.
      *
      * @param vertexURI URI of the vertex.
      * @return Vertex or None if there isn't one with such properties.
      */
    def getVertexWithURI(vertexURI: String): Option[IdentifiedNode] = {
        _vertices.find({ n: Node =>
            if (n.isInstanceOf[IdentifiedNode] &&
                n.asInstanceOf[IdentifiedNode].uri == vertexURI){
                true
            }else{
                false
            }
        }).asInstanceOf[Option[IdentifiedNode]]
    }

    /** Merges an edge from another graph into this one. Both vertices must be
      * included in this graph, or have an equivalent (i.e. the same URI or values).
      *
      * @param e Edge to be merged in.
      */
    private def mergeInEdge(e: Edge) {
        val origin: Option[IdentifiedNode] = getVertexWithURI(e.origin.uri)
        val destination: Option[Node] = if (e.destination.isInstanceOf[IdentifiedNode]) {
            getVertexWithURI(e.destination.asInstanceOf[IdentifiedNode].uri)
        }else{
            val d = e.destination.asInstanceOf[LiteralNode]
            getLiteralVertexWithValue(d.value, d.language)
        }

        assert(origin.isDefined && destination.isDefined, "Trying to merge an edge that " +
            "doesn't have both vertices in this graph (" + origin + " -> " + destination + ")")

        if (!containsEdgeBetweenNodes(origin.get, destination.get, e.uri)) {
            _edges += new Edge(origin.get, destination.get, e.uri)
        }
    }

    /** Merges in a node from another graph. If an equivalent node is already
      * among vertices of this graph, this method does nothing.
      *
      * @param n Node to be merged in.
      */
    private def mergeInNode(n: Node) {
        n match {
            case identifiedNode: IdentifiedNode => {
                if (!this.containsVertexWithURI(identifiedNode.uri)){
                    // This vertex is not in this graph, let's add it
                    _vertices += n
                }
            }
            case literalNode: LiteralNode => {
                if (!this.containsLiteralVertexWithValue(literalNode.value, literalNode.language)){
                    _vertices += n
                }
            }
            case _ => throw new IllegalArgumentException("Unknown RDF graph node class - " + n)
        }
    }




    /** Returns the hashed namespace. This method is used in Edge classes
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
        if (ns == null) {
            ""
        } else {
            val short: Option[String] = _namespaces.get(ns)
            if (short.isEmpty) {
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
            } else {
                short.get
            }
        }
    }
}
