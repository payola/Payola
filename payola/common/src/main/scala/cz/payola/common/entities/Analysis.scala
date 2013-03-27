package cz.payola.common.entities

import scala.collection._
import cz.payola.common.Entity
import cz.payola.common.entities.plugins.PluginInstance
import cz.payola.common.entities.analyses.PluginInstanceBinding

/**
  * A set of analytical plugin instances that are bound together (the output of one plugin instance is bound to the
  * input of another plugin instance). The analysis is in a valid state iff all plugin instances have all inputs and
  * outputs bound, no input nor output is bound more than once and there is one plugin instance that doesn't have its
  * output bound. That is the analysis output. If the analysis is in the valid state, it may be evaluated.
  */
trait Analysis extends Entity with NamedEntity with OptionallyOwnedEntity with ShareableEntity with DescribedEntity
{
    /** Type of the analytical plugin instances the analysis consists of. */
    type PluginInstanceType <: PluginInstance

    /** Type of the bindings between analytical plugin instances. */
    type PluginInstanceBindingType <: PluginInstanceBinding

    /** Type of the ontology customization for analysis */
    type OntologyCustomizationType <: settings.OntologyCustomization

    protected var _pluginInstances = mutable.ArrayBuffer[PluginInstanceType]()

    protected var _pluginInstanceBindings = mutable.ArrayBuffer[PluginInstanceBindingType]()

    protected var _defaultCustomization: Option[OntologyCustomizationType] = None

    var token: Option[String] = None

    /** Analytical plugin instances the analysis consists of.*/
    def pluginInstances: immutable.Seq[PluginInstanceType] = _pluginInstances.toList

    /** Bindings between the analytical plugin instances. */
    def pluginInstanceBindings: immutable.Seq[PluginInstanceBindingType] = _pluginInstanceBindings.toList

    /** Default ontology customization for this analysis */
    def defaultOntologyCustomization = _defaultCustomization

    /**
      * Sets new default ontology customization for this analysis
      * @param value New default ontology customization for this analysis
      */
    def defaultOntologyCustomization_=(value: Option[OntologyCustomizationType]) { _defaultCustomization = value }

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
