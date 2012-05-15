package cz.payola.domain.entities.analyses.optimization

import cz.payola.domain.entities.analyses.PluginInstance
import cz.payola.domain.entities.analyses.plugins.query.Construct
import cz.payola.domain.sparql.{Variable, Subject}

object MultipleConstructsPlugin extends Construct("Merged SPARQL construct queries", Nil)
{
    override def createInstance(): PluginInstance = {
        MultipleConstructsPluginInstance.empty
    }

    def getConstructQuery(instance: PluginInstance, subject: Subject, variableGetter: () => Variable) = {
        instance match {
            case constructsInstance: MultipleConstructsPluginInstance => {
                val constructs = constructsInstance.constructs
                val queries = constructs.flatMap(c => c.plugin.getConstructQuery(c.instance, subject, variableGetter))
                if (queries.nonEmpty) {
                    Some(queries.reduce(_ + _))
                } else {
                    None
                }
            }
            case _ => None
        }
    }
}
