package cz.payola.domain.entities.plugins

import cz.payola.domain.entities.Plugin
import cz.payola.domain.entities.parameters.StringParameter

sealed class SparqlQueryPlugin extends Plugin("Sparql query", List(new StringParameter("Query", "")))
{
    def queryParameter: StringParameter = parameters.head.asInstanceOf[StringParameter]
}
