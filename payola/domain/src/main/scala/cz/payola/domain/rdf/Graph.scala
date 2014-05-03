package cz.payola.domain.rdf

import cz.payola.common.rdf._
import java.io._
import scala.io._
import com.hp.hpl.jena.rdf.model._
import com.hp.hpl.jena.query.ResultSetFormatter
import com.hp.hpl.jena.query._
import scala.collection.JavaConverters._
import org.apache.jena.riot._
import scala.collection.immutable
import cz.payola.domain.DomainException

object Graph
{    
    def rdf2JenaDataset(representation: RdfRepresentation.Type, data: String): com.hp.hpl.jena.query.Dataset = {
        try {
            val dataInputStream = new ByteArrayInputStream(data.getBytes("UTF-8"))
            val jenaLanguage = representationToJenaLanguage(representation)

            val dataSet = DatasetFactory.createMem()
            RDFDataMgr.read(dataSet, dataInputStream, jenaLanguage)
            dataSet
        } catch {
            case e: org.apache.jena.riot.RiotException => {
                println(e.getMessage)
                throw new IllegalArgumentException("Query failed, returned non-XML data: "+data.substring(0, 500))
            }
            case e: Exception => throw new IllegalArgumentException(e.getMessage)
        }
    }

    def rdf2Jena(representation: RdfRepresentation.Type, data: String): scala.collection.Seq[com.hp.hpl.jena.graph.Graph] = {
        val dataSet = rdf2JenaDataset(representation, data)
        val dataSetGraph = dataSet.asDatasetGraph()
        List(dataSetGraph.getDefaultGraph()) ++ dataSetGraph.listGraphNodes().asScala.toList.map { n =>
            dataSetGraph.getGraph(n)
        }
    }

    private def representationToJenaLanguage(representation: RdfRepresentation.Type) = {
        representation match {
            case RdfRepresentation.RdfXml => Lang.RDFXML
            case RdfRepresentation.Turtle => Lang.TURTLE
            case RdfRepresentation.Trig => Lang.TRIG
        }
    }
}

abstract class Graph(vertices: immutable.Seq[Vertex], edges: immutable.Seq[Edge], _resultCount: Option[Long])
    extends cz.payola.common.rdf.Graph(vertices, edges, _resultCount)
{
    /**
     * Creates a new graph with contents of this graph and the specified other graph.
     * @param otherGraph The graph to be merged.
     * @return A new Graph instance.
     */
    def +(otherGraph: Graph): Graph

    /**
     * Creates a Jena model out of the graph. The model has to be closed using the 'close' method, after working with
     * it is done.
     * @return Model representing this graph.
     */
    def getModel : Model

    protected def makeGraph(representation: RdfRepresentation.Type, rdf: String): Graph

    /**
     * Processes a query execution corresponding to a SPARQL construct query.
     * @param execution The query execution to process.
     * @return A graph containing the result of the query.
     */
    protected def processConstructQueryExecution(execution: QueryExecution): Graph

    /**
     * Returns a string representation of the graph - either in RDF/XML or TTL.
     * @param representationFormat Output format.
     */
    def toStringRepresentation(representationFormat: RdfRepresentation.Type): String = {
        val outputStream = new ByteArrayOutputStream()
        representationFormat match {
            case RdfRepresentation.RdfXml => getModel.write(outputStream)
            case RdfRepresentation.Turtle => getModel.write(outputStream, "TURTLE")
        }

        val s = Source.fromInputStream(new ByteArrayInputStream(outputStream.toByteArray), "UTF-8").mkString
        "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" + s
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
                sparqlQuery.getQueryType match {
                    case Query.QueryTypeSelect => processSelectQueryExecution(execution)
                    case Query.QueryTypeConstruct => processConstructQueryExecution(execution)
                    case _ => throw new DomainException("Unsupported query type.")
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
    protected def processSelectQueryExecution(execution: QueryExecution): Graph = {
        val results = execution.execSelect
        val output = new java.io.ByteArrayOutputStream()
        ResultSetFormatter.outputAsRDF(output, "", results)
        makeGraph(RdfRepresentation.RdfXml, new String(output.toByteArray))
    }
}
