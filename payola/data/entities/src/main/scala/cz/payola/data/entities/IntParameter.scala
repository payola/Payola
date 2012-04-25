package cz.payola.data.entities

class IntParameter(
        id: String = java.util.UUID.randomUUID.toString,
        name: String,
        defaultValue: Int,
        plugin: Plugin)
    extends cz.payola.domain.entities.parameters.IntParameter(id, name, defaultValue)
    with PersistableEntity
    with Parameter[Int]
{
    val pluginId: String = if (plugin == null) "" else plugin.id

    private lazy val _instances = schema.PayolaDB.instancesOfIntParameters.left(this)

    def instances: Seq[IntParameterInstance] = evaluateCollection(_instances)
}


