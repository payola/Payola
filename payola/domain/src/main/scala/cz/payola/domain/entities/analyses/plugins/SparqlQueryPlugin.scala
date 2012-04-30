package cz.payola.domain.entities.analyses.plugins

import cz.payola.domain.rdf.Graph
import cz.payola.domain.entities.analyses.parameters.{StringParameterValue, StringParameter}
import cz.payola.domain.entities.analyses.Plugin
import com.hp.hpl.jena.query.ResultSet
import com.hp.hpl.jena.query.{QueryExecution, QueryFactory, QueryExecutionFactory, ResultSetFormatter}
import com.hp.hpl.jena.rdf.model.Model
import java.io.ByteArrayOutputStream

sealed class SparqlQueryPlugin extends Plugin("Sparql query", List(new StringParameter("Query", "")))
{
    def queryParameter: StringParameter = parameters.head.asInstanceOf[StringParameter]

    def evaluate(inputGraph: Graph, parameterValues: Seq[ParameterValueType], progressReporter: Double => Unit) = {
        progressReporter(0.0)

        val queryString: String = parameterValues.head.asInstanceOf[StringParameterValue].value

        require(queryString != null && queryString != "", "Empty or NULL SPARQL query.")

        val query = QueryFactory.create(queryString)
        val model: Model = inputGraph.getModel

        progressReporter(0.33)

        val execution: QueryExecution = QueryExecutionFactory.create(query, model)
        val results: ResultSet = execution.execSelect

        val output: ByteArrayOutputStream = new ByteArrayOutputStream()

        ResultSetFormatter.outputAsRDF(output, "", results);
        execution.close

        val resultingGraphXML: String = new String(output.toByteArray)

        println(resultingGraphXML)

        progressReporter(0.66)

        val g = Graph(resultingGraphXML)

        progressReporter(1.0)

        g
    }
}
