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
        require(sourcePluginInstance != null, "The source plugin instance mustn't be null.")
        require(targetPluginInstance != null, "The target plugin instance mustn't be null.")
        require(sourcePluginInstance != targetPluginInstance, "The source plugin instance cannot also be the target " +
            "plugin instance (a cycle formed of one plugin instance).")
        require(targetInputIndex >= 0 && targetInputIndex < targetPluginInstance.plugin.inputCount,
            "The target input index is invalid.")
    }
}
