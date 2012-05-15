package cz.payola.domain.entities.analyses.plugins.query

import cz.payola.domain.entities.analyses.parameters.StringParameter
import cz.payola.domain.sparql._
import scala.collection.immutable
import cz.payola.domain.IDGenerator
import cz.payola.domain.entities.analyses._

class Typed(
    name: String = "Typed",
    inputCount: Int = 1,
    parameters: immutable.Seq[Parameter[_]] = List(new StringParameter("TypeURI", "")),
    id: String = IDGenerator.newId)
    extends Construct(name, inputCount, parameters, id)
{
    val typePropertyURI = Uri("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")

    def getTypeURI(instance: PluginInstance): Option[String] = {
        instance.getStringParameter("TypeURI")
    }

    def getConstructQuery(instance: PluginInstance, subject: Subject, variableGetter: () => Variable) = {
        getTypeURI(instance).map(uri => ConstructQuery(TriplePattern(subject, typePropertyURI, Uri(uri))))
    }
}
