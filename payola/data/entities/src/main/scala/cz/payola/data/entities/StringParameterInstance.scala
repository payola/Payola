package cz.payola.data.entities

class StringParameterInstance(
        id: String = java.util.UUID.randomUUID.toString,
        parameter: StringParameter,
        value: String,
        pluginInstance: PluginInstance)
    extends cz.payola.domain.entities.parameters.StringParameterInstance(id, parameter, value)
    with PersistableEntity
    with ParameterInstance[String]
{
    val pluginInstanceId: String = if (pluginInstance == null) "" else pluginInstance.id
}
