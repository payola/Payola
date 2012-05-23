package cz.payola.common.entities

import analyses.{PluginInstanceBinding, PluginInstance}
import scala.collection.mutable

/**
  * A named sequence of analytical plugin instances.
  */
trait Analysis extends NamedEntity with OptionallyOwnedEntity with ShareableEntity with DescribedEntity
{
    /** Type of the analytical plugin instances the analysis consists of. */
    type PluginInstanceType <: PluginInstance

    /** Type of the bindings between analytical plugin instances. */
    type PluginInstanceBindingType <: PluginInstanceBinding

    private val _pluginInstances = mutable.ArrayBuffer[PluginInstanceType]()

    private val _pluginInstanceBindings = mutable.ArrayBuffer[PluginInstanceBindingType]()

    /** Analytical plugin instances the analysis consists of.*/
    def pluginInstances: Seq[PluginInstanceType] = _pluginInstances

    /** Bindings between the analytical plugin instances. */
    def pluginInstanceBindings: Seq[PluginInstanceBindingType] = _pluginInstanceBindings

    /**
      * Stores the specified plugin instance to the analysis.
      * @param instance The plugin instance to store.
      */
    protected def storePluginInstance(instance: PluginInstanceType) {
        _pluginInstances += instance
    }

    /**
      * Discards the specified plugin instance from the analysis. Complementary operation to store.
      * @param instance The plugin instance to discard.
      */
    protected def discardPluginInstance(instance: PluginInstanceType) {
        _pluginInstances -= instance
    }

    /**
      * Stores the specified plugin instance binding to the analysis.
      * @param binding The plugin instance binding to store.
      */
    protected def storeBinding(binding: PluginInstanceBindingType) {
        _pluginInstanceBindings += binding
    }

    /**
      * Discards the specified plugin instance from the analysis. Complementary operation to store.
      * @param binding The plugin instance binding to discard.
      */
    protected def discardBinding(binding: PluginInstanceBindingType) {
        _pluginInstanceBindings -= binding
    }
}
