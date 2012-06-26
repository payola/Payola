package cz.payola.data.entities.analyses

import cz.payola.data.entities._
import cz.payola.data.entities.analyses.parameters._

object Parameter {
    def apply(p: cz.payola.domain.entities.plugins.Parameter[_]) = {
        p match {
            case b: BooleanParameter => b
            case f: FloatParameter => f
            case i: IntParameter => i
            case s: StringParameter => s
            case b: cz.payola.domain.entities.plugins.parameters.BooleanParameter => BooleanParameter(b)
            case f: cz.payola.domain.entities.plugins.parameters.FloatParameter => FloatParameter(f)
            case i: cz.payola.domain.entities.plugins.parameters.IntParameter => IntParameter(i)
            case s: cz.payola.domain.entities.plugins.parameters.StringParameter => StringParameter(s)
        }
    }
}

trait Parameter[A] extends cz.payola.domain.entities.plugins.Parameter[A] with PersistableEntity
{
    var pluginId: Option[String] = None

    def parameterValues: Seq[ParameterValue[A]]
}
