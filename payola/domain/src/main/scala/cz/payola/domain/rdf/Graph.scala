package cz.payola.domain.rdf

import com.hp.hpl.jena.rdf.model._
import java.security.MessageDigest
import collection.mutable.{ArrayBuffer, HashMap, ListBuffer}
import com.hp.hpl.jena.datatypes.RDFDatatype
import java.io.{ByteArrayOutputStream, StringReader}
import com.hp.hpl.jena.query._

object Graph
{
    // Minimal length of the hash used for space-saving during serialization
    private val kRDFGraphMinimalNamespaceHashLength = 1

    def apply(model: Model): Graph = {
        // List the graph nodes and build the graph
        val identifiedNodes: HashMap[String, IdentifiedNode] = new HashMap[String, IdentifiedNode]()
        val allNodes: ListBuffer[Node] = new ListBuffer[Node]()
        val edges: ListBuffer[Edge] = new ListBuffer[Edge]()
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

        apply(model)
    }

    /** Creates a new graph merged from graphs represented by RDF strings passed
      * as argument.
      *
      * @param rdfStrings XML or TTL representations of RDF graphs.
      * @return New graph instance.
      */
    def apply(rdfStrings: String*): Graph = {
        var g: Graph = null
        rdfStrings.foreach({ s: String =>
            if (g == null) {
                g = Graph(s)
            }else{
                g = g + Graph(s)
            }
        })

        g
    }

    /** Returns whether collection c contains an edge between these two nodes with
      * such an URI.
      *
      * @param c Collection.
      * @param origin Origin.
      * @param destination Destination.
      * @param edgeURI Edge URI.
      * @return True of false.
      */
    def collectionContainsEdgeBetweenNodes(c: Traversable[Edge], origin: IdentifiedNode, destination: Node, edgeURI: String): Boolean = {
        c.find({ e: Edge =>
            e.origin == origin && e.destination == destination && e.uri == edgeURI
        }).isDefined
    }

    /** Returns whether collection c contains a vertex with such properties.
      *
      * @param c Collection.
      * @param value Value.
      * @param language Language.
      * @return True or false.
      */
    def collectionContainsLiteralVertexWithValue(c: Traversable[Node], value: Any, language: Option[String] = None): Boolean = {
        getLiteralVertexWithValueFromCollection(c, value, language).isDefined
    }

    /** Returns whether collection c contains a vertex with such properties.
      *
      * @param c Collection.
      * @param vertexURI URI.
      * @return True or false.
      */
    def collectionContainsVertexWithURI(c: Traversable[Node], vertexURI: String): Boolean = {
        getVertexWithURIFromCollection(c, vertexURI).isDefined
    }

