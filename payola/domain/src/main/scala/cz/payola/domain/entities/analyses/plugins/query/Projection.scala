package cz.payola.domain.entities.analyses.plugins.query

import cz.payola.domain.entities.analyses.parameters.StringParameter
import cz.payola.domain.entities.analyses.PluginInstance
import collection.immutable
import cz.payola.domain.sparql._

class Projection extends Construct("Projection", List(new StringParameter("PropertyURIs", "")))
{
    def getPropertyURIs(instance: PluginInstance): Option[immutable.Seq[String]] = {
        instance.getStringParameter("PropertyURIs").map(_.split("\n").toList)
    }

    def getConstructQuery(instance: PluginInstance, subject: Subject, variableGetter: () => Variable) = {
        getPropertyURIs(instance).map {uris =>
            val triples = uris.map(uri => TriplePattern(subject, Uri(uri), variableGetter()))
            ConstructQuery(triples)
        }
    }
}
