package cz.payola.domain.entities.plugins.concrete.query

import collection.immutable
import cz.payola.domain.IDGenerator
import cz.payola.domain.entities.plugins._
import cz.payola.domain.sparql
import cz.payola.domain.sparql._
import cz.payola.domain.entities.plugins.parameters._
import cz.payola.domain.sparql.Variable
import cz.payola.domain.sparql.TriplePattern

/**
 * Definition of the Limit plugin, its parameters, input count, etc.
 *
 * @author Jiri Helmich
 */
class Limit(name: String, inputCount: Int, parameters: immutable.Seq[Parameter[_]], id: String)
    extends Construct(name, inputCount, parameters, id)
{
    def this() = {
        this("Limit", 1, List(new IntParameter(Limit.limitCountParameter, 0, Some(0))), IDGenerator.newId)
    }

    def getLimitCount(instance: PluginInstance): Option[Int] = {
        instance.getIntParameter(Limit.limitCountParameter)
    }

    def getConstructQuery(instance: PluginInstance, subject: Subject, variableGetter: () => Variable) = {
        usingDefined(getLimitCount(instance)) { limit =>
            val triples = List(TriplePattern(subject, variableGetter(), variableGetter()))
            ConstructQuery(GraphPattern(triples), sparql.Limit(limit))
        }
    }
}

object Limit
{
    val limitCountParameter = "Limit count"
}


