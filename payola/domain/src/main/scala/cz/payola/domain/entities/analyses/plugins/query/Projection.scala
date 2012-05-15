package cz.payola.domain.entities.analyses.plugins.query

import cz.payola.domain.entities.analyses.parameters.StringParameter
import collection.immutable
import cz.payola.domain.sparql._
import cz.payola.domain.IDGenerator
import cz.payola.domain.entities.analyses._

class Projection(
    name: String = "Projection",
    inputCount: Int = 1,
    parameters: immutable.Seq[Parameter[_]] = List(new StringParameter("PropertyURIs", "")),
    id: String = IDGenerator.newId)
    extends Construct(name, inputCount, parameters, id)
{
    def getPropertyURIs(instance: PluginInstance): Option[immutable.Seq[String]] = {
        instance.getStringParameter("PropertyURIs").map(_.split("\n").toList)
    }

    def getConstructQuery(instance: PluginInstance, subject: Subject, variableGetter: () => Variable) = {
        getPropertyURIs(instance).map { uris =>
            val triples = uris.map(uri => TriplePattern(subject, Uri(uri), variableGetter()))
            ConstructQuery(triples)
        }
    }
}
