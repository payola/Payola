package cz.payola.data.entities

class FloatParameter(
        id: String = java.util.UUID.randomUUID.toString,
        name: String,
        defaultValue: Float,
        plugin: Plugin)
    extends cz.payola.domain.entities.parameters.FloatParameter(id, name, defaultValue)
    with PersistableEntity
    with Parameter[Float]
{
    val pluginId: String = if (plugin == null) "" else plugin.id

    private lazy val _instances = schema.PayolaDB.instancesOfFloatParameters.left(this)

    def instances: Seq[FloatParameterInstance] = evaluateCollection(_instances)
}


