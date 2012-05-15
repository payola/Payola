package cz.payola.domain.entities.analyses.plugins.query

import collection.immutable
import cz.payola.domain.entities.analyses.plugins.SparqlQuery
import cz.payola.domain.sparql.{ConstructQuery, Subject, Variable}
import cz.payola.domain.entities.analyses.{Parameter, PluginInstance}
import cz.payola.domain.IDGenerator

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
    def getConstructQuery(instance: PluginInstance, subject: Subject, variableGetter: () => Variable):
    Option[ConstructQuery]

    def getQuery(instance: PluginInstance): Option[String] = {
        var i = 0;
        def variableGetter = () => {
            i += 1
            Variable("v" + i)
        }

        getConstructQuery(instance, Variable("s"), variableGetter).map(_.toString)
    }
}
