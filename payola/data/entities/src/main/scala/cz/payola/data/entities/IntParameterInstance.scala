package cz.payola.data.entities

class IntParameterInstance(
        id: String = java.util.UUID.randomUUID.toString,
        parameter: IntParameter,
        value: Int,
        pluginInstance: PluginInstance)
    extends cz.payola.domain.entities.parameters.IntParameterInstance(id, parameter, value)
    with PersistableEntity
    with ParameterInstance[Int]
{
    val pluginInstanceId: String = if (pluginInstance == null) "" else pluginInstance.id
}
