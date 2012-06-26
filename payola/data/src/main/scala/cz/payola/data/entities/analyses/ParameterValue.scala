package cz.payola.data.entities.analyses

import cz.payola.data.entities._

object ParameterValue
{
    def apply() {


    }
}

trait ParameterValue[A] extends cz.payola.domain.entities.plugins.ParameterValue[A] with PersistableEntity
{
    val parameterId: Option[String]

    var pluginInstanceId: Option[String] = None

    var dataSourceId: Option[String] = None

    override def parameter: ParameterType
}
