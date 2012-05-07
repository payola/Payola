package cz.payola.data.entities

class BooleanParameter(
        id: String  = java.util.UUID.randomUUID.toString,
        name: String,
        defaultValue: Boolean,
        plugin: Plugin)
    extends cz.payola.domain.entities.parameters.BooleanParameter(id, name, defaultValue)
    with PersistableEntity
    with Parameter[Boolean]
{
    val pluginId: String = if (plugin == null) "" else plugin.id
    
    private lazy val _instances = schema.PayolaDB.instancesOfBooleanParameters.left(this)

    def instances: Seq[BooleanParameterInstance] = evaluateCollection(_instances)
}


