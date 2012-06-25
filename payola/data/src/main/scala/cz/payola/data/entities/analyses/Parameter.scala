package cz.payola.data.entities.analyses

import cz.payola.data.entities._

trait Parameter[A] extends cz.payola.domain.entities.plugins.Parameter[A] with PersistableEntity
{
    var pluginId: Option[String] = None

    //def registerParameterValue(p: ParameterValue[A])

    def parameterValues: Seq[ParameterValue[A]]
}
