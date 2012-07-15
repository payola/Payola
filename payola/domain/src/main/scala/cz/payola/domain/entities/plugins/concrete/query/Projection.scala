package cz.payola.domain.entities.plugins.concrete.query

import collection.immutable
import cz.payola.domain.IDGenerator
import cz.payola.domain.entities.plugins._
import cz.payola.domain.entities.plugins.parameters.StringParameter
import cz.payola.domain.sparql._

class Projection(name: String, inputCount: Int, parameters: immutable.Seq[Parameter[_]], id: String)
    extends Construct(name, inputCount, parameters, id)
{
    def this() = {
        this("Projection", 1, List(new StringParameter("PropertyURIs", "")), IDGenerator.newId)
        isPublic = true
    }

    def getPropertyURIs(instance: PluginInstance): Option[immutable.Seq[String]] = {
        instance.getStringParameter("PropertyURIs").map(_.split("\n").toList)
    }

    def getConstructQuery(instance: PluginInstance, subject: Subject, variableGetter: () => Variable) = {
        usingDefined(getPropertyURIs(instance)) { uris =>
            val triples = uris.map(uri => TriplePattern(subject, Uri(uri), variableGetter()))
            ConstructQuery(triples)
        }
    }
}
