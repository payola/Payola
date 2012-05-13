package cz.payola.data.entities.analyses

import cz.payola.data.entities.PayolaDB

trait Parameter[A] extends cz.payola.domain.entities.analyses.Parameter[A]
{
    var pluginId: Option[String] = None

    def instances: Seq[cz.payola.domain.entities.analyses.ParameterValue[A]]
}
