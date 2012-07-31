package cz.payola.domain.entities.analyses

import cz.payola.domain.Entity
import cz.payola.domain.entities.plugins.PluginInstance

/**
  * @param _sourcePluginInstance The plugin instance that acts as a source of the binding.
  * @param _targetPluginInstance The plugin instance that acts as a target of the binding.
  * @param _targetInputIndex Index of the target plugin instance input the binding is connected to.
  */
class PluginInstanceBinding(
    protected var _sourcePluginInstance: PluginInstance,
    protected var _targetPluginInstance: PluginInstance,
    protected val _targetInputIndex: Int = 0)
    extends Entity
    with cz.payola.common.entities.analyses.PluginInstanceBinding
{
    checkConstructorPostConditions()

    type PluginInstanceType = PluginInstance

    override def canEqual(other: Any): Boolean = {
        other.isInstanceOf[PluginInstanceBinding]
    }

    override protected def checkInvariants() {
        super[Entity].checkInvariants()
        validate(sourcePluginInstance != null, "sourcePluginInstance",
            "The source plugin instance of the binding mustn't be null.")
        validate(targetPluginInstance != null, "targetPluginInstance",
            "The target plugin instance of the binding mustn't be null.")
        validate(sourcePluginInstance != targetPluginInstance, "sourcePluginInstance",
            "The source plugin instance of the binding cannot also be the target plugin instance (a cycle formed of " +
            "one plugin instance).")
        validate(targetInputIndex >= 0 && targetInputIndex < targetPluginInstance.plugin.inputCount,
            "targetInputIndex", "The target input index of the binding is invalid.")
    }
}
