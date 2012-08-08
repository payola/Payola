package cz.payola.data.squeryl.entities.plugins

/**
 * Represents instances of plugins
 */
trait PluginInstanceLike extends cz.payola.common.entities.plugins.PluginInstance
{
    var _isEdit: Boolean

    var pluginId: String

    // Load value from DB
    isEditable = _isEdit

    /**
     * Sets parameter values. Called when fetching the plugin instance from database.
     * @param value List of parameter values of this plugin instance
     */
    def parameterValues_=(value: collection.immutable.Seq[cz.payola.domain.entities.plugins.ParameterValue[_]]) {
        _parameterValues = value.asInstanceOf[collection.immutable.Seq[PluginType#ParameterValueType]]
    }

    /**
     * Set plugin of this instance. Called when fetching the plugin instance from database.
     * @param value Plugin from which the instance is derived
     */
    def plugin_=(value: cz.payola.domain.entities.Plugin) {
        _plugin = value.asInstanceOf[PluginType]
    }
}
