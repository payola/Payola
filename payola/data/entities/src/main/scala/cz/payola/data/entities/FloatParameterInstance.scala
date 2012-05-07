package cz.payola.data.entities

class FloatParameterInstance(
        id: String = java.util.UUID.randomUUID.toString,
        parameter: FloatParameter,
        value: Float,
        pluginInstance: PluginInstance)
    extends cz.payola.domain.entities.parameters.FloatParameterInstance(id, parameter, value)
    with PersistableEntity
    with ParameterInstance[Float]
{
    val pluginInstanceId: String = if (pluginInstance == null) "" else pluginInstance.id
}
