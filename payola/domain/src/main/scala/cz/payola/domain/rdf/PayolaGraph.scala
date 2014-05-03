package cz.payola.domain.rdf

import scala.collection._
import scala.collection.JavaConverters._
import scala.io.Source
import java.io._
import com.hp.hpl.jena.query._
import com.hp.hpl.jena.rdf.model._
import org.apache.jena.riot._
import org.apache.jena.riot.lang._
import cz.payola.domain._
import cz.payola.common.rdf._
import java.util.UUID

object PayolaGraph
{
    /**
     * Returns a new empty graph.
     */
    def empty: PayolaGraph = new PayolaGraph(Nil, Nil, None)

    /**
     * Takes a string representing a RDF data and returns an instance of Graph representing that particular graph.
     * @param representation Type of the RDF data representation.
     * @param data The RDF data of the graph.
     * @return A new graph instance.
     */
    def apply(representation: RdfRepresentation.Type, data: String): PayolaGraph = {

        val result = Graph.rdf2Jena(representation, data).map(g => PayolaGraph(ModelFactory.createModelForGraph(g)))

        result.size match {
            case 0 => PayolaGraph.empty
            case 1 => result.head
            case _ => result.fold(PayolaGraph.empty)(_ + _)
        }
    }

    /**
     * Creates a new Graph instance from an instance of [[com.hp.hpl.jena.rdf.model.Model]].
     * @param model The model to create the graph from.
     * @return A new graph instance.
     */
    private[rdf] def apply(model: Model): PayolaGraph = {

        val literalVertices = mutable.ListBuffer.empty[LiteralVertex]
        val edges = mutable.HashSet.empty[Edge]
        val identifiedVertices = mutable.HashMap.empty[String, IdentifiedVertex]
        def getIdentifiedVertex(node: RDFNode) = {
            val uri = Option(node.asResource.getURI).getOrElse("nodeId://blank/"+node.toString)
            identifiedVertices.getOrElseUpdate(uri, new IdentifiedVertex(uri))
        }

        // Process the vertices.
        val subjectIterator = model.listSubjects
        while (subjectIterator.hasNext) {
            val subject = subjectIterator.nextResource
            val origin = getIdentifiedVertex(subject)

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
                    getIdentifiedVertex(obj)
                }

                edges += new Edge(origin, destination, predicate.getURI)
            }
        }

        new PayolaGraph(literalVertices.toList ++ identifiedVertices.values, edges.toList, Some(model.size()))
    }
}

class PayolaGraph(vertices: immutable.Seq[Vertex], edges: immutable.Seq[Edge], _resultCount: Option[Long])
    extends Graph(vertices, edges, _resultCount)
{
    def +(otherGraph: Graph): PayolaGraph = {
        val model = getModel
        model.add(otherGraph.getModel)
        PayolaGraph(model)
    }

    def getModel: Model = {
        println("modelstart")
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
        println("modelend")
        model
    }

    protected def makeGraph(representation: RdfRepresentation.Type, rdf: String): PayolaGraph = {
        PayolaGraph(representation, rdf)
    }


    protected def processConstructQueryExecution(execution: QueryExecution): PayolaGraph = {
        PayolaGraph(execution.execConstruct)
    }
}
