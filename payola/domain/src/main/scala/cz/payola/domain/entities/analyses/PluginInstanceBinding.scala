package cz.payola.domain.entities.analyses

import cz.payola.domain.entities.Entity

class PluginInstanceBinding(
    protected val _sourcePluginInstance: PluginInstance,
    protected val _targetPluginInstance: PluginInstance,
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
