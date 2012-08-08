package cz.payola.data.squeryl.entities.plugins

import cz.payola.data.squeryl.entities._
import cz.payola.data.squeryl.entities.plugins.parameters._
import cz.payola.data.squeryl._

object Parameter
{
    def apply(p: cz.payola.common.entities.plugins.Parameter[_])(implicit context: SquerylDataContextComponent) = {
        p match {
            case b: cz.payola.common.entities.plugins.parameters.BooleanParameter => BooleanParameter(b)
            case f: cz.payola.common.entities.plugins.parameters.FloatParameter => FloatParameter(f)
            case i: cz.payola.common.entities.plugins.parameters.IntParameter => IntParameter(i)
            case s: cz.payola.common.entities.plugins.parameters.StringParameter => StringParameter(s)
        }
    }
}

/**
 * General parameter pro plugins
 * @tparam A Type of the parameter.
 */
trait Parameter[A] extends cz.payola.domain.entities.plugins.Parameter[A] with Entity
{
    /**
     * ID of plugin this parameter is assigned to
     */
    var pluginId: String = null
}
