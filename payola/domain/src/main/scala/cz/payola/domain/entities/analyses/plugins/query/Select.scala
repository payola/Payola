package cz.payola.domain.entities.analyses.plugins.query

import cz.payola.domain.entities.analyses.parameters.StringParameter
import cz.payola.domain.entities.analyses.{PluginInstance, Plugin}
import cz.payola.domain.entities.analyses.plugins.SparqlQuery

sealed class Select extends SparqlQuery("Construct Query", List(new StringParameter("PropertyNames", "")))
{
    def getQuery(instance: PluginInstance): String = {
        val propertyNames = instance.getStringParameter("PropertyNames").get

        // TODO Prefixes?
        "PREFIX foaf:    <http://xmlns.com/foaf/0.1/>\n" +
            "SELECT " + propertyNames
    }
}
