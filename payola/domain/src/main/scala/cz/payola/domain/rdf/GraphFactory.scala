package cz.payola.domain.rdf

import scala.collection.mutable.{ListBuffer, HashMap}
import com.hp.hpl.jena.rdf.model._

/** A private class within the RDF package which creates a new Graph instance from
  * Jena's Model.
  *
  * @param model Jena's Model
  */
private[rdf] class GraphFactory(val model: Model)
{

    // List the graph nodes and build the graph
    private val identifiedNodes: HashMap[String, IdentifiedNode] = new HashMap[String, IdentifiedNode]()
    private val allNodes: ListBuffer[Node] = new ListBuffer[Node]()
    private val edges: ListBuffer[Edge] = new ListBuffer[Edge]()
    private var objectIDCounter: Int = 0
    private var graph: Graph = null

    /** Actually creates a new Graph instance from Model.
      */
    private def createGraph {
        val resIterator: ResIterator = model.listSubjects
        while (resIterator.hasNext) {
            val res: Resource = resIterator.nextResource
            processResource(res)
        }

        graph = new Graph(allNodes, edges)
    }

    /** Creates a new LiteralNode object from Literal and Statement and adds it to the node list.
      *
      * @param rdfNode RDF Node.
      * @param statement Statement.
      * @return New literal node.
      */
    private def createLiteralNodeForRDFNode(rdfNode: Literal, statement: Statement): LiteralNode = {
        var language = statement.getLanguage
        if (language == "") {
            language = null
        }

        val literalNode = new LiteralNode(rdfNode.getValue, Option(language))
        literalNode.objectID = objectIDCounter
        objectIDCounter += 1
        allNodes += literalNode

        literalNode
    }

    /** Returns an instance of Graph. Can be called multiple times, however,
      * same instance will be returned each time.
      *
      * @return Graph instance generated from the Model.
      */
    def getGraph: Graph = {
        if (graph == null) {
            createGraph
        }

        graph
    }

    /** Gets an identified node for Resource. If such a node doesn't exist yet,
      * one is created.
      *
      * @param rdfNode Resource.
      * @return Identified node corresponding to the Resource's URI.
      */
    private def getIdentifiedNode(rdfNode: Resource): IdentifiedNode = {
        val destinationURI = rdfNode.getURI
        getIdentifiedNode(destinationURI)
    }

    /** Gets an identified node for URI. If such a node doesn't exist yet,
      * one is created.
      *
      * @param uri Node URI.
      * @return Identified node corresponding to the URI.
      */
    private def getIdentifiedNode(uri: String): IdentifiedNode = {
        identifiedNodes.get(uri).getOrElse( {
            val destination = new IdentifiedNode(uri)
            destination.objectID = objectIDCounter
            objectIDCounter += 1
            identifiedNodes.put(uri, destination)
            allNodes += destination

            destination
        } )
    }

    /** Processes a Resource object. Goes through all the edges and adds
      * nodes.
      *
      * @param res Resource.
      */
    private def processResource(res: Resource) {
        val node: IdentifiedNode = getIdentifiedNode(res)

        // Look for edges and add them
        val iterator: StmtIterator = res.listProperties
        while (iterator.hasNext) {
            val statement: Statement = iterator.nextStatement
            processStatement(statement, node)
        }
    }

    /** Processes a Statement object. Generally adds an edge to the graph.
      *
      * @param statement Statement.
      * @param origin Origin of the edge.
      */
    private def processStatement(statement: Statement, origin: IdentifiedNode) {
        val predicate: Property = statement.getPredicate
        val rdfNode: RDFNode = statement.getObject

        // We need to distinguish two cases - the node is a literal, or a reference
        // to another node (resource)
        val destination: Node = if (rdfNode.isLiteral) {
            createLiteralNodeForRDFNode(rdfNode.asLiteral, statement)
        } else {
            getIdentifiedNode(rdfNode.asResource)
        }

        val edge: Edge = new Edge(origin, destination, predicate.getURI)
        edges += edge
    }

}
