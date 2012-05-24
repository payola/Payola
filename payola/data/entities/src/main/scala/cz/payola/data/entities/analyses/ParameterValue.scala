package cz.payola.data.entities.analyses

import cz.payola.data.entities._
import org.squeryl.dsl.ManyToOne

trait ParameterValue[A] extends cz.payola.domain.entities.analyses.ParameterValue[A] with PersistableEntity
{
    val parameterId: Option[String]

    var pluginInstanceId: Option[String] = None

    override def parameter: ParameterType
}