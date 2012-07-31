package cz.payola.domain.entities.analyses.optimization.plugins

import scala.collection.immutable
import cz.payola.domain.entities.analyses.AnalysisException
import cz.payola.domain.entities.analyses.optimization.PluginWithInstance
import cz.payola.domain.entities.plugins.PluginInstance
import cz.payola.domain.entities.plugins.concrete.query.Construct

object MultipleConstructsPluginInstance
{
    /**
      * Joins the specified construct plugin instances into one.
      * @param instances The instances to join.
      * @return The joined multiple constructs plugin instance.
      */
    def apply(instances: PluginInstance*): MultipleConstructsPluginInstance = {
        instances.foldLeft(MultipleConstructsPluginInstance.empty)(_ + _)
    }

    /**
      * Returns an empty multiple constructs plugin instance.
      */
    def empty: MultipleConstructsPluginInstance = {
        new MultipleConstructsPluginInstance(Nil)
    }
}

/**
  * An instance of the multiple constructs optimization plugin.
  * @param constructs The construct plugin instances with their plugins.
  */
class MultipleConstructsPluginInstance(val constructs: immutable.Seq[PluginWithInstance[Construct]])
    extends PluginInstance(MultipleConstructsPlugin, Nil)
{
    /**
      * Creates a new instance based on the current instance with added specified construct plugin instance.
      * @param instance The construct plugin instance to add.
      * @return The new instance of multiple constructs plugin.
      */
    def +(instance: PluginInstance): MultipleConstructsPluginInstance = {
        instance.plugin match {
            case construct: Construct => {
                new MultipleConstructsPluginInstance(PluginWithInstance(construct, instance) +: constructs)
            }
            case plugin => {
                throw new AnalysisException("Cannot add an instance of plugin %s.".format(plugin.getClass.getName))
            }
        }
    }
}
