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

    protected def queryString(parameterValues: Seq[ParameterValueType]): String = {
        parameterValues.head.asInstanceOf[StringParameterValue].value
    }

    def evaluate(inputGraph: Graph, parameterValues: Seq[ParameterValueType], progressReporter: Double => Unit) = {
        val qs = this.queryString(parameterValues)

        /** Unfortunately because of the the way Jena returns results,
          *  it is necessary to distinguish each query type.
          */

        // TODO smarter matching
        if (qs.contains("SELECT")){
            inputGraph.executeSelectSPARQLQuery(qs)
        }else if (qs.contains("CONSTRUCT")) {
            inputGraph.executeConstructSPARQLQuery(qs)
        }else{
            // TODO ASK and possibly DESCRIBE?
            throw new IllegalArgumentException("Unknown SPARQL query type (" + qs + ")")
        }
    }
}
