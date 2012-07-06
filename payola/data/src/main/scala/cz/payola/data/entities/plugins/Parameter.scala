package cz.payola.data.entities.plugins

import cz.payola.data.entities._
import cz.payola.data.entities.plugins.parameters._
import cz.payola.data.SquerylDataContextComponent

/**
  * This object converts [[cz.payola.common.entities.plugins.Parameter]] to proper parameter
  * in [[cz.payola.data.entities.plugins.parameters]] package.
  *
  */
object Parameter
{
    def apply(p: cz.payola.common.entities.plugins.Parameter[_])(implicit context: SquerylDataContextComponent) = {
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

    /**
      * @return Returns collection of associated [[cz.payola.data.entities.plugins.ParameterValues]]s.
      */
    def parameterValues: Seq[ParameterValue[A]]
}
