package cz.payola.data.squeryl.entities.plugins

trait PluginInstanceLike extends cz.payola.common.entities.plugins.PluginInstance
{
    var _isEdit: Boolean

    var pluginId: String

    // Load value from DB
    isEditable = _isEdit

    def parameterValues_=(value: collection.immutable.Seq[cz.payola.domain.entities.plugins.ParameterValue[_]]) {
        _parameterValues = value.asInstanceOf[collection.immutable.Seq[PluginType#ParameterValueType]]
    }

    def plugin_=(value: cz.payola.domain.entities.Plugin) {
        _plugin = value.asInstanceOf[PluginType]
    }
}
