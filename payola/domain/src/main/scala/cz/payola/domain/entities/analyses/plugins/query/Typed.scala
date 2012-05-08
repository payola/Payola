package cz.payola.domain.entities.analyses.plugins.query

import cz.payola.domain.entities.analyses.parameters.StringParameter
import cz.payola.domain.entities.analyses.PluginInstance

class Typed extends SparqlQueryPart("Typed", List(new StringParameter("TypeURI", "")))
{
    val typePropertyURI = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type"

    def getTypeURI(instance: PluginInstance): Option[String] = {
        instance.getStringParameter("TypeURI")
    }

    def getPattern(instance: PluginInstance, subject: String = defaultSubject): Option[String] = {
        getTypeURI(instance).map(uri => getTriplePattern(subject, typePropertyURI, "<" + uri + ">"))
    }
}
