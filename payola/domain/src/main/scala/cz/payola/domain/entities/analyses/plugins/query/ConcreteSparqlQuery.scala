package cz.payola.domain.entities.analyses.plugins.query

import cz.payola.domain.entities.analyses.plugins.SparqlQuery
import cz.payola.domain.entities.analyses.parameters.StringParameter
import cz.payola.domain.entities.analyses.PluginInstance

sealed class ConcreteSparqlQuery extends SparqlQuery("SPARQL query", List(new StringParameter("Query", "")))
{
    def getQuery(instance: PluginInstance): String = {
        instance.getStringParameter("Query").get
    }
}
