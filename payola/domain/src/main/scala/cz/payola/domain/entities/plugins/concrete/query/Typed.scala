package cz.payola.domain.entities.plugins.concrete.query

import collection.immutable
import cz.payola.domain.IDGenerator
import cz.payola.domain.entities.plugins._
import cz.payola.domain.sparql._
import cz.payola.domain.entities.plugins.parameters.StringParameter

class Typed(name: String, inputCount: Int, parameters: immutable.Seq[Parameter[_]], id: String)
    extends Construct(name, inputCount, parameters, id)
{
    def this() = this("Typed", 1, List(new StringParameter("TypeURI", "")), IDGenerator.newId)

    val typePropertyURI = Uri("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")

    def getTypeURI(instance: PluginInstance): Option[String] = {
        instance.getStringParameter("TypeURI")
    }

    def getConstructQuery(instance: PluginInstance, subject: Subject, variableGetter: () => Variable) = {
        usingDefined(getTypeURI(instance)) { uri =>
            ConstructQuery(TriplePattern(subject, typePropertyURI, Uri(uri)))
        }
    }
}
