package cz.payola.domain.entities.analyses.plugins.query

import cz.payola.domain.entities.analyses.parameters.StringParameter
import cz.payola.domain.entities.analyses.{PluginInstance, Plugin}
import cz.payola.domain.entities.analyses.plugins.SparqlQuery

sealed class Construct extends SparqlQuery("Construct Query", List(
    new StringParameter("URI", ""),
    new StringParameter("Operator", ""),
    new StringParameter("Value", "")))
{
    def getQuery(instance: PluginInstance): String = {
        val uri = instance.getStringParameter("URI").get
        val operator = instance.getStringParameter("Operator").get
        val value = instance.getStringParameter("Value").get

        // TODO Prefixes?
        "PREFIX foaf:    <http://xmlns.com/foaf/0.1/>\n" +
            "CONSTRUCT { " + uri + " " + operator + " " + value + " }"
    }
}
