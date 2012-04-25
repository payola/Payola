package cz.payola.domain.entities.analyses.plugins

import cz.payola.domain.entities.analyses.Plugin
import cz.payola.domain.entities.analyses.parameters.StringParameter
import cz.payola.domain.rdf.Graph

sealed class SparqlQueryPlugin extends Plugin("Sparql query", List(new StringParameter("Query", "")))
{
    def queryParameter: StringParameter = parameters.head.asInstanceOf[StringParameter]

    def evaluate(inputGraph: Graph, parameterValues: Seq[ParameterValueType], progressReporter: Double => Unit) = {
        // TODO
        inputGraph
    }
}
