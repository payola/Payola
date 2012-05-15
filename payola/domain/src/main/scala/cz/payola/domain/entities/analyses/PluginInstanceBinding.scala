package cz.payola.domain.entities.analyses

import cz.payola.domain.entities.Entity

class PluginInstanceBinding(protected val _sourcePluginInstance: PluginInstance,
    protected val _targetPluginInstance: PluginInstance, protected val _targetInputIndex: Int = 0)
    extends Entity with cz.payola.common.entities.analyses.PluginInstanceBinding
{
    // When Squeryl is creating schema, then both parameters are null
    if (_sourcePluginInstance != null && _targetPluginInstance != null) {
        require(_sourcePluginInstance != _targetPluginInstance,
            "The source plugin instance cannot also be the target plugin instance (a cycle formed of one plugin instance).")
        require(_targetInputIndex >= 0 && _targetInputIndex < _targetPluginInstance.plugin.inputCount,
            "The target input index is invalid.")
    }

    type PluginInstanceType = PluginInstance

    override def canEqual(other: Any): Boolean = {
        other.isInstanceOf[PluginInstanceBinding]
    }
}
