package cz.payola.domain.entities.analyses.plugins.query

import cz.payola.domain.entities.analyses.{PluginInstance, Parameter}
import collection.immutable
import cz.payola.domain.entities.analyses.plugins.SparqlQuery
import cz.payola.domain.sparql.{ConstructQuery, Subject, Variable}

abstract class Construct(name: String, parameters: immutable.Seq[Parameter[_]])
    extends SparqlQuery(name, parameters)
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
