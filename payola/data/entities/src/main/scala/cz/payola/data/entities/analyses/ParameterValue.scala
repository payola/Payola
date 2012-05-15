package cz.payola.data.entities.analyses

import cz.payola.data.entities.PayolaDB

trait ParameterValue[A] extends cz.payola.domain.entities.analyses.ParameterValue[A]
{
    val parameterId: Option[String] = if (parameter == null) None else Some(parameter.id)

    var pluginInstanceId: Option[String] = None
}
