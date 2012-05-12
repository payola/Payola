package cz.payola.domain.entities.analyses.plugins.query

import cz.payola.domain.entities.analyses.parameters.StringParameter
import cz.payola.domain.entities.analyses.PluginInstance
import cz.payola.domain.sparql._

class Typed extends Construct("Typed", List(new StringParameter("TypeURI", "")))
{
    val typePropertyURI = Uri("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")

    def getTypeURI(instance: PluginInstance): Option[String] = {
        instance.getStringParameter("TypeURI")
    }

    def getConstructQuery(instance: PluginInstance, subject: Subject, variableGetter: () => Variable) = {
        getTypeURI(instance).map(uri => ConstructQuery(TriplePattern(subject, typePropertyURI, Uri(uri))))
    }
}