    /** Looks for a vertex with such properties in collection c.
      *
      * @param c Collection.
      * @param value Value.
      * @param language Language.
      * @return Vertex or None.
      */
    def getLiteralVertexWithValueFromCollection(c: Traversable[Node], value: Any, language: Option[String] = None): Option[LiteralNode] = {
        c.find({ n: Node =>
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

    /** Looks for a vertex with such URI in collection c.
      *
      * @param c Collection.
      * @param vertexURI URI of the vertex.
      * @return Vertex or None.
      */
    def getVertexWithURIFromCollection(c: Traversable[Node], vertexURI: String): Option[IdentifiedNode] = {
        c.find({ n: Node =>
            if (n.isInstanceOf[IdentifiedNode] &&
                n.asInstanceOf[IdentifiedNode].uri == vertexURI){
                true
            }else{
                false
            }
        }).asInstanceOf[Option[IdentifiedNode]]
    }

    /** Merges two graphs into a new one. Equivalent to g1 + g2.
      *
      * @param g1 First graph.
      * @param g2 Second graph.
      * @return New instance with merged vertices and edges.
      */
    def merge(g1: Graph, g2: Graph): Graph = {
        val vs: ListBuffer[Node] = new ListBuffer[Node]()
        val es: ListBuffer[Edge] = new ListBuffer[Edge]()

        // Copy over all vertices and edges from the first graph
        vs ++= g1.vertices
        es ++= g1.edges

        // Now begin the merge
        g2.vertices foreach({ n: Node =>
            mergeNodeToNodesAndEdges(n, vs, es)
        })

        g2.edges foreach({ e: Edge =>
            mergeEdgeToNodesAndEdges(e, vs, es)
        })

        val g = new Graph(vs, es)
        g
    }

    /** Merges in an edge. Both vertices or their equivalents must be included in
      * vs collection.
      *
      * @param e Edge to be merged in.
      * @param vs Vertices.
      * @param es Edges.
      */
    private def mergeEdgeToNodesAndEdges(e: Edge, vs: ListBuffer[Node], es: ListBuffer[Edge]) {
        val origin: Option[IdentifiedNode] = getVertexWithURIFromCollection(vs, e.origin.uri)
        val destination: Option[Node] = if (e.destination.isInstanceOf[IdentifiedNode]) {
            getVertexWithURIFromCollection(vs, e.destination.asInstanceOf[IdentifiedNode].uri)
        }else{
            val d = e.destination.asInstanceOf[LiteralNode]
            getLiteralVertexWithValueFromCollection(vs, d.value, d.language)
        }

        assert(origin.isDefined && destination.isDefined, "Trying to merge an edge that " +
            "doesn't have both vertices in this graph (" + origin + " -> " + destination + ")")

        if (!collectionContainsEdgeBetweenNodes(es, origin.get, destination.get, e.uri)) {
            es += new Edge(origin.get, destination.get, e.uri)
        }
    }

    /** Merges in a node n. If an equivalent node is already
      * among vertices of this graph, this method does nothing.
      *
      * @param n Node to be merged in.
      * @param vs Vertices.
      * @param es Edges.
      */
    private def mergeNodeToNodesAndEdges(n: Node, vs: ListBuffer[Node], es: ListBuffer[Edge]) {
        n match {
            case identifiedNode: IdentifiedNode => {
                if (!collectionContainsVertexWithURI(vs, identifiedNode.uri)){
                    // This vertex is not in this graph, let's add it
                    vs += n
                }
            }
            case literalNode: LiteralNode => {
                if (!collectionContainsLiteralVertexWithValue(vs, literalNode.value, literalNode.language)){
                    vs += n
                }
            }
            case _ => throw new IllegalArgumentException("Unknown RDF graph node class - " + n)
        }
    }

}

import Graph._

class Graph(protected val _vertices: List[Node], protected val _edges: List[Edge])
    extends cz.payola.common.rdf.Graph
{
    /** A secondary constructor which converts any other collection to list.
      *
      * @param verts Vertices.
      * @param es Edges.
      * @return New graph instance.
      */
    def this(verts: Traversable[Node], es: Traversable[Edge]) = this(verts.toList, es.toList)

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
        Graph.merge(this, otherGraph)
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
        Graph.collectionContainsEdgeBetweenNodes(_edges, origin, destination, edgeURI)
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

    /** Executes a construct SPARQL query on this graph and returns a new graph instance
      * that consists of only the nodes that are in the query result.
      *
      * @param queryString SPARQL query string - mustn't be null.
      * @return New instance of graph with vertices that are in the query result.
      */
    def executeConstructSPARQLQuery(queryString: String): Graph = {
        require(queryString != null && queryString != "", "Empty or NULL SPARQL query.")

        val query = QueryFactory.create(queryString)
        val model: Model = this.getModel

        val execution: QueryExecution = QueryExecutionFactory.create(query, model)
        Graph(execution.execConstruct)
    }

    /** Executes a select SPARQL query on this graph and returns a new graph instance
      * that consists of only the nodes that are in the query result.
      *
      * @param queryString SPARQL query string - mustn't be null.
      * @return New instance of graph with vertices that are in the query result.
      */
    def executeSelectSPARQLQuery(queryString: String): Graph = {
        require(queryString != null && queryString != "", "Empty or NULL SPARQL query.")

        val query = QueryFactory.create(queryString)
        val model: Model = this.getModel

        val execution: QueryExecution = QueryExecutionFactory.create(query, model)
        val results: ResultSet = execution.execSelect

        val output: ByteArrayOutputStream = new ByteArrayOutputStream()

        ResultSetFormatter.outputAsRDF(output, "", results);
        execution.close

        val resultingGraphXML: String = new String(output.toByteArray)
        Graph(resultingGraphXML)
    }

    /** Returns a vertex with these properties or None if there is no such node.
      *
      * @param value Value of the literal vertex.
      * @param language Language of the literal vertex.
      * @return Vertex or None if there isn't one with such properties.
      */
    def getLiteralVertexWithValue(value: Any, language: Option[String] = None): Option[LiteralNode] = {
        Graph.getLiteralVertexWithValueFromCollection(_vertices, value, language)
    }

    /** Returns a vertex with these properties or None if there is no such node.
      *
      * @param vertexURI URI of the vertex.
      * @return Vertex or None if there isn't one with such properties.
      */
    def getVertexWithURI(vertexURI: String): Option[IdentifiedNode] = {
        Graph.getVertexWithURIFromCollection(_vertices, vertexURI)
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


    def getModel: Model = {
        /*val jenaGraph: com.hp.hpl.jena.graph.Graph = com.hp.hpl.jena.graph.Graph.emptyGraph

        _edges foreach { e: Edge =>
            val origin = com.hp.hpl.jena.graph.Node.createURI(e.origin.uri)
            val edge = com.hp.hpl.jena.graph.Node.createURI(e.uri)
            val destination = e.destination match {
                case identifiedNode: IdentifiedNode => com.hp.hpl.jena.graph.Node.createURI(identifiedNode.uri)
                case literalNode: LiteralNode => com.hp.hpl.jena.graph.Node.createLiteral(literalNode.value.toString, literalNode.language.getOrElse(""), true)
                case _ => throw new IllegalArgumentException("Unknown node type " + e.destination)
            }

            jenaGraph.add(new com.hp.hpl.jena.graph.Triple(origin, edge, destination))
        }

        ModelFactory.createModelForGraph(jenaGraph)*/



        val model: Model = ModelFactory.createDefaultModel()

        // A hash map URI -> Resource
        val hashMap = new HashMap[String, Resource]()
        _vertices foreach { n: Node =>
            // Only take identified nodes, ignore literal nodes, they will be added when
            // going through edges.
            if (n.isInstanceOf[IdentifiedNode]) {
                val identifiedNode: IdentifiedNode = n.asInstanceOf[IdentifiedNode]
                val res: Resource = hashMap.get(identifiedNode.uri).getOrElse({
                    val r = model.createResource(identifiedNode.uri)
                    hashMap.put(identifiedNode.uri, r)
                    r
                })

                // Add all the edges
                _edges foreach { e: Edge =>
                    if (e.origin.uri == identifiedNode.uri) {
                        val prop = ResourceFactory.createProperty(e.uri)
                        val destination = e.destination
                        val statement: Statement = if (destination.isInstanceOf[IdentifiedNode]) {
                            // Identified node
                            val identDest = destination.asInstanceOf[IdentifiedNode]
                            val destRes = hashMap.get(identDest.uri).getOrElse({
                                val r = model.createResource(identDest.uri)
                                hashMap.put(identDest.uri, r)
                                r
                            })
                            res.addProperty(prop, destRes)
                            model.createStatement(res, prop, destRes)
                        }else{
                            // Literal node
                            val litDest = destination.asInstanceOf[LiteralNode]
                            //val litRes: Literal = model.createTypedLiteral(litDest.value)
                            //res.addProperty(prop, litRes)
                            
                            model.createStatement(res, prop, litDest.value.toString, litDest.language.getOrElse(""))
                        }
                        model.add(statement)
                    }

                }

            }
        }

        model
    }

}
