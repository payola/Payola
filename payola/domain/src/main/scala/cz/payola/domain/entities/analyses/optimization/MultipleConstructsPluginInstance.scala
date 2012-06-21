package cz.payola.domain.entities.analyses.optimization

import collection.immutable
import cz.payola.domain.entities.analyses._
import cz.payola.domain.entities.plugins.PluginInstance
import cz.payola.domain.entities.plugins.concrete.query.Construct

object MultipleConstructsPluginInstance
{
    def apply(instances: PluginInstance*): MultipleConstructsPluginInstance = {
        instances.foldLeft(MultipleConstructsPluginInstance.empty)(_ + _)
    }

    def empty: MultipleConstructsPluginInstance = {
        new MultipleConstructsPluginInstance(Nil)
    }
}

class MultipleConstructsPluginInstance(val constructs: immutable.Seq[PluginWithInstance[Construct]])
    extends PluginInstance(MultipleConstructsPlugin, Nil)
{
    def +(instance: PluginInstance): MultipleConstructsPluginInstance = {
        instance.plugin match {
            case construct: Construct => {
                new MultipleConstructsPluginInstance(PluginWithInstance(construct, instance) +: constructs)
            }
            case plugin => {
                throw new AnalysisException("Cannot add an instance of plugin %s.".format(plugin.getClass))
            }
        }
    }
}
