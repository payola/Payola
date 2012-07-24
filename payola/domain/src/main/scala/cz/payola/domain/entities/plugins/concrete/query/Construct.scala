package cz.payola.domain.entities.plugins.concrete.query

import collection.immutable
import cz.payola.domain.IDGenerator
import cz.payola.domain.entities.plugins._
import cz.payola.domain.entities.plugins.concrete.SparqlQuery
import cz.payola.domain.sparql._
import cz.payola.common.rdf.Edge

object Construct
{
    def optionalProperties(subject: Subject, variableGetter: () => Variable): immutable.Seq[GraphPattern] = {
        (Edge.rdfTypeEdge :: Edge.rdfLabelEdges).map { e =>
            GraphPattern(TriplePattern(subject, Uri(e), variableGetter()))
        }
    }
}

abstract class Construct(
    name: String,
    inputCount: Int = 1,
    parameters: immutable.Seq[Parameter[_]] = Nil,
    id: String = IDGenerator.newId)
    extends SparqlQuery(name, inputCount, parameters, id)
{
    /**
      * Returns the construct query representation.
      * @param instance The instance whose query representation should be retrieved.
      * @param subject The subject to use within the representation.
      * @param variableGetter An unique variable provider, that must be used when the plugin needs a new variable.
      * @return The construct query representation.
      */
    def getConstructQuery(instance: PluginInstance, subject: Subject, variableGetter: () => Variable): ConstructQuery

    def getQuery(instance: PluginInstance): String = {
        val query = getConstructQuery(instance, Variable("s"), new VariableGenerator)
        if (query.isEmpty) {
            throw new PluginException("The construct query is empty.")
        } else {
            query.toString
        }
    }
}
