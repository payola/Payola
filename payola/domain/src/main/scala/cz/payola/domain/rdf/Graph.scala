package cz.payola.domain.rdf

import com.hp.hpl.jena.rdf.model._
import java.security.MessageDigest
import collection.mutable.{ArrayBuffer, HashMap, ListBuffer}
import com.hp.hpl.jena.datatypes.RDFDatatype
import com.hp.hpl.jena.query._
import java.io._
import scala.io.Source

object Graph
{
    /** Create a new empty graph.
      *
      * @return Empty graph instance.
      */
    def empty: Graph = new Graph(Nil, Nil)

    /** Reads an RDF graph from input stream.
      *
      * @param is Input Stream.
      * @param encoding Encoding of the input stream. UTF-8 by default.
      * @return Instance of graph.
      */
    def apply(is: InputStream, encoding: String = "UTF-8"): Graph = {
        val rdfXML: String = Source.fromInputStream(is, encoding).mkString
        apply(rdfXML)
    }

    /** Creates a new Graph instance from Jena's Model object.
      *
      * @param model Model.
      * @return New graph instance.
      */
    def apply(model: Model): Graph = {
        val factory = new GraphFactory(model)
        factory.getGraph
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
        rdfStrings.foreach({s: String =>
            if (g == null) {
                g = Graph(s)
            } else {
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
    def collectionContainsEdgeBetweenNodes(c: Traversable[Edge], origin: IdentifiedNode, destination: Node,
        edgeURI: String): Boolean = {
        c.find({e: Edge =>
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
    def collectionContainsLiteralVertexWithValue(c: Traversable[Node], value: Any,
        language: Option[String] = None): Boolean = {
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
    def getLiteralVertexWithValueFromCollection(c: Traversable[Node], value: Any,
        language: Option[String] = None): Option[LiteralNode] = {
        c.find({n: Node =>
            if (n.isInstanceOf[LiteralNode]) {
                val litNode = n.asInstanceOf[LiteralNode]
                if (litNode.value == value &&
                    ((language == None && litNode.language == None) || (language.get == litNode.language.get))) {
                    true
                } else {
                    false
                }
            } else {
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
        c.find({n: Node =>
            if (n.isInstanceOf[IdentifiedNode] &&
                n.asInstanceOf[IdentifiedNode].uri == vertexURI) {
                true
            } else {
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
        g2.vertices foreach ({n: Node =>
            mergeNodeToNodesAndEdges(n, vs, es)
        })

        g2.edges foreach ({e: Edge =>
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
        } else {
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
                if (!collectionContainsVertexWithURI(vs, identifiedNode.uri)) {
                    // This vertex is not in this graph, let's add it
                    vs += n
                }
            }
            case literalNode: LiteralNode => {
                if (!collectionContainsLiteralVertexWithValue(vs, literalNode.value, literalNode.language)) {
                    vs += n
                }
            }
            case _ => throw new IllegalArgumentException("Unknown RDF graph node class - " + n)
        }
    }

}

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

    /** Creates a new graph with contents of this graph and otherGraph.
      *
      * @param otherGraph The graph to be merged.
      * @return A new Graph instance.
      */
    def +(otherGraph: Graph): Graph = {
        // Create an empty graph and merge this and otherGraph into it
        Graph.merge(this, otherGraph)
    }

    /** Adds an equivalent of the Edge e to the model.
      *
      * @param e Edge.
      * @param hashMap HashMap of the Resource objects.
      * @param model Model to be added to.
      */
    private def addEdgeToModel(e: Edge, hashMap: HashMap[String, Resource], model: Model) {
        val prop: Property = ResourceFactory.createProperty(e.uri)
        val res: Resource = getResourceInModelForIdentifiedNode(e.origin, hashMap, model)

        val statement: Statement = createStatementForEdge(res, prop, e.destination, hashMap, model)
        model.add(statement)
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

    /** Returns whether this graph contains an edge whose destination is the literalNode.
      *
      * @param literalNode Literal node.
      * @return True of false.
      */
    def containsEdgeWithLiteralNode(literalNode: LiteralNode): Boolean = {
        // The literal node must always be destination of the edge
        _edges.find({ e: Edge => e.destination == literalNode }).isDefined
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

    /** Creates a statement - origin - property - destination. Origin and property
      * are already the Jena objects, while destination is a Node.
      *
      * @param origin Origin.
      * @param property Property.
      * @param destination Destination.
      * @param hashMap HashMap of Resource objects.
      * @param model Model.
      * @return Statement for this edge.
      */
    private def createStatementForEdge(origin: Resource, property: Property, destination: Node, hashMap: HashMap[String, Resource], model: Model): Statement = {
        destination match {
            case identifiedDestination: IdentifiedNode => {
                val destinationResource: Resource = getResourceInModelForIdentifiedNode(identifiedDestination, hashMap, model)
                origin.addProperty(property, destinationResource)
                model.createStatement(origin, property, destinationResource)
            }
            case litDestination: LiteralNode => {
                model.createStatement(origin, property, litDestination.value.toString, litDestination.language.getOrElse(""))
            }
            case _ => throw new IllegalArgumentException("Unknown node type " + destination.getClass)
        }
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

    /** Creates a Jena model out of itself.
      *
      * @return Model representing this graph.
      */
    def getModel: Model = {
        val model: Model = ModelFactory.createDefaultModel()

        // A hash map URI -> Resource
        val hashMap = new HashMap[String, Resource]()

        // Add all identified vertices right now.
        _vertices foreach { n: Node =>
            if (n.isInstanceOf[IdentifiedNode]) {
                getResourceInModelForIdentifiedNode(n.asInstanceOf[IdentifiedNode], hashMap, model)
            }
        }

        // Now add all edges
        _edges foreach { e: Edge => addEdgeToModel(e, hashMap, model) }

        // TODO - this is an impossible case - investigate - Jena doesn't allow orphan literals.
        /*
        // Now handle orphans - we have handled identified nodes already, that's fine.
        // However, if there's a literal-node orphan, it has been omitted for sure.
        // Hence go through all the vertices and look for literal nodes that do not
        // appear in any edge.
        _vertices foreach { n: Node =>
            if (n.isInstanceOf[LiteralNode]){
                val literalNode: LiteralNode = n.asInstanceOf[LiteralNode]
                if (!containsEdgeWithLiteralNode(literalNode)) {
                    // No edge contains this poor thing. Add it
                    val literal: Literal = model.createLiteral(literalNode.value.toString, literalNode.language.getOrElse(""))
                    model.add(literal)
                }
            }
        }
        */

        model
    }

    /** Returns a Jena Resource object for the node. If there's no such object already saved
      * in the hashMap, new Resource is created in the Model m.
      *
      * @param node Identified node.
      * @param hashMap HashMap of created resources.
      * @param m Model.
      * @return Resource.
      */
    private def getResourceInModelForIdentifiedNode(node: IdentifiedNode, hashMap: HashMap[String, Resource], m: Model): Resource = {
        getResourceInModelForURI(node.uri, hashMap, m)
    }

    /** Returns a Jena Resource object for uri. If there's no such object already saved
      * in the hashMap, new Resource is created in the Model m.
      *
      * @param uri URI of the Resource.
      * @param hashMap HashMap of created resources.
      * @param model Model.
      * @return Resource.
      */
    private def getResourceInModelForURI(uri: String, hashMap: HashMap[String, Resource], model: Model): Resource = {
        hashMap.get(uri).getOrElse({
            val r = model.createResource(uri)
            hashMap.put(uri, r)
            r
        })
    }

    /** Returns a vertex with these properties or None if there is no such node.
      *
      * @param vertexURI URI of the vertex.
      * @return Vertex or None if there isn't one with such properties.
      */
    def getVertexWithURI(vertexURI: String): Option[IdentifiedNode] = {
        Graph.getVertexWithURIFromCollection(_vertices, vertexURI)
    }

}
