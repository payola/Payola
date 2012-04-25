package cz.payola.data.entities

class StringParameter(
        id: String = java.util.UUID.randomUUID.toString,
        name: String,
        defaultValue: String,
        plugin: Plugin)
    extends cz.payola.domain.entities.parameters.StringParameter(id, name, defaultValue)
    with PersistableEntity
    with Parameter[String]
{
    val pluginId: String = if (plugin == null) "" else plugin.id

    private lazy val _instances = schema.PayolaDB.instancesOfStringParameters.left(this)

    def instances: Seq[StringParameterInstance] = evaluateCollection(_instances)
}


