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

        val g = apply(model)
        model.close()
        g
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

    /** Merges two graphs into a new one. Equivalent to g1 + g2.
      *
      * @param g1 First graph.
      * @param g2 Second graph.
      * @return New instance with merged vertices and edges.
      */
    def merge(g1: Graph, g2: Graph): Graph = {
        GraphMerger(g1, g2)
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
        GraphHelper.collectionContainsEdgeBetweenNodes(_edges, origin, destination, edgeURI)
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

    /** Returns whether this graph contains an edge with URI.
      *
      * @param edgeURI Edge URI.
      * @return True of false.
      */
    def containsEdgeWithURI(edgeURI: String): Boolean = {
        _edges.find(_.uri == edgeURI).isDefined
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

    def containsVertex(vertex: Node): Boolean = {
        vertex match {
            case iv: IdentifiedNode => containsVertexWithURI(iv.uri)
            case lv: LiteralNode => containsLiteralVertexWithValue(lv.value, lv.language)
            case _ => false
        }
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

    /** Returns edges filtered by URI.
      *
      * @param edgeURI URI of the edge.
      * @return A new sequence of edges with edgeURI.
      */
    def edgesWithURI(edgeURI: String): collection.Seq[Edge] = {
        _edges.filter(_.uri == edgeURI)
    }

    /** Executes a construct SPARQL query on this graph and returns a new graph instance
      * that consists of only the nodes that are in the query result.
      *
      * @param queryString SPARQL query string - mustn't be null.
      * @return New instance of graph with vertices that are in the query result.
      */
    private def executeConstructSPARQLQuery(queryString: String): Graph = {
        require(queryString != null && queryString != "", "Empty or NULL SPARQL query.")

        val query = QueryFactory.create(queryString)
        val model: Model = this.getModel

        val execution: QueryExecution = QueryExecutionFactory.create(query, model)
        val g = Graph(execution.execConstruct)

        // Free up resources
        execution.close()
        model.close()

        g
    }

    /** Executes a select SPARQL query on this graph and returns a new graph instance
      * that consists of only the nodes that are in the query result.
      *
      * @param queryString SPARQL query string - mustn't be null.
      * @return New instance of graph with vertices that are in the query result.
      */
    private def executeSelectSPARQLQuery(queryString: String): Graph = {
        require(queryString != null && queryString != "", "Empty or NULL SPARQL query.")

        val query = QueryFactory.create(queryString)
        val model: Model = this.getModel

        val execution: QueryExecution = QueryExecutionFactory.create(query, model)
        val results: ResultSet = execution.execSelect

        val output: ByteArrayOutputStream = new ByteArrayOutputStream()

        ResultSetFormatter.outputAsRDF(output, "", results);
        val resultingGraphXML: String = new String(output.toByteArray)

        execution.close()
        model.close()

        Graph(resultingGraphXML)
    }

    /** Executes a given SPARQL query.
      *
      * @param queryString Query string.
      * @return A graph that corresponds to the executed query result.
      */
    def executeSPARQLQuery(queryString: String): Graph = {
        if (queryString.contains("SELECT")) {
            executeSelectSPARQLQuery(queryString)
        } else if (queryString.contains("CONSTRUCT")) {
            executeConstructSPARQLQuery(queryString)
        } else {
            // TODO ASK and possibly DESCRIBE?
            throw new IllegalArgumentException("Unknown SPARQL query type (" + queryString + ")")
        }
    }

    /** Returns a vertex with these properties or None if there is no such node.
      *
      * @param value Value of the literal vertex.
      * @param language Language of the literal vertex.
      * @return Vertex or None if there isn't one with such properties.
      */
    def getLiteralVertexWithValue(value: Any, language: Option[String] = None): Option[LiteralNode] = {
        GraphHelper.getLiteralVertexWithValueFromCollection(_vertices, value, language)
    }

    /** Creates a Jena model out of itself.
      *
      * WARNING: You are responsible for calling close() on the model after you're
      * done working with it.
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
        GraphHelper.getVertexWithURIFromCollection(_vertices, vertexURI)
    }

}
