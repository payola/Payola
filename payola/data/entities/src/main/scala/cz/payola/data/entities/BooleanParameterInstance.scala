package cz.payola.data.entities

class BooleanParameterInstance(
        id: String = java.util.UUID.randomUUID.toString,
        parameter: BooleanParameter,
        value: Boolean,
        pluginInstance: PluginInstance)
    extends cz.payola.domain.entities.parameters.BooleanParameterInstance(id, parameter, value)
    with PersistableEntity
    with ParameterInstance[Boolean]
{
    val pluginInstanceId: String = if (pluginInstance == null) "" else pluginInstance.id
}
