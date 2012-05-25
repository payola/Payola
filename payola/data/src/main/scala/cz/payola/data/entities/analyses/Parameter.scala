package cz.payola.data.entities.analyses

import cz.payola.data.entities._

trait Parameter[A] extends cz.payola.domain.entities.analyses.Parameter[A] with PersistableEntity
{
    var pluginId: Option[String] = None

    def parameterValues: Seq[ParameterValue[A]]
}
