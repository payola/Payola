package cz.payola.domain.entities.analyses.optimization

import cz.payola.domain.entities.analyses.plugins.query.Construct
import cz.payola.domain.entities.analyses._
import cz.payola.domain.entities.analyses.PluginException
import cz.payola.domain.sparql._

object MultipleConstructsPlugin extends Construct("Merged SPARQL construct queries")
{
    override def createInstance(): PluginInstance = {
        MultipleConstructsPluginInstance.empty
    }

    def getConstructQuery(instance: PluginInstance, subject: Subject, variableGetter: () => Variable) = {
        instance match {
            case constructsInstance: MultipleConstructsPluginInstance => {
                val constructs = constructsInstance.constructs
                val queries = constructs.map(c => c.plugin.getConstructQuery(c.instance, subject, variableGetter))
                queries.fold(ConstructQuery.empty)(_ + _)
            }
            case _ => throw new PluginException("The specified plugin instance doesn't correspond to the plugin.")
        }
    }
}
