package cz.payola.domain.entities.analyses.plugins.query

import cz.payola.domain.entities.analyses.parameters.StringParameter
import cz.payola.domain.entities.analyses.PluginInstance

class Selection extends SparqlQueryPart("Selection", List(
    new StringParameter("PropertyURI", ""),
    new StringParameter("Operator", ""),
    new StringParameter("Value", "")))
{
    def getPropertyURI(instance: PluginInstance): Option[String] = {
        instance.getStringParameter("PropertyURI")
    }

    def getOperator(instance: PluginInstance): Option[String] = {
        instance.getStringParameter("Operator")
    }

    def getValue(instance: PluginInstance): Option[String] = {
        instance.getStringParameter("Value")
    }

    def getPattern(instance: PluginInstance, subject: String = defaultSubject): Option[String] = {
        getPropertyURI(instance).map(uri => getTriplePattern(subject, uri, "?y"))
    }

    override def getFilter(instance: PluginInstance, obj: String = defaultObject): Option[String] = {
        getOperator(instance).flatMap(operator => getValue(instance).map(value => obj + " " + operator + " " + value))
    }
}
