package cz.payola.domain.rdf

import scala.collection._
import com.hp.hpl.jena.query._
import com.hp.hpl.jena.rdf.model._
import cz.payola.common.rdf._
import cz.payola.domain.DomainException

object Graph
{
    /**
      * Returns a new empty graph.
      */
    def empty: Graph = new Graph(Nil, Nil)

    /**
      * Takes a string representing a RDF data and returns an instance of Graph representing that particular graph.
      * @param representation Type of the RDF data representation.
      * @param data The RDF data of the graph.
      * @return A new graph instance.
      */
    def apply(representation: RdfRepresentation.Type, data: String): Graph = {
        val model = ModelFactory.createDefaultModel
        val language = representation match {
            case RdfRepresentation.RdfXml => "RDF/XML"
            case RdfRepresentation.Turtle => "TURTLE"
        }
        model.read(new java.io.StringReader(data), null, language)

        val graph = Graph(model)
        model.close()
        graph
    }

    /**
      * Creates a new Graph instance from an instance of [[com.hp.hpl.jena.rdf.model.Model]].
      * @param model The model to create the graph from.
      * @return A new graph instance.
      */
    private def apply(model: Model): Graph = {
        val literalVertices = mutable.ListBuffer.empty[LiteralVertex]
        val edges = mutable.HashSet.empty[Edge]
        val identifiedVertices = mutable.HashMap.empty[String, IdentifiedVertex]
        def getIdentifiedVertex(uri: String) = identifiedVertices.getOrElseUpdate(uri, new IdentifiedVertex(uri))

        // Process the vertices.
        val subjectIterator = model.listSubjects
        while (subjectIterator.hasNext) {
            val subject = subjectIterator.nextResource
            val origin = getIdentifiedVertex(subject.getURI)

            // Process the edges that originate in the current vertex.
            val propertyIterator = subject.listProperties
            while (propertyIterator.hasNext) {
                val statement = propertyIterator.nextStatement
                val predicate = statement.getPredicate
                val obj = statement.getObject
                val destination = if (obj.isLiteral) {
                    val lv = LiteralVertex(obj.asLiteral, statement)
                    literalVertices += lv
                    lv
                } else {
                    getIdentifiedVertex(obj.asResource.getURI)
                }

                edges += new Edge(origin, destination, predicate.getURI)
            }
        }

        new Graph(literalVertices.toList ++ identifiedVertices.values, edges.toList)
    }
}

class Graph(vertices: immutable.Seq[Vertex], edges: immutable.Seq[Edge])
    extends cz.payola.common.rdf.Graph(vertices, edges)
{
    /**
      * Creates a new graph with contents of this graph and the specified other graph.
      * @param otherGraph The graph to be merged.
      * @return A new Graph instance.
      */
    def +(otherGraph: Graph): Graph = {
        val mergedVertices = (vertices.toSet ++ otherGraph.vertices).toList
        val mergedEdges = (edges.toSet ++ otherGraph.edges).toList.map { e =>
            // We have to make sure that all edges reference vertices from the mergedVertices collection. It's sure
            // that vertices equal to origin and destination would be found in the mergedVertices, because edges in the
            // original graphs surely had origin and destination present in the graph vertices.
            val origin = mergedVertices.find(_ == e.origin).get.asInstanceOf[IdentifiedVertex]
            val destination = mergedVertices.find(_ == e.destination).get
            new Edge(origin, destination, e.uri)
        }
        new Graph(mergedVertices, mergedEdges)
    }

    /**
      * Executes the specified SPARQL query.
      * @param query The query to execute.
      * @return A graph that corresponds to the executed query result.
      */
    def executeSPARQLQuery(query: String): Graph = {
        val sparqlQuery = QueryFactory.create(query)
        val model = getModel
        try {
            val execution = QueryExecutionFactory.create(sparqlQuery, model)
            try {
                sparqlQuery.getQueryType() match {
                    case Query.QueryTypeSelect => processSelectQueryExecution(execution)
                    case Query.QueryTypeConstruct => processConstructQueryExecution(execution)
                    case Query.QueryTypeAsk => throw new DomainException("An ASK query isn't supported.")
                    case Query.QueryTypeDescribe => throw new DomainException("A DESCRIBE query isn't supported.")
                    case _ => throw new DomainException("Unknown type of the query.")
                }
            } finally {
                execution.close()
            }
        } finally {
            model.close()
        }
    }

    /**
      * Processes a query execution corresponding to a SPARQL select query.
      * @param execution The query execution to process.
      * @return A graph containing the result of the query.
      */
    private def processSelectQueryExecution(execution: QueryExecution): Graph = {
        val results = execution.execSelect
        val output = new java.io.ByteArrayOutputStream()
        ResultSetFormatter.outputAsRDF(output, "", results)
        Graph(RdfRepresentation.RdfXml, new String(output.toByteArray))
    }

    /**
      * Processes a query execution corresponding to a SPARQL construct query.
      * @param execution The query execution to process.
      * @return A graph containing the result of the query.
      */
    private def processConstructQueryExecution(execution: QueryExecution): Graph = {
        Graph(execution.execConstruct)
    }

    /**
      * Creates a Jena model out of the graph. The model has to be closed using the 'close' method, after working with
      * it is done.
      * @return Model representing this graph.
      */
    private def getModel: Model = {
        val model = ModelFactory.createDefaultModel()

        // A map of resources identified by their URIs.
        val resources = mutable.HashMap.empty[String, Resource]
        def getResource(uri: String): Resource = resources.getOrElseUpdate(uri, model.createResource(uri))

        // Add all identified vertices.
        vertices.foreach {
            case iv: IdentifiedVertex => getResource(iv.uri)
            case _ => // NOOP
        }

        // Add all the edges
        edges.foreach { e =>
            val origin = getResource(e.origin.uri)
            val property = ResourceFactory.createProperty(e.uri)
            val statement = e.destination match {
                case iv: IdentifiedVertex => {
                    val destination = getResource(iv.uri)
                    origin.addProperty(property, destination)
                    model.createStatement(origin, property, destination)
                }
                case lv: LiteralVertex => {
                    model.createStatement(origin, property, lv.value.toString, lv.language.getOrElse(""))
                }
            }
            model.add(statement)
        }

        model
    }
}
